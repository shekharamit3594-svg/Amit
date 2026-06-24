Feature: PUT Operation - Update User Created by POST Feature

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

  @PutOperation
  Scenario: Update a user using the ID generated from the POST feature
    * def postResult = call read('PostOperation.feature@PostOperation')
    * def userId = postResult.userId
    * print 'User ID received from POST feature:', userId

    * def updatePayload =
      """
      {
        "name": "#(name(10))",
        "email": "#(emailID())",
        "phone": "987-654-3210"
      }
      """

    Given url baseUrl
    And path usersPath, userId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request updatePayload
    When method patch
    Then status 200
    And print 'PATCH Response - User updated:', response
    And match response.id == userId
    And match response.name == updatePayload.name
    And match response.email == updatePayload.email
    And match response.phone == updatePayload.phone
