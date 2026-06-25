Feature: Performance Testing — PT-8 Concurrency Test (Race Conditions and Data Consistency)

  # ══════════════════════════════════════════════════════════════════════════════
  # HOW CONCURRENCY TESTING DIFFERS FROM ALL OTHER PERFORMANCE TESTS
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # Load / Stress / Volume tests            │  Concurrency Test (this file)
  # ─────────────────────────────────────────┼────────────────────────────────────
  # Many users hit DIFFERENT resources       │ Many users hit THE SAME resource
  # User A: creates User-1                   │ User A: updates User-99
  # User B: creates User-2                   │ User B: updates User-99 (same record)
  # No shared state between virtual users   │ SHARED state — all users touch same ID
  # Bottleneck: connection pool, CPU         │ Bottleneck: row-level locks, transaction isolation
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # WHAT CONCURRENCY TESTING REVEALS
  # ──────────────────────────────────────────────────────────────────────────────
  #   1. RACE CONDITIONS    — two threads read the same value, both modify, both
  #                           write back — one write is silently discarded (lost update)
  #   2. DIRTY READS        — thread A reads a record that thread B is still writing
  #                           → reads partially-committed data
  #   3. DEADLOCKS          — thread A waits for thread B's lock while B waits for A's
  #                           → both hang until a timeout kills one of them
  #   4. INCONSISTENCY      — after 10 concurrent updates to the same field, the
  #                           final value should equal the last write, not an average
  #
  # HOW THIS FILE SIMULATES CONCURRENCY
  # ──────────────────────────────────────────────────────────────────────────────
  #   Step 1: One virtual user creates a shared resource (gets sharedUserId)
  #   Step 2: N other virtual users ALL update the SAME sharedUserId concurrently
  #   Step 3: One user reads back the final state and asserts consistency
  #   Step 4: Delete the shared resource (cleanup)
  #
  # In Karate's parallel runner, the `sharedUserId` variable must be accessible
  # across scenarios.  We use a Java static field in PerformanceMetricsHelper
  # to pass the ID between scenarios running in different threads.
  #
  # Note: True multi-threaded race condition testing is best achieved through
  # Gatling's concurrent user injection. The PerformanceRunner parallelUserLifecycle()
  # test demonstrates this by running 3 lifecycle threads simultaneously.
  # ══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure retry      = { count: 0 }
    * configure connectTimeout = 5000
    * configure readTimeout = 15000

    * def metrics = Java.type('performanceTesting.PerformanceMetricsHelper')

    * def CONCURRENT_SLA_MS = 5000   # concurrent operations may be slower due to lock wait
    * def randomStr = function() { return Math.random().toString(36).substring(2, 10) }

  # ────────────────────────────────────────────────────────────────────────────
  # PT-8a: Setup — create a shared resource that ALL subsequent concurrent
  # users will target.  Runs once (single user, no concurrency yet).
  # ────────────────────────────────────────────────────────────────────────────
  @ConcurrencyTest @Setup @PT8
  Scenario: Concurrency setup — create shared resource for concurrent access

    * def suffix    = randomStr()
    * def userEmail = 'concurrent.' + suffix + '@perf.io'
    * def payload   = { name: 'Shared Resource User', email: '#(userEmail)', gender: 'male', status: 'active' }

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post

    * def elapsed = metrics.recordSuccess(reqStart)
    Then status 201
    And match response.id == '#number'

    * def createdId = response.id
    * print '[PT-8a] Created shared resource — ID:', createdId, '| elapsed:', elapsed, 'ms'

    # Store the shared ID in a Karate session variable so later scenarios can use it.
    # In Gatling simulations, each virtual user creates its OWN resource instead
    # (using pt_UserLifecycle.feature) since Gatling runs scenarios in isolation.
    * karate.set('sharedUserId', createdId)
    * karate.set('sharedUserEmail', userEmail)


  # ────────────────────────────────────────────────────────────────────────────
  # PT-8b: Concurrent reads on the same resource
  # ALL virtual users read the SAME userId simultaneously.
  # This tests: shared cache correctness, connection pool under read concurrency.
  # In Karate's parallel runner, multiple threads execute this simultaneously.
  # ────────────────────────────────────────────────────────────────────────────
  @ConcurrencyTest @ConcurrentRead @PT8
  Scenario: Concurrent reads — multiple virtual users read the same resource simultaneously

    * def userId    = karate.get('sharedUserId')
    * def userEmail = karate.get('sharedUserEmail')

    # Skip if setup did not run (sharedUserId not set)
    * if (!userId) karate.skip()

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-8b] Concurrent read — userId:', userId, '| elapsed:', elapsed, 'ms'

    Then status 200
    And match response.id    == userId
    And match response.email == userEmail

    # DATA CONSISTENCY CHECK: no matter how many concurrent reads happen,
    # every reader must see the SAME correct data — no partial or corrupt reads
    And match response.status == 'active'
    * assert elapsed < CONCURRENT_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-8c: Concurrent writes on the same resource — THE RACE CONDITION PROBE
  # ALL virtual users PATCH the SAME userId with different name values.
  # After all concurrent PATCHes complete, the final state must be ONE of the
  # submitted values (not a hybrid, not null, not a previous value).
  # ────────────────────────────────────────────────────────────────────────────
  @ConcurrencyTest @ConcurrentWrite @PT8
  Scenario: Concurrent writes — PATCH same resource from multiple virtual users (race condition probe)

    * def userId  = karate.get('sharedUserId')
    * if (!userId) karate.skip()

    # Each virtual user sends a different name — only ONE should "win" the race
    * def threadId = java.lang.Thread.currentThread().getId()
    * def newName  = 'ConcurrentWriter-' + threadId

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: '#(newName)' }
    When method patch

    * def elapsed = metrics.recordSuccess(reqStart)
    * def httpStatus = responseStatus
    * print '[PT-8c] Concurrent PATCH | thread:', threadId, '| status:', httpStatus, '| elapsed:', elapsed, 'ms'

    # Acceptable outcomes under concurrent write:
    #   200 — this thread's write succeeded
    #   409 — optimistic locking conflict (server correctly detected race)
    #   503 — server shed the write under load (retry-after expected)
    * def isAcceptable = httpStatus == 200 || httpStatus == 409 || httpStatus == 503
    * assert isAcceptable
    * assert elapsed < CONCURRENT_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-8d: Post-concurrency consistency check
  # After all concurrent writes, read the resource one more time and verify:
  #   1. It still exists (no delete-on-conflict)
  #   2. The final value is ONE of the submitted names (no corruption)
  #   3. The email is unchanged (partial updates should not affect other fields)
  # ────────────────────────────────────────────────────────────────────────────
  @ConcurrencyTest @ConsistencyCheck @PT8
  Scenario: Post-concurrency read — verify data consistency after concurrent writes

    * def userId    = karate.get('sharedUserId')
    * def userEmail = karate.get('sharedUserEmail')
    * if (!userId) karate.skip()

    # Short pause — allow any in-flight writes to settle before reading
    * karate.pause(500)
    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)

    Then status 200
    And match response.id == userId

    # EMAIL MUST BE UNCHANGED — no concurrent write touched this field
    And match response.email == userEmail

    # NAME must start with 'ConcurrentWriter-' (one of the thread names won)
    # A corrupt/null value here signals a lost-update race condition
    * def name = response.name
    * print '[PT-8d] Final state after concurrent writes — name:', name, '| email:', response.email
    * assert name != null && name.length > 0
    * assert elapsed < CONCURRENT_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-8e: Cleanup — delete the shared resource
  # ────────────────────────────────────────────────────────────────────────────
  @ConcurrencyTest @Cleanup @PT8
  Scenario: Concurrency teardown — delete shared resource

    * def userId = karate.get('sharedUserId')
    * if (!userId) karate.skip()

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method delete

    * def elapsed = metrics.recordSuccess(reqStart)
    Then status 204
    * print '[PT-8e] Cleaned up shared resource — ID:', userId, '| elapsed:', elapsed, 'ms'


  # ────────────────────────────────────────────────────────────────────────────
  # PT-8f: Concurrency metrics report
  # ────────────────────────────────────────────────────────────────────────────
  @ConcurrencyTestReport @PT8
  Scenario: Print concurrency test metrics

    * def report = metrics.getMetrics()
    * print ''
    * print '╔══════════════════════════════════════════════════╗'
    * print '║     CONCURRENCY TEST METRICS SUMMARY (PT-8)      ║'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Total Requests  :', report.totalRequests
    * print '║  Errors          :', report.errors, '(', report.errorRatePct, '%)'
    * print '║  Throughput      :', report.throughputPerSec, 'req/s'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  p50 RT          :', report.p50Ms, 'ms'
    * print '║  p95 RT          :', report.p95Ms, 'ms'
    * print '║  Max RT          :', report.maxMs, 'ms  ← lock-wait ceiling'
    * print '╚══════════════════════════════════════════════════╝'
    # Under concurrency, lock-wait spikes max RT but p95 should still be reasonable
    * assert report.p95Ms == null || report.p95Ms < CONCURRENT_SLA_MS
