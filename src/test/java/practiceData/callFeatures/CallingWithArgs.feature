Feature: Calling Feature Files with Arguments — Module 7.4

  # call read('other.feature') { key: value, ... }
  #
  # The JSON object after the file path is the "argument" passed to the called feature.
  # Every key in that object becomes a Karate variable inside the called feature,
  # exactly as if it had written:  * def key = value  at the top.
  #
  # The called feature's final variable state is returned as a map.
  # Capture it with:   * def result = call read('...') { ... }
  # Then read fields:  * def id = result.userId
  #
  # This is the Karate equivalent of calling a Java method with parameters.

  Background:
    * def name =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """
    * def userEmail = 'args.single.' + name() + '@example.com'
    * def gender = Java.type('practiceData.gorest.FetchGendersAndStatus').getGender()
    * def status = Java.type('practiceData.gorest.FetchGendersAndStatus').getStatus()

  @CallingWithArgs @SingleCall
  Scenario: Pass explicit user data to CreateUserHelper and read back the return value

    # The JSON object {name, email, gender, status} is injected into CreateUserHelper.feature.
    # Inside the helper, those four keys are available as ordinary Karate variables.
    * def result = call read('CreateUserHelper.feature') { name: '#(name())', email: '#(userEmail)', gender: '#(gender)', status: '#(status)' }

    * print 'Return value from called feature:', result
    * print 'Captured userId:', result.userId

    # result.userId and result.createdUser are variables defined inside the helper
    * match result.userId == '#number'
    * match result.createdUser.email  == '#(userEmail)'
    * match result.createdUser.gender == '#(gender)'
    * match result.createdUser.status == '#(status)'


  @CallingWithArgs @CallSameHelperTwice
  Scenario: Call the same helper twice with different args — independent results, different IDs

    * def name =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """
    * def userEmail = 'args.single.' + name() + '@example.com'

    # First call — active male user
    * def activeResult = call read('CreateUserHelper.feature') { name: '#(name())', email: '#(userEmail)', gender: 'male', status: 'active' }

    * def secondName = name()
    * def secondEmail = 'args.single.' + secondName + '@example.com'
    # Second call — inactive female user (different args, same helper feature)
    * def inactiveResult = call read('CreateUserHelper.feature') { name: '#(secondName)', email: '#(secondEmail)', gender: 'female', status: 'inactive' }

    * print 'Active user ID  :', activeResult.userId
    * print 'Inactive user ID:', inactiveResult.userId

    * match activeResult.createdUser.status   == 'active'
    * match inactiveResult.createdUser.status == 'inactive'

    # Each call created an independent resource — the IDs must be different
    * assert activeResult.userId != inactiveResult.userId


  @CallingWithArgs @ChainedCalls
  Scenario: Chain calls — use the userId returned by the helper as input to the next request


    # Step 1 — Create user via the helper and capture the returned userId
    * def createResult = call read('CreateUserHelper.feature') { name: '#(name())', email: '#(userEmail)', gender: 'male', status: 'active' }
    * def userId = createResult.userId
    * print 'Created user — ID:', userId

    # Step 2 — Use the returned userId in a direct GET to verify the resource exists
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get
    Then status 200
    And print 'Fetched user:', response
    And match response.id   == userId

    # Step 3 — Use the returned userId in a PATCH call
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { status: 'inactive' }
    When method patch
    Then status 200
    And match response.status == 'inactive'

  @CallingWithArgs @ChainedCallsWithExamples
  Scenario Outline: Chain calls — use the userId returned by the helper as input to the next request


    # Step 1 — Create user via the helper and capture the returned userId
    * def createResult = call read('CreateUserHelper.feature') { name: '<User_Name>', email: '<User_Email>', gender: 'male', status: 'active' }
    * def userId = createResult.userId
    * print 'Created user — ID:', userId

    # Step 2 — Use the returned userId in a direct GET to verify the resource exists
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    When method get
    Then status 200
    And print 'Fetched user:', response
    And match response.id   == userId

    # Step 3 — Use the returned userId in a PATCH call
    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { status: 'inactive' }
    When method patch
    Then status 200
    And match response.status == 'inactive'

    Examples:
      | User_Name | User_Email                            |
      | #(name()) | 'args.ex1.' + #(name()) + '@example.com' |
      | #(name()) | 'args.ex2.' + #(name()) + '@example.com' |

