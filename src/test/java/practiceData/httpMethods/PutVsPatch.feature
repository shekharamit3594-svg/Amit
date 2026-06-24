Feature: PUT vs PATCH — Understanding the Difference

  # PUT   = Full replacement.
  #         You send ALL fields — even fields you are not changing.
  #         The server replaces the entire resource with what you send.
  #         Use when replacing a resource completely.
  #
  # PATCH = Partial update.
  #         You send ONLY the fields that should change.
  #         Fields absent from the body remain exactly as they are on the server.
  #         Use when updating one or two fields without touching the rest.
  #
  # Both return 200 on success in GoRest.

  Background:
    * def randomSuffix =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """

  @PutVsPatch @PutFullReplacement
  Scenario: PUT — must send ALL fields; the server replaces the resource with what you provide

    * def suffix       = call randomSuffix
    * def originalEmail = 'pvp.put.orig.' + suffix + '@example.com'
    * def updatedEmail  = 'pvp.put.upd.'  + suffix + '@example.com'

    # Step 1 — Create a user
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'PUT Demo User', email: '#(originalEmail)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created user — ID:', userId, '| Gender: male | Status: active'

    # Step 2 — PUT: replace the entire resource
    # Every required field (name, email, gender, status) must be present.
    # Changing gender from 'male' to 'female' and email to the updated one.
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'PUT Demo User', email: '#(updatedEmail)', gender: 'female', status: 'inactive' }
    When method put
    Then status 200
    And print 'After PUT:', response

    # The server reflects the full new representation we sent
    And match response.id     == userId
    And match response.name   == 'PUT Demo User'
    And match response.email  == updatedEmail
    And match response.gender == 'female'
    And match response.status == 'inactive'


  @PutVsPatch @PatchPartialUpdate
  Scenario: PATCH — only the fields you send are changed; everything else stays as-is

    * def suffix = call randomSuffix
    * def email  = 'pvp.patch.' + suffix + '@example.com'

    # Step 1 — Create a user
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'PATCH Demo User', email: '#(email)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created user — ID:', userId, '| Gender: male | Status: active'

    # Step 2 — PATCH: only change gender; name, email, status stay untouched
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { gender: 'female' }
    When method patch
    Then status 200
    And print 'After PATCH:', response

    # Only gender changed
    And match response.gender == 'female'
    # name, email, status remain exactly as created
    And match response.name   == 'PATCH Demo User'
    And match response.email  == email
    And match response.status == 'active'


  @PutVsPatch @SideBySideComparison
  Scenario: Side-by-side — same starting state; PUT replaces, PATCH patches — compare the outcome

    * def suffix  = call randomSuffix
    * def emailA  = 'pvp.side.a1.' + suffix + '@example.com'
    * def emailA2 = 'pvp.side.a2.' + suffix + '@example.com'
    * def emailB  = 'pvp.side.b.'  + suffix + '@example.com'

    # ── Create User A — will be updated with PUT ────────────────────────────
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Side-by-Side User A', email: '#(emailA)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    * def userAId = response.id
    * print 'User A created — ID:', userAId

    # PUT User A: supply all fields; switch gender male → female AND change email
    Given url baseUrl
    And path usersPath, userAId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Side-by-Side User A', email: '#(emailA2)', gender: 'female', status: 'active' }
    When method put
    Then status 200
    * def afterPut = response
    * print 'After PUT  — gender:', afterPut.gender, '| email:', afterPut.email

    # ── Create User B — will be updated with PATCH ──────────────────────────
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Side-by-Side User B', email: '#(emailB)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    * def userBId = response.id
    * print 'User B created — ID:', userBId

    # PATCH User B: only change gender; email is NOT in the body
    Given url baseUrl
    And path usersPath, userBId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { gender: 'female' }
    When method patch
    Then status 200
    * def afterPatch = response
    * print 'After PATCH — gender:', afterPatch.gender, '| email:', afterPatch.email

    # ── Comparison ───────────────────────────────────────────────────────────
    # Both switched gender from male → female
    And match afterPut.gender   == 'female'
    And match afterPatch.gender == 'female'

    # PUT replaced the email with the new one we sent
    And match afterPut.email == emailA2

    # PATCH did NOT touch the email — it is still the original value
    And match afterPatch.email == emailB
    And print 'CONCLUSION — PUT changed email, PATCH left email unchanged'
