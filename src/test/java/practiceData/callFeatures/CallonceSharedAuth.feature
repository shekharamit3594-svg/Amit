Feature: callonce — Shared Auth Token Across Scenarios — Module 7.5 / 7.6 / 7.7

  # CONCEPT: call vs callonce
  #
  # call     — the called feature runs EVERY TIME the line is executed.
  #            If placed in Background it runs once per scenario (potentially slow
  #            if the called feature hits a real login endpoint).
  #
  # callonce — the called feature runs ONCE for the entire feature file.
  #            The result is cached. Every subsequent Background execution for other
  #            scenarios returns the cached object instantly — no HTTP call is made.
  #
  # PATTERN (7.6 — login as reusable called feature):
  #   1. Create a dedicated feature file that performs login (AuthSetup.feature).
  #   2. In Background, use callonce to execute it once and cache the auth object.
  #   3. Extract the token from the cached object.
  #   4. All scenarios in this file share the same token (7.7 — shared auth).
  #
  # This is the Karate equivalent of a @BeforeSuite setup in TestNG.

  Background:
    # callonce: AuthSetup.feature runs only on the FIRST scenario.
    # For all subsequent scenarios the cached result is returned immediately.
    * def auth  = callonce read('AuthSetup.feature')
    * def token = auth.authToken
    * print '--- Background fired — token (cached after first run):', token, '---'


  @CallonceAuth @TokenIsAvailable
  Scenario: Verify the shared token is accessible and has the expected format

    * print 'Scenario 1 — token:', token
    * match token == '#string'
    # GoRest bearer tokens start with 'Bearer '
    * assert token.startsWith('Bearer ')
    * print 'Token format is correct'


  @CallonceAuth @CreateWithSharedToken
  Scenario: Create a user using the shared auth token — AuthSetup must NOT run again

    # Background ran again, but callonce returned the cached auth object.
    # AuthSetup.feature was NOT re-executed.
    * print 'Scenario 2 — same token (from cache):', token

    * def randomName =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """
    * def suffix = call randomName
    * def email  = 'callonce.user1.' + suffix + '@example.com'

    Given url baseUrl
    And path usersPath
    # Use the shared token — the same object that Scenario 1 received from callonce
    And header Authorization = token
    And header Content-Type = contentType
    And request { name: '#(suffix)', email: '#(email)', gender: 'male', status: 'active' }
    When method post
    Then status 201
    And print 'Created user with shared token. ID:', response.id
    And match response.id == '#number'
    And match response.id != 0
    #Checking if the response id exists or not in the response
    And match response.id == '#present'


  @CallonceAuth @SecondUserSameToken
  Scenario: Second user creation — same cached token, no third login attempt

    * print 'Scenario 3 — still same cached token:', token

    * def randomSuffix =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """
    * def suffix = call randomSuffix
    * def email  = 'callonce.user2.' + suffix + '@example.com'

    Given url baseUrl
    And path usersPath
    And header Authorization = token
    And header Content-Type = contentType
    And request { name: 'Callonce User 2', email: '#(email)', gender: 'female', status: 'inactive' }
    When method post
    Then status 201
    And print 'Created second user with same cached token. ID:', response.id
    And match response.id == '#number'


  @CallonceAuth @TokenUsedInGetRequest
  Scenario: Use shared token for a GET request — demonstrating token reuse across HTTP methods

    * print 'Scenario 4 — token reused for GET:', token

    Given url baseUrl
    And path usersPath
    And header Authorization = token
    And param status = 'active'
    And param per_page = 5
    When method get
    Then status 200
    And match response == '#[]'
    And print 'GET with shared token returned', response.length, 'users'
    # AuthSetup.feature ran only once across all four scenarios in this file
    And print 'callonce ensured only 1 login across', 4, 'scenarios'
