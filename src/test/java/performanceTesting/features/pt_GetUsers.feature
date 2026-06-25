Feature: Performance Testing — PT-2 Read Load (GET /users)

  # ═══════════════════════════════════════════════════════════════════════════════
  # MODULE PT-2 : Read-Heavy Load Test
  #
  # Purpose
  #   Simulates concurrent read traffic against the /users endpoint.
  #   Read operations are the most frequent in most systems; they should be the
  #   FIRST thing you load test before touching write paths.
  #
  # Scenarios
  #   @GetAllUsers  — plain paginated GET, page 1.  Used in ramp-up simulations.
  #   @GetByStatus  — filtered GET (status=active).  Tests query param handling.
  #   @GetByPage    — data-driven across pages 1-3.  Tests pagination behaviour.
  #
  # Used by
  #   RampUpSimulation.scala    (@GetAllUsers)  — gradual ramp 1→30 users
  #   StepUpLoadSimulation.scala (@GetAllUsers) — step-up 5→25 users/sec
  #   PerformanceRunner.java    parallelGetUsers() (10 threads)
  # ═══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure retry = { count: 0 }
    * configure connectTimeout = 5000
    * configure readTimeout = 15000

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-2a: Plain paginated GET — page 1
  # Simplest possible read.  Used as the workhorse in ramp-up simulations because
  # it requires zero setup and produces deterministic response shapes.
  # ─────────────────────────────────────────────────────────────────────────────
  @GetAllUsers @ReadLoad @PT2
  Scenario: GET /users — page 1, no filter (baseline read scenario)

    Given url baseUrl
    And path usersPath
    And param page = 1
    And header Authorization = bearerToken
    When method get
    Then status 200
    And match response == '#array'
    And assert response.length > 0
    * print '[PT-2a] GET /users page 1 — returned', response.length, 'records'

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-2b: Filtered GET — query parameter load
  # Tests the server's query-param parsing and index/filter execution under load.
  # Every returned record must satisfy the filter — any mismatch is a server bug
  # that only surfaces under concurrent access.
  # ─────────────────────────────────────────────────────────────────────────────
  @GetByStatus @ReadLoad @PT2
  Scenario: GET /users — filter by status=active (query param under concurrent load)

    Given url baseUrl
    And path usersPath
    And param status = 'active'
    And param per_page = 10
    And header Authorization = bearerToken
    When method get
    Then status 200
    And match response == '#array'
    # Correctness assertion: ALL returned users must have status=active.
    # If this fails under load it reveals a race condition in server-side filtering.
    And match each response contains { status: 'active' }
    * print '[PT-2b] Filter by status=active returned', response.length, 'records'

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-2c: Data-driven pagination — pages 1, 2, 3
  # Scenario Outline rows run as separate iterations in Gatling; each virtual user
  # cycles through all three rows, testing that the server handles different
  # offset/page combos correctly under concurrent access.
  # ─────────────────────────────────────────────────────────────────────────────
  @GetByPage @ReadLoad @PT2
  Scenario Outline: GET /users — paginated read, page <page> (data-driven)

    Given url baseUrl
    And path usersPath
    And param page = <page>
    And param per_page = 5
    And header Authorization = bearerToken
    When method get
    Then status 200
    And match response == '#array'
    * print '[PT-2c] Page', <page>, '— returned', response.length, 'records'

    Examples:
      | page |
      | 1    |
      | 2    |
      | 3    |
