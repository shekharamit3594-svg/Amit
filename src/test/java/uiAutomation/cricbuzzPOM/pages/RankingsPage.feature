@ignore
Feature: Rankings Page Object
  # ─── Page Object for the Cricbuzz ICC Rankings section ────────────────────────
  # Assumes the browser is already on the Rankings page.
  # Navigate there first via: call read(homePage) @clickRankings
  #
  # HOW TO USE IN A TEST FEATURE:
  #   * def rankingsPage = 'classpath:uiAutomation/cricbuzzPOM/pages/RankingsPage.feature'
  #
  #   Navigate to rankings, switch format tab, then query:
  #     * call read(homePage) @clickRankings
  #     * call read(rankingsPage) @clickODITab
  #     * call read(rankingsPage) @getRowCount
  #     * karate.log('ODI rows:', rowCount)
  #
  # Variable contract:
  #   @getRowCount      → exposes `rowCount`    (Number) — rows in the visible table body
  #   @getTopTeamInfo   → exposes `topTeamRank` (String) and `topTeamName` (String)

  # ── @verifyTableLoaded ────────────────────────────────────────────────────────
  @verifyTableLoaded
  Scenario: Assert rankings page URL and the main table container are visible
    Then match driver.url contains 'cricket-team-rankings'
    * waitFor('.table-responsive')
    And  assert exists('.table-responsive')

  # ── @clickTestTab ─────────────────────────────────────────────────────────────
  @clickTestTab
  Scenario: Click the Test format tab and wait for the table to re-render
    * waitFor("{a}Test")
    * click("{a}Test")
    * waitFor('.table-responsive')

  # ── @clickODITab ──────────────────────────────────────────────────────────────
  @clickODITab
  Scenario: Click the ODI format tab and wait for the table to re-render
    * waitFor("{a}ODI")
    * click("{a}ODI")
    * waitFor('.table-responsive')

  # ── @clickT20ITab ─────────────────────────────────────────────────────────────
  @clickT20ITab
  Scenario: Click the T20I format tab and wait for the table to re-render
    * waitFor("{a}T20I")
    * click("{a}T20I")
    * waitFor('.table-responsive')

  # ── @clickBattingTab ──────────────────────────────────────────────────────────
  @clickBattingTab
  Scenario: Click the Batting sub-tab to switch to player batting rankings
    * waitFor("{a}Batting")
    * click("{a}Batting")
    * waitFor('.table-responsive')

  # ── @clickBowlingTab ──────────────────────────────────────────────────────────
  @clickBowlingTab
  Scenario: Click the Bowling sub-tab to switch to player bowling rankings
    * waitFor("{a}Bowling")
    * click("{a}Bowling")
    * waitFor('.table-responsive')

  # ── @getRowCount ──────────────────────────────────────────────────────────────
  # Returns: rowCount (Number) — number of <tr> elements in the visible table body
  @getRowCount
  Scenario: Return the row count of the currently visible rankings table
    * def rowCount = script("document.querySelectorAll('.table-responsive tbody tr').length")
    Then match rowCount == '#number'

  # ── @getTopTeamInfo ───────────────────────────────────────────────────────────
  # Returns:
  #   topTeamRank (String) — rank number displayed in the first row (should be '1')
  #   topTeamName (String) — team or player name in the first row
  @getTopTeamInfo
  Scenario: Extract the rank number and name of the top entry in the current table
    * waitFor('.table-responsive tbody tr')
    * def topTeamRank = script("document.querySelector('.table-responsive tbody tr td:nth-child(1)').innerText.trim()")
    * def topTeamName = script("document.querySelector('.table-responsive tbody tr td:nth-child(2)').innerText.trim()")
    Then match topTeamRank == '1'
    And  match topTeamName == '#string'
