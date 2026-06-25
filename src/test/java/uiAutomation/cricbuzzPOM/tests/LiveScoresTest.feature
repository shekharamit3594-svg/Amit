Feature: Cricbuzz Live Scores — POM Tests
  # ─── How this compares to the non-POM version (uiAutomation/cricbuzz/CricbuzzLiveScores.feature) ─
  #
  # NON-POM (old):
  #   Background repeats navigation inline (configure driver + driver url + click + waitFor).
  #   Selectors like '.cb-lv-scr-card', '.cb-loading-spinner' appear in every scenario.
  #
  # POM (this file):
  #   Background composes two page objects: HomePage navigates to the site, then
  #   @clickLiveScores lands us on the scores section — zero selectors in Background.
  #   Each Scenario calls a named action; the intent is immediately readable.
  # ─────────────────────────────────────────────────────────────────────────────────

  Background:
    * def homePage      = 'classpath:uiAutomation/cricbuzzPOM/pages/HomePage.feature'
    * def liveScoresPage = 'classpath:uiAutomation/cricbuzzPOM/pages/LiveScoresPage.feature'
    # Navigate to homepage then click Live Scores — two composed page object calls.
    * call read(homePage) @navigate
    * call read(homePage) @clickLiveScores

  # ─── Tag: @LiveScoresPageLoad ─────────────────────────────────────────────────
  @LiveScoresPageLoad @POM
  Scenario: Live Scores page loads with the correct URL and section header
    * screenshot()
    * call read(liveScoresPage) @verifyPageLoaded

  # ─── Tag: @LiveMatchCards ─────────────────────────────────────────────────────
  @LiveMatchCards @POM
  Scenario: Live match score cards are rendered and the count is a positive number
    * call read(liveScoresPage) @getMatchCardCount
    # `cardCount` is the variable exposed by @getMatchCardCount
    * karate.log('Match cards visible on page:', cardCount)
    * screenshot()
    Then assert cardCount > 0

  # ─── Tag: @LiveScoreContent ───────────────────────────────────────────────────
  @LiveScoreContent @POM
  Scenario: The first live match card has non-empty visible content
    * call read(liveScoresPage) @verifyFirstCardContent
    # `firstCardText` is exposed by @verifyFirstCardContent
    * karate.log('First card text:', firstCardText)
    * screenshot()

  # ─── Tag: @SpinnerGone ────────────────────────────────────────────────────────
  @SpinnerGone @POM
  Scenario: The loading spinner disappears once scores have finished loading
    * call read(liveScoresPage) @waitForSpinnerGone

  # ─── Tag: @ClickMatchCard ─────────────────────────────────────────────────────
  @ClickMatchCard @POM
  Scenario: Clicking a live match card navigates to the full scorecard page
    * call read(liveScoresPage) @clickFirstMatchCard
    * screenshot()

  # ─── Tag: @HoverOnCard ────────────────────────────────────────────────────────
  @HoverOnCard @POM
  Scenario: Hovering over a match card triggers its hover state without errors
    * call read(liveScoresPage) @hoverOnMatchCard
    * screenshot()
    Then assert exists('.cb-lv-scr-card')
