Feature: PATCH Operation — Partial Update

  # PATCH sends only the fields that should change.
  # Fields not included in the request body remain unchanged on the server.
  # GoRest returns 200 on a successful PATCH.
  #
  # KEY RULE: Only include the fields you want to update. Every other field on
  # the server stays exactly as it was — PATCH never touches fields you don't send.

  Background:
    * def randomSuffix =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """

  @PatchOperation @PatchName
  Scenario: PATCH — Update only the name; email gender and status must remain unchanged

    * def suffix        = call randomSuffix
    * def originalEmail = 'patch.name.' + suffix + '@example.com'

    # Step 1 — Create a user so we have an ID to PATCH
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Original Name', email: '#(originalEmail)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created user — ID:', userId, '| Name:', response.name

    # Step 2 — PATCH: change only the name field
    # email, gender, and status are NOT included in the body — the server keeps them as-is
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Updated Name via PATCH' }
    When method patch
    Then status 200
    And print 'PATCH response:', response

    # Name changed
    And match response.name == 'Updated Name via PATCH'
    # All other fields remain exactly as they were after the POST
    And match response.email  == originalEmail
    And match response.gender == 'male'
    And match response.status == 'active'


  @PatchOperation @PatchStatus
  Scenario: PATCH — Toggle user status from active to inactive; all other fields intact

    * def suffix = call randomSuffix
    * def email  = 'patch.status.' + suffix + '@example.com'

    # Step 1 — Create an active user
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Status Toggle User', email: '#(email)', gender: 'female', status: 'active' }
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created user — ID:', userId, '| Status: active'

    # Step 2 — PATCH only the status field
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { status: 'inactive' }
    When method patch
    Then status 200
    And print 'After PATCH status:', response.status

    And match response.status == 'inactive'
    # name, email, gender are unaffected
    And match response.name   == 'Status Toggle User'
    And match response.email  == email
    And match response.gender == 'female'


  @PatchOperation @PatchMultipleFields
  Scenario: PATCH — Update two fields at once; third field stays unchanged

    * def suffix = call randomSuffix
    * def email  = 'patch.multi.' + suffix + '@example.com'

    # Step 1 — Create user
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Multi-Field User', email: '#(email)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created user — ID:', userId

    # Step 2 — PATCH name AND status together; gender and email stay the same
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Patched Name', status: 'inactive' }
    When method patch
    Then status 200
    And print 'After multi-field PATCH:', response

    And match response.name   == 'Patched Name'
    And match response.status == 'inactive'
    # gender and email were not sent — must be unchanged
    And match response.gender == 'male'
    And match response.email  == email
