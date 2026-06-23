Feature: POST Operation - Create a New User and Capture the Generated ID

  Background:
    * def emailID =
      """
        function() {
          let randomEmailID = Math.random().toString(36).substring(2, 10);
          return randomEmailID + '@example.com';
        }
      """
    * def name =
      """
        function(length) {
          let alphabet = 'abcdefghijklmnopqrstuvwxyz';
          let randomName = '';
          for (let i = 0; i < length; i++) {
            randomName += alphabet.charAt(Math.floor(Math.random() * alphabet.length));
          }
          return randomName;
        }
      """
    * def gender = Java.type('practiceData.gorest.FetchGendersAndStatus')
    * def status = Java.type('practiceData.gorest.FetchGendersAndStatus')
    * def payload =
      """
      {
        "phone": "123-456-7891"
      }
      """
    * payload.name = name(10)
    * payload.gender = gender.getGender()
    * payload.status = status.getStatus()
    * payload.email = emailID()

  @PostOperation
  Scenario: Create a new user via POST and capture the generated ID
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request payload
    When method post
    Then status 201
    And print 'POST Response - New user created:', response
    And match response.id != null
    And match response.id == '#number'
    And match response.name == '#string'
    And match response.email == '#string'
    And match response.gender == '#string'
    And match response.status == '#string'
    And match response.phone == '#string'
    And match response.name == payload.name
    And match response.email == payload.email
    # Capturing the generated ID so callers can access it via result.userId
    * def userId = response.id
    And print 'Captured User ID for downstream use:', userId
