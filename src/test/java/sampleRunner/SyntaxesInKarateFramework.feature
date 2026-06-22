Feature: Understanding Different Syntaxes

  @KarateSyntax @VariableDefinition @PrintingStatements
  Scenario: Understanding the concept of definitions in a variable
    * def a = 10
    * def b = 20
    * def c = a+b
    * print c

  @KarateSyntax @ConcatenationOperation @PrintingStatements
  Scenario: Understanding the concept of printing statements
    * def name = 'Ashvini'
    * def age = 30
    * print 'Name:', name, ', Age:', age

  @KarateSyntax @ConcatenationOperation @PrintingStatements @MultipleDataSet
  Scenario Outline: Understanding the concept of printing statements for Multiple Data Set
    * def name = '<name>'
    * def age = <age>
    * print 'Name:', name, ', Age:', age

    Examples:
      | name    | age |
      | Ashvini | 30  |
      | John    | 25  |
      | Alice   | 28  |


  @KarateSyntax @JSONParsing @PrintingStatements
  Scenario: Understanding the concept of JSON Parsing and Printing Statements
    * def jsonRequest =
      """
      {
          "name": "Ashvini",
          "age": 30,
          "city": "Pune"
      }
      """
    * print jsonRequest['name']
    * print jsonRequest.age