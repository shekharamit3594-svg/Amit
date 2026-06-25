Feature: Performance Testing — PT-1 Health Check / Warm-Up Probe

  # ═══════════════════════════════════════════════════════════════════════════════
  # MODULE PT-1 : Health Check / Warm-Up Probe
  #
  # Purpose
  #   A minimal single-step GET used to:
  #     1) Warm up server-side connection pools BEFORE heavier load scenarios run
  #     2) Establish a baseline "zero load" latency measurement
  #     3) Confirm the endpoint is reachable and the token is valid
  #
  # Why no retry?
  #   Performance tests must NEVER retry on failure — retries artificially inflate
  #   response times and mask the true error rate the system experiences under load.
  #   Global karate-config.js sets retry: 3, so we override it to 0 here.
  #
  # Used by
  #   BaselineSimulation.scala  — atOnceUsers(10)  warm-up + baseline assertion
  #   PerformanceRunner.java    — parallelGetUsers() (10 threads)
  # ═══════════════════════════════════════════════════════════════════════════════

  Background:
    # Override the global retry policy — zero retries for all perf feature files
    * configure retry = { count: 0 }
    * configure connectTimeout = 5000
    * configure readTimeout = 15000

  @HealthCheck @WarmUp @PT1
  Scenario: GET /users — baseline latency probe (single request, minimal assertions)

    # The least possible work: one GET, one status check, one type assertion.
    # Keeping this lean ensures the measurement reflects network + server time,
    # not Karate's own assertion overhead.
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    When method get
    Then status 200
    # Only verify structure — not content — to keep assertion cost near zero
    And match response == '#array'
    And assert response.length > 0
    * print '[PT-1] Health check passed — endpoint is reachable'
