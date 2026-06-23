Feature: Data-Driven Testing — Multiple Examples Patterns

  # This feature demonstrates four distinct ways to use the Examples block
  # in a Scenario Outline for data-driven API testing.
  #
  # PATTERN 1 — Inline Examples with simple string values (GET with query params)
  # PATTERN 2 — Inline Examples with a numeric expected-status column (positive + negative in one table)
  # PATTERN 3 — Multiple Examples blocks under one Scenario Outline, each with its own tag
  # PATTERN 4 — Examples loaded from a JSON file instead of an inline table


  # ─────────────────────────────────────────────────────────────────────────────
  # PATTERN 1 — Inline Examples table (string values, query parameters)
  #
  # Each row in the Examples table is a separate test execution.
  # The column header names become the <placeholder> names used in the step text.
  # String values in the table do NOT need quotes — the cell value is treated as
  # plain text and substituted directly into the step.
  # ─────────────────────────────────────────────────────────────────────────────
  @DataDriven @Pattern1 @InlineExamples
  Scenario Outline: PATTERN 1 — Filter GoRest users by status and gender — <status> / <gender>
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken

    # <status> and <gender> are replaced with the actual cell value from each Examples row
    And param status = '<status>'
    And param gender = '<gender>'
    And param per_page = <per_page>
    When method get
    Then status 200

    # response is an JSON array — assert it is a valid array regardless of length
    And match response == '#[]'
    And print 'Total users with status=<status>, gender=<gender>: is', response.length

    # Inline Examples table — 4 rows = 4 independent test executions
    Examples:
      | status   | gender | per_page |
      | active   | male   | 30       |
      | active   | female | 40       |
      | inactive | male   | 20       |
      | inactive | female | 45       |


  # ─────────────────────────────────────────────────────────────────────────────
  # PATTERN 2 — Inline Examples with an expected status code column
  #
  # Including an <expectedStatus> column lets a single Scenario Outline cover
  # both positive (201) and negative (422) cases without duplicating steps.
  #
  # Key rule: number values in the Examples table do NOT use quotes.
  # Karate substitutes them as-is, so "Then status <expectedStatus>"
  # becomes "Then status 201" or "Then status 422" per row.
  # ─────────────────────────────────────────────────────────────────────────────
  @DataDriven @Pattern2 @ExpectedStatusColumn
  Scenario Outline: PATTERN 2 — Create user — positive and negative in one table — <label>
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { "name": "<name>", "email": "<email>", "gender": "<gender>", "status": "<status>" }
    When method post

    # <expectedStatus> is a number column — no quotes in the table, no quotes here
    Then status <expectedStatus>
    And print 'Result for <label>:', response

    # Examples table with a mix of valid and invalid data rows
    # Positive rows expect 201; negative rows expect 422
    Examples:
      | label                 | name        | email                       | gender | status   | expectedStatus |
      | valid male active     | David Lee   | david.lee.dd1@example.com   | male   | active   | 201            |
      | valid female inactive | Emma Wilson | emma.wilson.dd2@example.com | female | inactive | 201            |
      | invalid gender value  | Frank Brown | frank.brown.dd3@example.com | other  | active   | 422            |
      | invalid status value  | Grace Kim   | grace.kim.dd4@example.com   | female | unknown  | 422            |
      | blank name field      |             | blank.name.dd5@example.com  | male   | active   | 422            |


  # ─────────────────────────────────────────────────────────────────────────────
  # PATTERN 3 — Multiple Examples blocks under one Scenario Outline
  #
  # A single Scenario Outline can have more than one Examples block.
  # Each block can carry its own tag so you can run only the positive
  # or only the negative dataset from the command line:
  #
  #   Run positive only:  mvn test -Dkarate.options="--tags @Positive"
  #   Run negative only:  mvn test -Dkarate.options="--tags @Negative"
  #
  # The descriptive label after "Examples:" (e.g. "Valid users") appears in
  # the report and makes it easier to identify which dataset failed.
  # ─────────────────────────────────────────────────────────────────────────────
  @DataDriven @Pattern3 @MultipleExamplesBlocks
  Scenario Outline: PATTERN 3 — Create user from tagged dataset — <label>
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { "name": "<name>", "email": "<email>", "gender": "<gender>", "status": "<status>" }
    When method post
    Then status <expectedStatus>
    And print 'Result for <label>:', response

    # First block — valid data; tagged @Positive so it can be run in isolation
    @Positive
    Examples: Valid users — expect 201
      | label                | name        | email                       | gender | status   | expectedStatus |
      | male active user     | Henry Adams | henry.adams.dd6@example.com | male   | active   | 201            |
      | female inactive user | Iris Clarke | iris.clarke.dd7@example.com | female | inactive | 201            |

    # Second block — invalid data; tagged @Negative so it can be run in isolation
    @Negative
    Examples: Invalid users — expect 422
      | label            | name        | email                       | gender  | status    | expectedStatus |
      | invalid gender   | Jack White  | jack.white.dd8@example.com  | unknown | active    | 422            |
      | invalid status   | Karen Black | karen.black.dd9@example.com | female  | suspended | 422            |
      | empty name field |             | noname.dd10@example.com     | male    | active    | 422            |


  # ─────────────────────────────────────────────────────────────────────────────
  # PATTERN 4 — Examples loaded from a JSON file
  #
  # Instead of an inline table, the Examples block points to a JSON file.
  # The file must be a JSON array where every object has the same keys.
  # Karate maps each object's keys to column names and each object to one row.
  #
  # gorest_users_json.json contains:
  # [
  #   { "name": "Liam Johnson", "email": "liam.johnson.j1@example.com", ... },
  #   { "name": "Mia Thompson", "email": "mia.thompson.j2@example.com", ... },
  #   ...
  # ]
  #
  # Difference from CSV (Pattern in DataDrivenUserCreation.feature):
  #   CSV  → flat key=value pairs, no nesting possible
  #   JSON → supports nested objects; richer structure for complex payloads
  # ─────────────────────────────────────────────────────────────────────────────
  @DataDriven @Pattern4 @JSONFileExamples
  Scenario Outline: PATTERN 4 — Create GoRest user from JSON file — <name>
    Given url baseUrl
    And path usersPath
    And header Authorization = bearerToken
    And header Content-Type = contentType
    And request { "name": "<name>", "email": "<email>", "gender": "<gender>", "status": "<status>", "phone": "<phone>" }
    When method post
    Then status 201
    And print 'Created user from JSON file:', response
    And match response.id == '#number'
    And match response.name == '<name>'
    And match response.email == '<email>'
    And match response.gender == '<gender>'
    And match response.status == '<status>'

    # read() here returns the JSON array as an Examples table.
    # Each object in the array becomes one row; keys become column headers.
    Examples:
      | read('classpath:practiceData/gorest/test-data/gorest_users_json.json') |
