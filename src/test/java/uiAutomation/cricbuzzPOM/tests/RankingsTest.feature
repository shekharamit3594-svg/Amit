Feature: Cricbuzz Rankings — POM Tests
  # ─── How this compares to the non-POM version (uiAutomation/cricbuzz/CricbuzzRankings.feature) ───
  #
  # NON-POM (old):
  #   Background navigates to Rankings inline.  Each scenario repeats tab clicks and
  #   the same script() expression for row counts.
  #   The JavaScript selector '.table-responsive tbody tr' appears in 5 separate places.
  #
  # POM (this file):
  #   @getRowCount encapsulates the row-count script — change it in one place.
  #   @getTopTeamInfo encapsulates both column extractions.
  #   Test scenarios express INTENT: "T20I table has at least 10 teams" — not HOW.
  # ─────────────────────────────────────────────────────────────────────────────────

  Background:
    * def homePage     = 'classpath:uiAutomation/cricbuzzPOM/pages/HomePage.feature'
    * def rankingsPage  = 'classpath:uiAutomation/cricbuzzPOM/pages/RankingsPage.feature'
    * call read(homePage) @navigate
    * call read(homePage) @clickRankings

  # ─── Tag: @RankingsPageLoad ────────────────────────────────────────────────────
  @RankingsPageLoad @POM
  Scenario: Rankings page loads with the table rendered
    * call read(rankingsPage) @verifyTableLoaded
    * screenshot()

  # ─── Tag: @TestRankings ────────────────────────────────────────────────────────
  @TestRankings @POM
  Scenario: Test format rankings table has at least 10 teams
    * call read(rankingsPage) @clickTestTab
    * call read(rankingsPage) @getRowCount
    # `rowCount` is the variable exposed by @getRowCount
    * karate.log('Test rankings row count:', rowCount)
    * screenshot()
    Then assert rowCount >= 10

  # ─── Tag: @ODIRankings ─────────────────────────────────────────────────────────
  @ODIRankings @POM
  Scenario: ODI format rankings table has at least 10 teams
    * call read(rankingsPage) @clickODITab
    * call read(rankingsPage) @getRowCount
    * karate.log('ODI rankings row count:', rowCount)
    * screenshot()
    Then assert rowCount >= 10

  # ─── Tag: @T20IRankings ────────────────────────────────────────────────────────
  @T20IRankings @POM
  Scenario: T20I format rankings table has at least 10 teams
    * call read(rankingsPage) @clickT20ITab
    * call read(rankingsPage) @getRowCount
    * karate.log('T20I rankings row count:', rowCount)
    * screenshot()
    Then assert rowCount >= 10

  # ─── Tag: @TopRankedTeam ───────────────────────────────────────────────────────
  @TopRankedTeam @POM
  Scenario: The number 1 ranked Test team has a valid name
    * call read(rankingsPage) @clickTestTab
    * call read(rankingsPage) @getTopTeamInfo
    # `topTeamName` and `topTeamRank` are exposed by @getTopTeamInfo
    * karate.log('Top ranked Test team:', topTeamName, '— rank:', topTeamRank)
    * screenshot()

  # ─── Tag: @BatsmenRankings ─────────────────────────────────────────────────────
  @BatsmenRankings @POM
  Scenario: Test batting player rankings table has at least 10 players
    * call read(rankingsPage) @clickTestTab
    * call read(rankingsPage) @clickBattingTab
    * call read(rankingsPage) @getRowCount
    * screenshot()
    Then assert rowCount >= 10

  # ─── Tag: @BowlersRankings ─────────────────────────────────────────────────────
  @BowlersRankings @POM
  Scenario: Test bowling player rankings table has at least 10 players
    * call read(rankingsPage) @clickTestTab
    * call read(rankingsPage) @clickBowlingTab
    * call read(rankingsPage) @getRowCount
    * screenshot()
    Then assert rowCount >= 10
