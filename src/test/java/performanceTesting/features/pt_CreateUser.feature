Feature: Performance Testing — PT-3 Write Load (POST /users)

  # ═══════════════════════════════════════════════════════════════════════════════
  # MODULE PT-3 : Write Load Test
  #
  # Purpose
  #   Simulates concurrent write traffic — each virtual Gatling user executes
  #   a POST /users request with a UNIQUE email address per iteration.
  #
  # Key design rule for write load tests
  #   EVERY concurrent thread must generate a unique payload.
  #   If two virtual users send the same email, the API returns 422 (conflict)
  #   and your error rate is inflated by the test design, not the server.
  #   → Use Math.random() inside a Karate JS function to generate unique values.
  #
  # Scenarios
  #   @CreateUser        — create an active user (primary write scenario)
  #   @CreateInactiveUser — create an inactive user (variant for mixed traffic)
  #
  # Used by
  #   SpikeTestSimulation.scala (@CreateUser) — 5 users burst to 50 users
  #   SoakTestSimulation.scala  (@CreateUser) — sustained write over 5 min
  #   PerformanceRunner.java    parallelCreateUsers() (5 threads)
  # ═══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure retry = { count: 0 }
    * configure connectTimeout = 5000
    * configure readTimeout = 15000
    # randomStr() is evaluated freshly on EVERY scenario execution.
    # Because Karate evaluates Background before each Scenario, every virtual
    # user (and every iteration) gets a different random suffix — no collisions.
    * def randomStr =
      """
      function() {
        return Math.random().toString(36).substring(2, 10);
      }
      """

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-3a: Create an active user
  # Primary write scenario.  The email is built from a random string so that
  # 50 concurrent Gatling users can all POST simultaneously without conflict.
  # ─────────────────────────────────────────────────────────────────────────────
  @CreateUser @WriteLoad @PT3
  Scenario: POST /users — create a unique active user (write load)

    # Build a unique payload for this virtual user / iteration
    * def suffix    = randomStr()
    * def userEmail = 'perf.active.' + suffix + '@loadtest.io'
    * def payload   = { name: 'LoadTest Active User', email: '#(userEmail)', gender: 'male', status: 'active' }

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post
    Then status 201

    # Verify the created resource reflects what was sent
    And match response.email  == userEmail
    And match response.status == 'active'
    And match response.id     == '#number'

    # Capture the created userId — useful when chaining into a delete/cleanup step
    * def createdUserId = response.id
    * print '[PT-3a] Created active user — ID:', createdUserId, 'email:', userEmail

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-3b: Create an inactive user
  # A payload variant used to simulate realistic mixed write traffic:
  # not all users are created active.  Running this alongside PT-3a in a
  # Gatling simulation shows whether the server handles mixed statuses correctly
  # under concurrent writes.
  # ─────────────────────────────────────────────────────────────────────────────
  @CreateInactiveUser @WriteLoad @PT3
  Scenario: POST /users — create a unique inactive user (write load variant)

    * def suffix    = randomStr()
    * def userEmail = 'perf.inactive.' + suffix + '@loadtest.io'
    * def payload   = { name: 'LoadTest Inactive User', email: '#(userEmail)', gender: 'female', status: 'inactive' }

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post
    Then status 201
    And match response.status == 'inactive'
    And match response.id     == '#number'
    * def createdUserId = response.id
    * print '[PT-3b] Created inactive user — ID:', createdUserId, 'email:', userEmail
