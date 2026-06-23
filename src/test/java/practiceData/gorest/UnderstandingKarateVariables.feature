Feature: Understanding Karate Variables

  # Background runs before EVERY Scenario in this feature.
  # Variables defined here are shared across all scenarios — equivalent to a @BeforeMethod setup.
  Background:
    * def featureName = 'Karate Variables Demo'
    * def apiVersion = 'v2'
    * print '--- Running scenario from feature:', featureName, '| API version:', apiVersion, '---'


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 1 — Basic variable types
  # * def assigns a value to a named variable.
  # Karate infers the type automatically from the value on the right-hand side.
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @BasicTypes
  Scenario: Defining and accessing basic variable types

    # String
    * def firstName = 'Alice'

    # Number (integer and decimal are both supported)
    * def age = 28
    * def accountBalance = 5432.75

    # Boolean
    * def isActive = true

    # null
    * def emptyField = null

    # JSON object — Karate parses this as a native object, not a plain string
    * def address = { city: 'New York', country: 'USA', pinCode: 10001 }

    # Array
    * def roles = ['admin', 'editor', 'viewer']

    * print 'String     :', firstName
    * print 'Integer    :', age
    * print 'Decimal    :', accountBalance
    * print 'Boolean    :', isActive
    * print 'Null       :', emptyField
    * print 'JSON Object:', address
    * print 'Array      :', roles

    # Assertions to verify the values and access patterns
    * match firstName == 'Alice'
    * match age == 28
    * match accountBalance == 5432.75
    * match isActive == true
    * match emptyField == null

    # Dot notation — access a property by name
    * match address.city == 'New York'
    * match address.pinCode == 10001

    # Bracket notation — equivalent to dot notation, useful when key has special characters
    * match address['country'] == 'USA'

    # Array indexing — zero-based
    * match roles[0] == 'admin'
    * match roles[2] == 'viewer'

    # Array length assertion
    * match roles == '#[3]'


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 2 — Multi-line JSON variable (""" block)
  # Use triple-quoted strings to define a JSON body across multiple lines.
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @MultiLineJSON
  Scenario: Defining a multi-line JSON variable using triple quotes

    * def userPayload =
      """
      {
        "name": "Bob Carter",
        "email": "bob.carter@example.com",
        "gender": "male",
        "status": "active",
        "address": {
          "city": "Chicago",
          "country": "USA"
        }
      }
      """

    * print 'User payload:', userPayload

    # Karate parses the """ block as a JSON object, not as a raw string
    * match userPayload.name == 'Bob Carter'
    * match userPayload.address.city == 'Chicago'

    # Nested field access via dot-chaining
    * match userPayload.address.country == 'USA'


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 3 — Embedded expressions  #(variable)
  # Use #(varName) to inject the runtime value of a variable into any string,
  # JSON field value, or step argument. This is how variables flow into request bodies.
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @EmbeddedExpressions
  Scenario: Using embedded expressions to inject variables into strings and JSON

    * def name   = 'Carol Hughes'
    * def userId = 99
    * def status = 'active'

    # Embedded in a plain string — #(var) is replaced with the variable's value
    * def greeting = 'Welcome, #(name)!'
    * print greeting
    * match greeting == 'Welcome, Carol Hughes!'

    # Embedded inside a JSON object
    * def payload = { id: '#(userId)', name: '#(name)', status: '#(status)' }
    * print 'Payload:', payload

    # Note: #(userId) resolves to the number 99, not the string "99"
    * match payload.id == 99
    * match payload.name == 'Carol Hughes'

    # Embedded inside a multi-line """ JSON block
    * def requestBody =
      """
      {
        "user_id": #(userId),
        "full_name": "#(name)",
        "account_status": "#(status)"
      }
      """
    * print 'Request body with embedded expressions:', requestBody
    * match requestBody.user_id == 99
    * match requestBody.full_name == 'Carol Hughes'
    * match requestBody.account_status == 'active'


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 4 — Modifying JSON variables with * set and * remove
  # Use * set to add or update a field.
  # Use * remove to delete a field from a JSON variable.
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @SetAndRemove
  Scenario: Modifying a JSON variable using set and remove

    * def payload =
      """
      {
        "name": "Test User",
        "email": "test.user@example.com",
        "gender": "male",
        "status": "active"
      }
      """
    * print 'Original payload:', payload

    # Add a new field that does not exist yet
    * set payload.phone = '999-888-7777'
    * print 'After adding phone:', payload
    * match payload.phone == '999-888-7777'

    # Modify an existing field
    * set payload.status = 'inactive'
    * print 'After changing status:', payload
    * match payload.status == 'inactive'

    # Add a nested object field
    * set payload.address = { city: 'Austin', country: 'USA' }
    * print 'After adding nested address:', payload
    * match payload.address.city == 'Austin'

    # Remove a field entirely
    * remove payload.phone
    * print 'After removing phone:', payload
    # phone key must no longer exist
    * match payload.phone == '#notpresent'


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 5 — * table keyword
  # Defines a JSON array from an inline markdown-style table.
  # Column headers become keys; cell values must be valid Karate expressions.
  # Strings must be quoted with single quotes inside the cells.
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @TableKeyword
  Scenario: Defining structured data with the * table keyword

    #[
#        {
#            "name": "Alice Martin",
#            "gender": "female",
#            "status": "active"
#        },
#        {
#            "name": "Bob Carter",
#            "gender": "male",
#            "status": "active"
#        },
#        {
#            "name": "Carol Hughes",
#            "gender": "female",
#            "status": "inactive"
#        }
    #]
    * table users
      | name           | gender   | status     |
      | "Alice Martin" | "female" | "active"   |
      | "Bob Carter"   | "male"   | "active"   |
      | "Carol Hughes" | "female" | "inactive" |

    * print 'Users from table:', users

    # users is now a JSON array — each row is a JSON object
    * match users == '#[3]'
    * match users[0].name == 'Alice Martin'
    * match users[1].gender == 'male'
    * match users[2].status == 'inactive'

    # Assert that every element has the correct shape
    * match each users == { name: '#string', gender: '#string', status: '#string' }


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 6 — Variables derived from a JS function
  # Use * def with a function() block to generate dynamic values at runtime.
  # Call the function with * def result = call functionVar
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @JSFunctions
  Scenario: Defining and calling JavaScript functions as variables

    # Inline JS function that returns a random 6-character alphanumeric string
    * def randomString =
      """
      function(length) {
        var chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
        var result = '';
        for (var i = 0; i < length; i++) {
          result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return result;
      }
      """

    # Call the function and store the result in a new variable
    * def prefix = call randomString 6
    * def email  = prefix + '@example.com'
    * print 'Generated email:', email

    * match email == '#string'
    # email must end with @example.com
    * match email == '#regex .+@example\\.com'


  # ─────────────────────────────────────────────────────────────────────────────
  # CONCEPT 7 — Extracting variables from a response and reusing in the next call
  # After an API call, response fields can be stored in variables via * def.
  # Those variables can then be used in subsequent requests within the same scenario.
  # ─────────────────────────────────────────────────────────────────────────────
  @KarateVariables @ResponseExtraction
  Scenario: Extract a field from a response and use it in the next API call

    # Generate a unique email to avoid GoRest duplicate-email rejection on re-runs
    * def randomSuffix =
      """
      function() {
        return Math.random().toString(36).substring(2, 8);
      }
      """
    * def uniqueEmail = call randomSuffix
    * def email = 'variabledemo.' + uniqueEmail + '@example.com'
    * print 'Using email:', email

    # ── Step 1: Create a user ──────────────────────────────────────────────────
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { name: 'Variable Demo User', email: '#(email)', gender: 'male', status: 'active', phone: '555-100-2000' }
    When method post
    Then status 201
    And print 'Create response:', response

    # Extract the server-assigned ID into a variable
    * def createdUserId = response.id
    * def createdName   = response.name
    * print 'Extracted user ID:', createdUserId
    * print 'Extracted name   :', createdName

    # ── Step 2: Use the extracted ID to fetch the same user ───────────────────
    Given url baseUrl
    And path usersPath, createdUserId
    And header Authorization = bearerToken
    And header Content-Type = contentType
    When method get
    Then status 200
    And print 'GET response:', response

    # The fetched user must match what was created in Step 1
    And match response.id    == createdUserId
    And match response.name  == createdName
    And match response.email == email
