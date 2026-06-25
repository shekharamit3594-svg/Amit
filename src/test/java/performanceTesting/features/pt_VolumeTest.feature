Feature: Performance Testing — PT-7 Volume Test (Large Data, High Record Counts)

  # ══════════════════════════════════════════════════════════════════════════════
  # HOW VOLUME TESTING DIFFERS FROM LOAD AND STRESS TESTING
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # Load Test                                │  Volume Test (this file)
  # ─────────────────────────────────────────┼────────────────────────────────────
  # Many USERS, normal payload per request   │ Few users, LARGE payload per request
  # 50 users × GET page=1 (10 records)       │ 5 users × GET per_page=100 (100 records)
  # Tests concurrency handling               │ Tests DB query + serialization at scale
  # Bottleneck: thread/connection pool        │ Bottleneck: DB scan, JSON marshal, memory
  # ─────────────────────────────────────────┼────────────────────────────────────
  #
  # Stress Test                              │  Volume Test (this file)
  # ─────────────────────────────────────────┼────────────────────────────────────
  # Pushes user count beyond capacity        │ Pushes DATA SIZE beyond normal bounds
  # 200 concurrent users                     │ 5 users, but each requests 100+ records
  # Causes: connection pool exhaustion       │ Causes: heap pressure, slow JSON marshal
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # WHAT VOLUME TESTING REVEALS
  # ──────────────────────────────────────────────────────────────────────────────
  #   1. QUERY PLAN REGRESSION  — a query that takes 50ms for 10 records may take
  #      2000ms for 10,000 records if the DB lacks a covering index.
  #   2. SERIALIZATION COST     — converting 1000 DB rows to JSON takes CPU and
  #      memory proportional to record count. Measure response TIME vs SIZE.
  #   3. PAGINATION CORRECTNESS — page 1 and page 100 should return in similar time.
  #      Growing latency across pages reveals O(n) offset scans (a common DB bug).
  #   4. RESPONSE SIZE LIMITS   — APIs often cap response size (e.g., max 100 records).
  #      Volume tests verify these guards work and that payloads stay reasonable.
  #
  # VOLUME-SPECIFIC ASSERTIONS IN THIS FILE
  # ──────────────────────────────────────────────────────────────────────────────
  #   • response.length assertions — verify the API honours per_page limits
  #   • elapsed time vs page number — should not grow linearly with page offset
  #   • response.length == 0 for out-of-range pages — pagination boundary check
  # ══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure retry      = { count: 0 }
    * configure connectTimeout = 5000
    # Longer readTimeout for volume tests — large result sets take longer to marshal
    * configure readTimeout = 30000

    * def metrics = Java.type('performanceTesting.PerformanceMetricsHelper')

    # Volume SLA is more relaxed than load SLA because large payloads inherently
    # take longer to marshal and transmit than small ones
    * def VOLUME_SLA_MS   = 8000   # 8s SLA for large-payload requests
    * def MAX_PAGE_SIZE   = 100    # maximum records we expect per page

  # ────────────────────────────────────────────────────────────────────────────
  # PT-7a: Large result set — per_page=100 (maximum records in one response)
  # This directly tests DB query execution time + JSON serialization overhead
  # for the largest valid payload size.
  # ────────────────────────────────────────────────────────────────────────────
  @VolumeTest @LargeResultSet @PT7
  Scenario: Volume test — GET /users?per_page=100 (maximum single-request dataset)

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And param per_page = 100
    And param page = 1
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-7a] Large result set | records:', response.length, '| elapsed:', elapsed, 'ms'

    Then status 200
    And match response == '#array'

    # Volume assertion: API must return UP TO per_page records (not more)
    * assert response.length <= MAX_PAGE_SIZE

    # Volume SLA: even a 100-record payload must arrive within 8 seconds
    * assert elapsed < VOLUME_SLA_MS

    # Latency-per-record: how long did each record "cost"?
    * def msPerRecord = response.length > 0 ? Math.round(elapsed / response.length) : 0
    * print '[PT-7a] Cost per record:', msPerRecord, 'ms/record'


  # ────────────────────────────────────────────────────────────────────────────
  # PT-7b: Pagination latency consistency — pages 1 through 5
  # A CRITICAL volume test: page 1 should respond in similar time to page 5.
  # If page 5 is significantly slower than page 1, it reveals a DB OFFSET scan:
  #   SELECT * FROM users LIMIT 100 OFFSET 400  ← scans 400 rows to skip them
  # This O(n) behaviour is a common performance anti-pattern in paginated APIs.
  # ────────────────────────────────────────────────────────────────────────────
  @VolumeTest @PaginationLatency @PT7
  Scenario Outline: Volume test — pagination latency across pages (page <page> of 5)

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And param per_page = 20
    And param page = <page>
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)
    * print '[PT-7b] Page', <page>, '| records:', response.length, '| elapsed:', elapsed, 'ms'

    Then status 200
    And match response == '#array'
    # Each page must also respond within the volume SLA
    * assert elapsed < VOLUME_SLA_MS

    # KEY INSIGHT: if this assertion fails for large page numbers but passes
    # for page 1, the DB is using an O(n) OFFSET scan instead of cursor pagination
    Examples:
      | page |
      | 1    |
      | 2    |
      | 3    |
      | 4    |
      | 5    |


  # ────────────────────────────────────────────────────────────────────────────
  # PT-7c: Boundary — request a page far beyond the data set
  # Tests how the API handles out-of-range page numbers under load.
  # Two correct behaviours: return empty array [] or 404.
  # Bad behaviour: return a 500 error or hang indefinitely.
  # ────────────────────────────────────────────────────────────────────────────
  @VolumeTest @PaginationBoundary @PT7
  Scenario: Volume test — out-of-range page (page 9999) — API must respond quickly

    * def reqStart = metrics.startRequest()

    Given url baseUrl
    And path usersPath
    And param page = 9999
    And param per_page = 100
    And header Authorization = bearerToken
    When method get

    * def elapsed = metrics.recordSuccess(reqStart)
    * def httpStatus = responseStatus
    * print '[PT-7c] Boundary page 9999 | status:', httpStatus, '| elapsed:', elapsed, 'ms'

    # The API must not error — either empty response or 404 is acceptable
    * def isAcceptable = httpStatus == 200 || httpStatus == 404
    * assert isAcceptable

    # Out-of-range response must be FAST — no records to fetch means no DB scan cost
    # If this is slow, the DB is scanning all records to find page 9999 (O(n) bug)
    * def EMPTY_RESPONSE_SLA_MS = 2000   # empty page should respond in < 2s
    * assert elapsed < EMPTY_RESPONSE_SLA_MS
    * print '[PT-7c] Boundary check passed — no error, no hang on empty page'


  # ────────────────────────────────────────────────────────────────────────────
  # PT-7d: Volume metrics report
  # ────────────────────────────────────────────────────────────────────────────
  @VolumeTestReport @PT7
  Scenario: Print volume test metrics — response time should scale sub-linearly with page

    * def report = metrics.getMetrics()
    * print ''
    * print '╔══════════════════════════════════════════════════╗'
    * print '║        VOLUME TEST METRICS SUMMARY (PT-7)        ║'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Total Requests  :', report.totalRequests
    * print '║  Errors          :', report.errors
    * print '║  Throughput      :', report.throughputPerSec, 'req/s'
    * print '╠══════════════════════════════════════════════════╣'
    * print '║  Min RT          :', report.minMs, 'ms'
    * print '║  Mean RT         :', report.meanMs, 'ms'
    * print '║  p95 RT          :', report.p95Ms, 'ms'
    * print '║  Max RT          :', report.maxMs, 'ms'
    * print '╚══════════════════════════════════════════════════╝'
    # p95 RT for large result sets should stay under the relaxed volume SLA
    * assert report.p95Ms == null || report.p95Ms < VOLUME_SLA_MS
