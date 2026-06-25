Feature: Performance Testing — PT-4 Full CRUD Lifecycle (Transaction Throughput)

  # ═══════════════════════════════════════════════════════════════════════════════
  # MODULE PT-4 : Transaction Throughput — CRUD Chain
  #
  # Purpose
  #   Each virtual user executes a complete Create → Read → Update → Delete cycle.
  #   This is the most realistic simulation of real user behaviour because it:
  #     - Exercises all four HTTP verbs in a single transaction
  #     - Measures end-to-end transaction throughput (not just per-endpoint latency)
  #     - Self-cleans after each iteration (DELETE prevents data accumulation on
  #       long-running soak tests — critical for keeping the DB size stable)
  #
  # Why CRUD chains matter for performance testing
  #   A GET endpoint might respond in 200ms in isolation.  Under concurrent CRUD
  #   load — while 20 users are writing simultaneously — that same GET might jump
  #   to 800ms due to lock contention or index invalidation.  This scenario
  #   reveals that kind of cross-operation interference.
  #
  # Step sequence per virtual user
  #   Step 1  POST   /users        → creates user, captures userId
  #   Step 2  GET    /users/:id    → reads back, verifies data integrity
  #   Step 3  PATCH  /users/:id    → updates status to 'inactive'
  #   Step 4  DELETE /users/:id    → cleans up (essential for soak tests)
  #
  # Used by
  #   SoakTestSimulation.scala  (@UserLifecycle) — 20 users × 5 minutes
  #   PerformanceRunner.java    parallelUserLifecycle() (3 threads)
  # ═══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure retry = { count: 0 }
    * configure connectTimeout = 5000
    * configure readTimeout = 15000
    * def randomStr =
      """
      function() {
        return Math.random().toString(36).substring(2, 10);
      }
      """

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-4a: Full CRUD lifecycle in a single scenario
  # Each Gatling virtual user runs this scenario from start to finish.
  # The four HTTP steps chain via the `userId` variable captured in Step 1.
  # ─────────────────────────────────────────────────────────────────────────────
  @UserLifecycle @CRUD @Throughput @PT4
  Scenario: Full CRUD lifecycle — Create → Read → Update → Delete

    # ── Step 1 : CREATE ─────────────────────────────────────────────────────────
    # Build a unique payload so concurrent users never collide on email uniqueness.
    * def suffix    = randomStr()
    * def userEmail = 'lifecycle.' + suffix + '@loadtest.io'
    * def createPayload = { name: 'Lifecycle User', email: '#(userEmail)', gender: 'male', status: 'active' }

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request createPayload
    When method post
    Then status 201
    And match response.id    == '#number'
    And match response.email == userEmail
    # Capture the server-assigned ID — all subsequent steps depend on this value
    * def userId = response.id
    * print '[PT-4 Step 1] Created userId:', userId

    # ── Step 2 : READ ───────────────────────────────────────────────────────────
    # Immediately read back the resource to verify it was persisted correctly.
    # Under high write concurrency, databases can sometimes return stale reads.
    # This step would expose such anomalies.
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get
    Then status 200
    And match response.id     == userId
    And match response.email  == userEmail
    And match response.status == 'active'
    * print '[PT-4 Step 2] Read-back verified for userId:', userId

    # ── Step 3 : UPDATE (PATCH) ─────────────────────────────────────────────────
    # Partially update the resource — change status from active → inactive.
    # PATCH is preferred over PUT here because we only want to modify one field
    # (least-surprise principle, avoids accidentally overwriting other fields).
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { status: 'inactive' }
    When method patch
    Then status 200
    And match response.status == 'inactive'
    # The other fields must remain unchanged after a partial update
    And match response.id     == userId
    And match response.email  == userEmail
    * print '[PT-4 Step 3] PATCH status → inactive for userId:', userId

    # ── Step 4 : DELETE ─────────────────────────────────────────────────────────
    # Always clean up — this is the most important step for soak tests.
    # Without DELETE, each iteration of the 5-minute soak test would accumulate
    # rows in the database, eventually slowing down all subsequent GET queries.
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method delete
    Then status 204
    * print '[PT-4 Step 4] Deleted userId:', userId, '— lifecycle complete'

  # ─────────────────────────────────────────────────────────────────────────────
  # PT-4b: Read-then-Update (partial lifecycle for mixed traffic simulation)
  # Simulates a user who reads an existing resource and then modifies it —
  # without going through the create/delete overhead.  Useful for mixing into
  # a soak test alongside full lifecycle users to model realistic traffic shapes.
  # NOTE: This scenario calls CreateUserHelper.feature to first create a user so
  # it has a real ID to operate on.
  # ─────────────────────────────────────────────────────────────────────────────
  @ReadThenUpdate @CRUD @PT4
  Scenario: Partial lifecycle — Create → Read → Update (no delete — tests data retention)

    # Step 1 — Create
    * def suffix    = randomStr()
    * def userEmail = 'partial.' + suffix + '@loadtest.io'
    * def payload   = { name: 'Partial Lifecycle', email: '#(userEmail)', gender: 'female', status: 'active' }

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post
    Then status 201
    * def userId = response.id
    * print '[PT-4b Step 1] Created userId:', userId

    # Step 2 — Read
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get
    Then status 200
    And match response.id == userId
    * print '[PT-4b Step 2] Read-back OK for userId:', userId

    # Step 3 — Update name only
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Updated Partial User' }
    When method patch
    Then status 200
    And match response.name == 'Updated Partial User'
    And match response.id   == userId
    * print '[PT-4b Step 3] Name updated for userId:', userId
