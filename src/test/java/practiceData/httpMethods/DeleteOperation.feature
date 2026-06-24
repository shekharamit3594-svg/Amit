Feature: DELETE Operation — Remove a Resource

  # DELETE permanently removes a resource from the server.
  # GoRest returns 204 No Content on success — the response body is empty.
  # A subsequent GET for the same ID must return 404 Not Found, confirming deletion.
  #
  # IMPORTANT: 204 means success AND no body. Do not assert on response content after DELETE.

  Background:
    * def name =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """
    * def email = name() + '@abc.com'
    * def gender = Java.type('practiceData.gorest.FetchGendersAndStatus').getGender()
    * def status = Java.type('practiceData.gorest.FetchGendersAndStatus').getStatus()

  @DeleteOperation @BasicDelete
  Scenario: DELETE — Remove a user and verify 204 No Content

    # Step 1 — Create a user to delete
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request
    """
    {
        name: '#(name())',
        email: '#(email)',
        gender: '#(gender)',
        status: '#(status)'
    }
    """
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created user to delete — ID:', userId

    # Step 2 — DELETE the user by ID
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method delete
    # 204 No Content — deletion succeeded; no body is returned by GoRest
    Then status 204
    And print 'User deleted successfully. ID was:', userId

    # Step 3 — Try to GET the deleted user; the server must now return 404
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get
    Then status 404
    And print 'Confirmed: GET after DELETE returns 404 for user ID:', userId



  @DeleteOperation @CreatePatchDelete
  Scenario: Full lifecycle — POST → PATCH → DELETE → confirm 404

    # Step 1 — Create
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request
    """
      {
        name: '#(name())',
        email: '#(email)',
        gender: '#(gender)',
        status: '#(status)'
      }
    """
    When method post
    Then status 201
    * def userId = response.id
    * print 'Created — ID:', userId

    # Step 2 — PATCH
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    * def updatedName = name()
    And request { name: '#(updatedName)' }
    When method patch
    Then status 200
    And match response.name == updatedName
    And print 'Patched — name updated'

    # Step 3 — DELETE
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method delete
    Then status 204
    And print 'Deleted — ID:', userId

    # Step 4 — Confirm gone
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get
    Then status 404
    And print 'Confirmed lifecycle complete — resource no longer exists'
