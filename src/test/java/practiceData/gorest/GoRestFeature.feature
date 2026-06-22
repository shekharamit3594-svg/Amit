Feature: Understanding the Go Rest API

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
            for(let i = 0; i < length; i++) {
                randomName += alphabet.charAt(Math.floor(Math.random() * alphabet.length));
            }
            return randomName;
            }
    """
    * def payload =
      """
      {
           "phone": "123-456-7891"
       }
       """
    #Adding the new property to the payload object with a random email ID
    #Which is similar to JS Object Modifications
    * payload.name = name(10)
    * def gender = Java.type('practiceData.gorest.FetchGendersAndStatus')
    * def status = Java.type('practiceData.gorest.FetchGendersAndStatus')
    * payload.gender = gender.getGender()
    * payload.status = status.getStatus()
    * payload.email = emailID()

  Scenario: Creating a New User and checking whether the user is created or not
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    #Passing the payload to the POST Operation
    And request payload

    #Performing the POST Operation
    When method post

    #Validating the status code is 201
    Then status 201
    And print 'Response after creating a new user:', response
    And match response.id != null
    And match response.id == '#number'
    And match response.name == '#string'
    And match response.email == '#string'
    And match response.gender == '#string'
    And match response.status == '#string'
    And match response.phone == '#string'
    And match response.name == payload.name
    And match response.email == payload.email

    #Storing the user ID for further operations
    * def userId = response.id
    Given url baseUrl
    And path 'public/v2/users', userId
    And header Authorization = 'Bearer 1470e5bb7fb3e1bc38d2dc4ceb91a43d8a7747ef0a808319560c1055a44fc37f'
    And header Content-Type = 'application/json'
    And method get
    And status 200
    And print 'Response after fetching the user with ID:', userId, 'is:', response
    And match response.id == userId
