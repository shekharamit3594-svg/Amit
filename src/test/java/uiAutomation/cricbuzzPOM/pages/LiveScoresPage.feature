@ignore
Feature: Live Scores Page Object
  # ─── Page Object for the Cricbuzz Live Scores section ─────────────────────────
  # Assumes the browser is already on the Live Scores page.
  # Navigate there first via: call read(homePage) @clickLiveScores
  #
  # HOW TO USE IN A TEST FEATURE:
  #   * def liveScoresPage = 'classpath:uiAutomation/cricbuzzPOM/pages/LiveScoresPage.feature'
  #
  #   Then in a Scenario:
  #     * call read(liveScoresPage) @verifyPageLoaded
  #     * call read(liveScoresPage) @getMatchCardCount
  #     * karate.log('Cards visible:', cardCount)
  #
  # Variable contract:
  #   @getMatchCardCount    → exposes  `cardCount` (Number) to the calling scope
  #   @verifyFirstCardContent → exposes `firstCardText` (String) to the calling scope

  # ── @verifyPageLoaded ─────────────────────────────────────────────────────────
  @verifyPageLoaded
  Scenario: Assert the live scores page URL and the main section container are present
    Then match driver.url contains 'live-scores'
    * waitFor('#main-nav-web')
    And  assert exists('#main-nav-web')

  # ── @getMatchCardCount ────────────────────────────────────────────────────────
  # Returns: cardCount (Number) — total score cards currently rendered in the DOM
  @getMatchCardCount
  Scenario: Scroll to the match cards section and return how many cards are rendered
    # scroll() ensures lazy-loaded content below the fold is triggered.
    * scroll('.cb-lv-scr-card')
    * waitFor('.cb-lv-scr-card')
    # script() counts DOM nodes — faster and more reliable than locateAll() for a count.
    * def cardCount = script("document.querySelectorAll('.cb-lv-scr-card').length")
    Then match cardCount == '#number'

  # ── @verifyFirstCardContent ───────────────────────────────────────────────────
  # Returns: firstCardText (String) — innerText of the first match card
  @verifyFirstCardContent
  Scenario: Read the visible text of the first live match card and assert it is non-empty
    * waitFor('.cb-lv-scr-card')
    * def firstCardText = text('.cb-lv-scr-card')
    Then assert firstCardText.length > 0

  # ── @waitForSpinnerGone ───────────────────────────────────────────────────────
  @waitForSpinnerGone
  Scenario: Poll until the loading spinner has disappeared from the DOM
    # retry(3, 2000) re-evaluates the block up to 3 times at 2 s intervals.
    # This prevents a false pass when the spinner is still mid-animation.
    * retry(3, 2000) { notExists('.cb-loading-spinner') }
    Then assert notExists('.cb-loading-spinner')

  # ── @clickFirstMatchCard ──────────────────────────────────────────────────────
  @clickFirstMatchCard
  Scenario: Click the first live match card link and wait for the scorecard page
    # waitFor().click() is the idiomatic one-liner: waits for presence then clicks.
    * waitFor('.cb-lv-scr-card a').click()
    * waitForUrl('cricket-scores')
    Then match driver.url contains 'cricket-scores'

  # ── @hoverOnMatchCard ─────────────────────────────────────────────────────────
  @hoverOnMatchCard
  Scenario: Move the mouse cursor over the first match card to trigger hover state
    * waitFor('.cb-lv-scr-card')
    * mouse().move('.cb-lv-scr-card')
