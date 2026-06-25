Feature: Performance Testing — PT-5 Load Test (Normal Traffic with SLA Validation)

  # ══════════════════════════════════════════════════════════════════════════════
  # WHAT MAKES A LOAD TEST DIFFERENT FROM A REGULAR API TEST
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # Regular API Test (pt_GetUsers.feature)   │  Load Test (this file)
  # ─────────────────────────────────────────┼────────────────────────────────────
  # Goal: "Does it return CORRECT data?"     │ Goal: "Is it fast ENOUGH under load?"
  # 1 thread, runs once                      │ N virtual users running concurrently
  # No timing — fast or slow, doesn't matter │ Response time measured per request
  # No pauses between steps                  │ Think time (karate.pause) between steps
  # Assert: status 200, match response body  │ Assert: elapsed < SLA_MS
  # Retry on failure (3 retries)             │ No retry — every failure counts
  # Passes if API is eventually correct      │ Passes if API is ALWAYS fast AND correct
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # THREE PERFORMANCE-SPECIFIC CONSTRUCTS added to every scenario here:
  #
  #   1. karate.pause(ms)
  #      Simulates real user pacing. Without it, 50 virtual users fire
  #      requests at machine speed → 50,000 req/s → unrealistic load.
  #      Think time of 1–3 s models users reading a page before clicking next.
  #
  #   2. metrics.startRequest() → elapsed → metrics.recordSuccess()
  #      Captures wall-clock time around the HTTP call. The Java helper
  #      aggregates these across all threads for p50/p95/p99 reporting.
  #
  #   3. assert elapsed < READ_SLA_MS
  #      THE defining assertion of a performance test. The same GET that
  #      returns correct data in 8000ms would PASS a regular test but FAIL
  #      a load test because 8s violates the SLA.
  #
  # Under LoadTestSimulation.scala (50 virtual users), these scenarios run
  # hundreds of times. Gatling aggregates their latencies into p50/p95/p99
  # charts. PerformanceMetricsHelper does the same inside the JUnit runner.
  # ══════════════════════════════════════════════════════════════════════════════

  Background:
    # No retry — performance tests must count real failures, not hide them
    * configure retry = { count: 0 }
    * configure connectTimeout = 5000
    * configure readTimeout = 15000

    # Java helper: thread-safe response-time recorder (shared across all virtual users)
    * def metrics = Java.type('performanceTesting.PerformanceMetricsHelper')

    # SLA thresholds — define once, assert everywhere
    # Read endpoints are faster than writes because they only touch the DB read path
    * def READ_SLA_MS  = 3000   # GET  operations: 3-second SLA
    * def WRITE_SLA_MS = 5000   # POST operations: 5-second SLA

    # Think time — how long a real user pauses between requests
    # 1 second models a user glancing at a result before clicking the next action
    * def THINK_TIME_MS = 1000

    # Unique suffix per virtual user / iteration — prevents email key conflicts
    * def randomStr = function() { return Math.random().toString(36).substring(2, 10) }

  # ────────────────────────────────────────────────────────────────────────────
  # PT-5a: Read Load — GET /users with think time + SLA assertion
  #
  # SIDE-BY-SIDE with the equivalent regular API test:
  #
  # REGULAR (pt_GetUsers.feature @GetAllUsers):          LOAD TEST (this scenario):
  #  Given url baseUrl                                    * karate.pause(1000)           ← added
  #  And path usersPath                                   * def start = metrics.start()  ← added
  #  And param page = 1                                   Given url baseUrl
  #  And header Authorization = bearerToken               And path usersPath
  #  When method get                                      And param page = 1
  #  Then status 200                                      And header Authorization = bearerToken
  #  And match response == '#array'                       When method get
  #                                                       * def elapsed = metrics.record() ← added
  #                                                       Then status 200
  #                                                       And match response == '#array'
  #                                                       * assert elapsed < 3000          ← added
  # ────────────────────────────────────────────────────────────────────────────
  @LoadTest @ReadLoad @PT5
  Scenario: Load test — GET /users  (think time + response-time SLA)

    # STEP 1: Think time — virtual user "reads the previous page" before requesting the next
    * karate.pause(THINK_TIME_MS)

    # STEP 2: Capture start time BEFORE the HTTP call begins
    * def reqStart = metrics.startRequest()

    # STEP 3: The HTTP call itself — identical to any regular API test
    Given url baseUrl
    And path usersPath
    And param page = 1
    And param per_page = 10
    And header Authorization = bearerToken
    When method get

    # STEP 4: Record elapsed time and receive it back for assertion
    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-5a] GET /users | elapsed:', elapsed, 'ms | SLA:', READ_SLA_MS, 'ms'

    # STEP 5: Correctness assertions (same as regular API test)
    Then status 200
    And match response == '#array'
    And assert response.length > 0

    # STEP 6: SLA assertion — THIS is what makes it a performance test
    # The scenario passes only if BOTH correctness AND timing are satisfied
    * assert elapsed < READ_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-5b: Read Load — GET /users filtered by status
  # Filtered queries may use a different DB path (index scan vs full scan).
  # Load testing it separately lets you set a different SLA for filtered reads.
  # ────────────────────────────────────────────────────────────────────────────
  @LoadTest @ReadLoad @PT5
  Scenario: Load test — GET /users?status=active  (filter under load)

    * karate.pause(THINK_TIME_MS)
    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And param status = 'active'
    And param per_page = 10
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-5b] GET /users?status=active | elapsed:', elapsed, 'ms'

    Then status 200
    And match response == '#array'
    And match each response contains { status: 'active' }
    * assert elapsed < READ_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-5c: Write Load — POST /users with think time and WRITE SLA
  # Write SLA is more relaxed (5s vs 3s) because:
  #   - Writes involve a DB INSERT + index update
  #   - Writes may acquire row-level locks, increasing wait time under concurrency
  #   - Network round-trip is identical but server processing is heavier
  # ────────────────────────────────────────────────────────────────────────────
  @LoadTest @WriteLoad @PT5
  Scenario: Load test — POST /users  (write SLA, unique payload per virtual user)

    # Each virtual user generates a unique email — prevents 422 collisions under concurrency
    * def suffix    = randomStr()
    * def userEmail = 'loadtest.' + suffix + '@perf.io'
    * def payload   = { name: 'Load Test User', email: '#(userEmail)', gender: 'male', status: 'active' }

    # Think time: simulates user filling a form before submitting
    * karate.pause(THINK_TIME_MS)
    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post

    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-5c] POST /users | elapsed:', elapsed, 'ms | SLA:', WRITE_SLA_MS, 'ms'

    Then status 201
    And match response.id    == '#number'
    And match response.email == userEmail
    # Write SLA is 5s (vs 3s for reads) — explicitly documented here
    * assert elapsed < WRITE_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-5d: Metrics Report — aggregated across ALL virtual users
  # Run this AFTER the other scenarios complete.
  # In Karate's parallel runner, the last scenario to finish prints the summary.
  # In Gatling, you use the HTML report instead (Gatling collects its own stats).
  # ────────────────────────────────────────────────────────────────────────────
  @LoadTestReport @PT5
  Scenario: Print aggregated load test metrics (p50 / p95 / p99 / throughput)

    * def report = metrics.getMetrics()
    * print ''
    * print '╔══════════════════════════════════════════════════╗'
    * print '║         LOAD TEST METRICS SUMMARY (PT-5)         ║'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Total Requests  :', report.totalRequests
    * print '║  Errors          :', report.errors, '(', report.errorRatePct, '% error rate)'
    * print '║  Throughput      :', report.throughputPerSec, 'req/s'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Min RT          :', report.minMs, 'ms'
    * print '║  Mean RT         :', report.meanMs, 'ms'
    * print '║  p50 RT          :', report.p50Ms, 'ms   ← median'
    * print '║  p90 RT          :', report.p90Ms, 'ms'
    * print '║  p95 RT          :', report.p95Ms, 'ms   ← SLA boundary'
    * print '║  p99 RT          :', report.p99Ms, 'ms'
    * print '║  Max RT          :', report.maxMs, 'ms'
    * print '╚══════════════════════════════════════════════════╝'
    # p95 must satisfy the read SLA — if 5 % of requests exceeded it, the test fails
    * assert report.p95Ms == null || report.p95Ms < READ_SLA_MS
