Feature: Create User Helper — Reusable Called Feature

  # This feature is designed to be CALLED by other features, not run directly.
  #
  # The caller passes:  name, email, gender, status  as a JSON argument object.
  # The helper returns: userId, createdUser          as part of its final variable state.
  #
  # Caller syntax (7.4 — passing args):
  #   * def result = call read('CreateUserHelper.feature') { name: 'Alice', email: '#(email)', gender: 'female', status: 'active' }
  #   * def userId = result.userId
  #
  # Inside this feature, name / email / gender / status are available as ordinary
  # Karate variables because the caller injected them via the argument object.

  Scenario: Create a GoRest user from caller-supplied arguments

    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    # Embedded expressions substitute the variables injected by the caller
    And request { name: '#(name)', email: '#(email)', gender: '#(gender)', status: '#(status)' }
    When method post
    Then status 201

    # These two variables become part of the return value visible to the caller
    * def userId      = response.id
    * def createdUser = response
    * print 'Helper created user — ID:', userId, '| Name:', name
