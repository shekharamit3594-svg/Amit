Feature: Data-Driven User Creation from CSV

  # When Karate reads a CSV file it returns a list of maps.
  # Each row becomes a map where the keys are the column headers.
  # Example — row 1 becomes: { name: 'Alice Martin', email: 'alice.martin.qa1@example.com', gender: 'female', status: 'active', phone: '100-200-3001' }

  Scenario: Inspect CSV data loaded by read()
    # read() parses the CSV and returns a JSON array — useful to verify the data before running tests
    * def users = read('classpath:practiceData/gorest/test-data/gorest_users.csv')
    * print 'Total records loaded from CSV:', users.length
    * print 'First record:', users[0]
    * print 'Second record:', users[1]
    # Assert the shape of each record so we know the CSV was parsed correctly
    * match each users == { name: '#string', email: '#string', gender: '#string', status: '#string', phone: '#string' }


  # Scenario Outline runs once per CSV row.
  # Karate replaces every <column> placeholder in the step text with the actual cell value before executing.
  # So "name": "<name>" becomes "name": "Alice Martin" for the first row.
  Scenario Outline: Create GoRest user from CSV — <name>
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    # The entire request body is built from the CSV row — each <placeholder> is substituted at runtime
    And request { "name": "<name>", "email": "<email>", "gender": "<gender>", "status": "<status>", "phone": "<phone>" }
    When method post
    Then status 201
    And print 'Response for <name>:', response
    # Validate the response reflects exactly what was sent from the CSV row
    And match response.id == '#number'
    And match response.name == '<name>'
    And match response.email == '<email>'
    And match response.gender == '<gender>'
    And match response.status == '<status>'
    And match response.phone == '<phone>'

    # read() here returns the CSV as an Examples table — one column per header, one row per data row
    Examples:
      | read('classpath:practiceData/gorest/test-data/gorest_users.csv') |
