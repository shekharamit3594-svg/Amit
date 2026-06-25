Feature: Performance Testing — PT-6 Stress Test (Push System Beyond Capacity)

  # ══════════════════════════════════════════════════════════════════════════════
  # HOW STRESS TESTING DIFFERS FROM LOAD TESTING
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # Load Test (pt_LoadTest.feature)          │  Stress Test (this file)
  # ─────────────────────────────────────────┼────────────────────────────────────
  # Traffic = EXPECTED normal load           │ Traffic = 3–10× BEYOND capacity
  # Think time: 1–3 seconds (realistic)      │ Think time: 0–100ms (maximum pressure)
  # SLA: strict (3s / 5s thresholds)         │ SLA: relaxed or NONE (degradation expected)
  # Error rate target: < 1 %                 │ Error rate target: < 20 % (some expected)
  # Goal: "Does it meet SLAs?"               │ Goal: "At what load does it BREAK?"
  # Fail fast on first SLA breach            │ Measure HOW it degrades, not just IF
  # Tests stable operation                   │ Tests resilience and recovery
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # WHAT WE LEARN FROM A STRESS TEST
  # ──────────────────────────────────────────────────────────────────────────────
  #   1. BREAKING POINT — the user count at which error rate spikes above threshold
  #   2. FAILURE MODE   — does it 429 (rate limit), 503 (server overload), or time out?
  #   3. RECOVERY       — after the spike ends, does latency return to normal?
  #   4. GRACEFUL DEGRADATION — does it shed load cleanly or cascade fail?
  #
  # HOW THIS FEATURE FILE ENCODES "STRESS" DIFFERENTLY
  # ──────────────────────────────────────────────────────────────────────────────
  #   • Think time = 50ms (nearly no pause → maximum request rate)
  #   • STRESS_SLA_MS = 10000 (10s — we accept degraded but not completely broken)
  #   • ACCEPTABLE_ERROR_PCT = 15 (15% error budget — some 429s/503s are expected)
  #   • responseStatus is captured before the `Then status` assertion so we can
  #     log errors without failing the scenario prematurely — stress tests run to
  #     completion even when the server starts returning errors.
  #   • SpikeTestSimulation.scala drives this with atOnceUsers(50) + baseline
  # ══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure retry      = { count: 0 }
    * configure connectTimeout = 5000
    # Shorter readTimeout for stress tests — we want to detect hangs quickly
    * configure readTimeout = 10000

    * def metrics = Java.type('performanceTesting.PerformanceMetricsHelper')

    # Stress SLA is intentionally relaxed — we're looking for total failure, not SLA breach
    * def STRESS_SLA_MS        = 10000  # 10 seconds — detect complete failure, not slow responses
    * def ACCEPTABLE_ERROR_PCT = 15     # up to 15% error rate is acceptable under extreme stress

    # Minimal think time — stress tests maximize request rate to push the server hard
    * def STRESS_THINK_MS = 50

    * def randomStr = function() { return Math.random().toString(36).substring(2, 10) }

  # ────────────────────────────────────────────────────────────────────────────
  # PT-6a: Stress Read — GET /users at maximum rate
  # Think time is 50ms instead of 1000ms.  With 50 virtual users each pausing
  # only 50ms, the server sees ~1000 req/s instead of ~50 req/s.
  # ────────────────────────────────────────────────────────────────────────────
  @StressTest @ReadStress @PT6
  Scenario: Stress test — GET /users at maximum request rate (minimal think time)

    # 50ms pause = high-frequency hammer, not realistic user pacing
    * karate.pause(STRESS_THINK_MS)
    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And param page = 1
    And header Authorization = bearerToken
    When method get

    # Capture response status BEFORE the Then assertion —
    # stress tests may receive 429/503; we record those as errors but continue
    * def httpStatus = responseStatus

    # Record success or error so the metrics helper tracks the real error rate
    * def elapsed = httpStatus < 400 ? metrics.recordSuccess(reqStart) : metrics.recordError(reqStart)
    * print '[PT-6a] GET /users | status:', httpStatus, '| elapsed:', elapsed, 'ms'

    # Under stress, 429 (Too Many Requests) and 503 (Overloaded) are EXPECTED.
    # We accept 2xx, 4xx, and 5xx here — the assertion is on the AGGREGATE error
    # rate in the report scenario, not on individual requests.
    * def isAcceptable = httpStatus == 200 || httpStatus == 429 || httpStatus == 503
    * assert isAcceptable

    # Even under stress, no single request should hang for more than 10 seconds
    * assert elapsed < STRESS_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-6b: Stress Write — POST /users at maximum rate
  # Write operations are more sensitive to stress because they acquire locks.
  # Under stress, this reveals:
  #   - DB connection pool exhaustion (500 errors when pool is full)
  #   - Write lock contention (slow response as requests queue up)
  #   - Rate limiting (429 when the API throttles writes)
  # ────────────────────────────────────────────────────────────────────────────
  @StressTest @WriteStress @PT6
  Scenario: Stress test — POST /users at maximum write rate (lock contention probe)

    * def suffix    = randomStr()
    * def userEmail = 'stress.' + suffix + '@perf.io'
    * def payload   = { name: 'Stress User', email: '#(userEmail)', gender: 'female', status: 'active' }

    # Minimal think time — writing as fast as the server can handle
    * karate.pause(STRESS_THINK_MS)
    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post

    * def httpStatus = responseStatus
    * def elapsed = httpStatus < 400 ? metrics.recordSuccess(reqStart) : metrics.recordError(reqStart)
    * print '[PT-6b] POST /users | status:', httpStatus, '| elapsed:', elapsed, 'ms'

    # Accept 201 (success), 422 (validation), 429 (rate limit), 503 (overloaded)
    * def isAcceptable = httpStatus == 201 || httpStatus == 422 || httpStatus == 429 || httpStatus == 503
    * assert isAcceptable
    * assert elapsed < STRESS_SLA_MS


  # ────────────────────────────────────────────────────────────────────────────
  # PT-6c: Stress Recovery Check
  # After running at maximum rate, slow back to normal pace and verify the
  # server has recovered.  A healthy server should return to baseline latency
  # within 1–2 requests.  Slow recovery signals:
  #   - Thread pool still draining
  #   - GC pressure from object churn during the spike
  #   - Connection pool not yet refilled
  # ────────────────────────────────────────────────────────────────────────────
  @StressTest @RecoveryCheck @PT6
  Scenario: Stress recovery — verify server returns to normal after spike (2s think time)

    # Long pause: let the server breathe before sending the recovery probe
    * karate.pause(2000)
    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And param page = 1
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-6c] Recovery probe | elapsed:', elapsed, 'ms'

    # Post-stress, a healthy server should respond within the NORMAL read SLA (3s)
    Then status 200
    * assert elapsed < 3000


  # ────────────────────────────────────────────────────────────────────────────
  # PT-6d: Stress Metrics Report
  # At acceptable_error_pct = 15, this scenario PASSES with up to 15% errors.
  # Compare this to pt_LoadTest.feature's report which requires < 1% errors.
  # That difference is the core distinction between load and stress testing.
  # ────────────────────────────────────────────────────────────────────────────
  @StressTestReport @PT6
  Scenario: Print stress test metrics — error rate must stay under acceptable threshold

    * def report = metrics.getMetrics()
    * print ''
    * print '╔══════════════════════════════════════════════════╗'
    * print '║        STRESS TEST METRICS SUMMARY (PT-6)        ║'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Total Requests  :', report.totalRequests
    * print '║  Errors          :', report.errors, '(', report.errorRatePct, '% error rate)'
    * print '║  THRESHOLD       : < ', ACCEPTABLE_ERROR_PCT, '% (stress allows more errors than load)'
    * print '║  Throughput      :', report.throughputPerSec, 'req/s'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Min RT          :', report.minMs, 'ms'
    * print '║  p50 RT          :', report.p50Ms, 'ms'
    * print '║  p95 RT          :', report.p95Ms, 'ms'
    * print '║  Max RT          :', report.maxMs, 'ms  ← ceiling under extreme stress'
    * print '╚══════════════════════════════════════════════════╝'
    # Stress test accepts higher error rates — only fails if server completely melts down
    * assert report.errorRatePct == null || report.errorRatePct < ACCEPTABLE_ERROR_PCT
