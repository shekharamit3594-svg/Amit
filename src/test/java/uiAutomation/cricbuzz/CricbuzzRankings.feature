Feature: Cricbuzz ICC Rankings Page
  # UI Concepts Covered:
  #   1. Tab switching         - clicking Test / ODI / T20I format tabs
  #   2. script()              - extract table row count using JavaScript
  #   3. text()                - read the #1 ranked team/player name
  #   4. Batsmen / Bowlers tab - switching between sub-category tabs
  #   5. karate.log()          - printing extracted values to the Karate report
  #   6. waitFor chaining      - waitFor then immediately assert text or count
  #   7. match #number         - fuzzy assertion to confirm a value is numeric

  Background:
    # driverConfig from karate-config.js: local Chrome for dev, Selenium Grid for docker env.
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')
    # Navigate to Rankings from the top nav.
    * click("{a}Rankings")
    * waitForUrl('cricket-team-rankings')
    * screenshot()

  # ─── Tag: @RankingsPageLoad ────────────────────────────────────────────────────
  @RankingsPageLoad @RankingsNavigation
  Scenario: Verify the Rankings page loads and the table is rendered
    Then match driver.url contains 'cricket-team-rankings'
    # The main rankings table must be present.
    * waitFor('.table-responsive')
    And assert exists('.table-responsive')
    * screenshot()

  # ─── Tag: @TestRankings ────────────────────────────────────────────────────────
  @TestRankings @RankingsNavigation
  Scenario: Verify Test match team rankings table shows at least 10 teams
    # The Test tab is the default landing tab for Rankings.
    * waitFor("{a}Test")
    * click("{a}Test")
    * waitFor('.table-responsive')
    # script() executes a JavaScript expression in the live browser context.
    # querySelectorAll returns a NodeList; .length gives the count of <tr> elements.
    * def rowCount = script("document.querySelectorAll('.table-responsive tbody tr').length")
    * karate.log('Test rankings row count:', rowCount)
    # #number is a Karate fuzzy type matcher — asserts the value is numeric.
    Then match rowCount == '#number'
    # ICC Test rankings always include at least 10 full-member nations.
    And assert rowCount >= 10
    * screenshot()

  # ─── Tag: @ODIRankings ─────────────────────────────────────────────────────────
  @ODIRankings @RankingsNavigation
  Scenario: Switch to the ODI tab and verify the ODI team rankings table loads
    * waitFor("{a}ODI")
    * click("{a}ODI")
    # After clicking the tab, the table should re-render with ODI data.
    * waitFor('.table-responsive')
    * def rowCount = script("document.querySelectorAll('.table-responsive tbody tr').length")
    * karate.log('ODI rankings row count:', rowCount)
    Then match rowCount == '#number'
    And assert rowCount >= 10
    * screenshot()

  # ─── Tag: @T20IRankings ────────────────────────────────────────────────────────
  @T20IRankings @RankingsNavigation
  Scenario: Switch to the T20I tab and verify T20I team rankings table loads
    * waitFor("{a}T20I")
    * click("{a}T20I")
    * waitFor('.table-responsive')
    * def rowCount = script("document.querySelectorAll('.table-responsive tbody tr').length")
    * karate.log('T20I rankings row count:', rowCount)
    Then match rowCount == '#number'
    And assert rowCount >= 10
    * screenshot()

  # ─── Tag: @TopRankedTeam ───────────────────────────────────────────────────────
  @TopRankedTeam @RankingsNavigation
  Scenario: Read the #1 ranked Test team and log it to the report
    * waitFor("{a}Test")
    * click("{a}Test")
    * waitFor('.table-responsive tbody tr')
    # The first <td> in the first data row holds the rank number.
    # The second <td> in the first data row holds the team name.
    # We use a JavaScript snippet to extract both values at once.
    * def topTeamRank = script("document.querySelector('.table-responsive tbody tr td:nth-child(1)').innerText.trim()")
    * def topTeamName = script("document.querySelector('.table-responsive tbody tr td:nth-child(2)').innerText.trim()")
    * karate.log('Top ranked Test team is:', topTeamName, 'with rank:', topTeamRank)
    Then match topTeamRank == '1'
    And  match topTeamName == '#string'
    * screenshot()

  # ─── Tag: @BatsmenRankings ─────────────────────────────────────────────────────
  @BatsmenRankings @RankingsNavigation
  Scenario: Navigate to Test Batting player rankings and verify top 10 batsmen
    # Cricbuzz Rankings page has sub-tabs: Teams | Batsmen | Bowlers | All-rounders
    * waitFor("{a}Batting")
    * click("{a}Batting")
    * waitFor('.table-responsive')
    * def batsmenCount = script("document.querySelectorAll('.table-responsive tbody tr').length")
    * karate.log('Batsmen rankings rows:', batsmenCount)
    Then match batsmenCount == '#number'
    And assert batsmenCount >= 10
    * screenshot()

  # ─── Tag: @BowlersRankings ─────────────────────────────────────────────────────
  @BowlersRankings @RankingsNavigation
  Scenario: Navigate to Test Bowling player rankings and verify top 10 bowlers
    * waitFor("{a}Bowling")
    * click("{a}Bowling")
    * waitFor('.table-responsive')
    * def bowlersCount = script("document.querySelectorAll('.table-responsive tbody tr').length")
    * karate.log('Bowlers rankings rows:', bowlersCount)
    Then match bowlersCount == '#number'
    And assert bowlersCount >= 10
    * screenshot()
