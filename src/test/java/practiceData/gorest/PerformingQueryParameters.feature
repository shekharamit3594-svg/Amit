Feature: Understanding the concepts of Query Parameters

  Background:
  Scenario: Performing different operations on the query parameters
    Given url baseUrl

    #Path represents the path parameter
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType

    #Performing GET operation with query parameters to
    #Param represents the query parameter and we can perform multiple query parameters in a single request
    And param status = 'active'
    And param gender = 'male'
    When method get
    Then status 200
    And print 'Response after performing GET operation with query parameters:', response