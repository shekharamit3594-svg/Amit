Feature: Cricbuzz Homepage — POM Tests
  # ─── How this compares to the non-POM version (uiAutomation/cricbuzz/CricbuzzHomePage.feature) ───
  #
  # NON-POM (old):
  #   Every scenario repeats the full setup inline:
  #     Background:
  #       * configure driver = driverConfig
  #       * driver 'https://www.cricbuzz.com'
  #     Scenario: Verify nav bar
  #       * waitFor('#main-header + div')
  #       Then assert exists("{a}Live Scores")
  #       ...
  #
  # POM (this file):
  #   Background calls the page object to open the browser and land on the page.
  #   Each Scenario composes page-level ACTIONS — no selectors or waits in this file.
  #   Selectors live ONLY in pages/HomePage.feature; change them in one place.
  #
  #   Background:
  #     * call read(homePage) @navigate      ← opens browser, single definition
  #   Scenario: Verify nav bar
  #     * call read(homePage) @verifyNavBar  ← action from the page object
  # ────────────────────────────────────────────────────────────────────────────────

  Background:
    * def homePage = 'classpath:uiAutomation/cricbuzzPOM/pages/HomePage.feature'
    # @navigate opens the browser and lands on https://www.cricbuzz.com.
    # Every scenario in this feature starts from the homepage with a fresh browser session.
    * call read(homePage) @navigate

  # ─── Tag: @HomePageLoad ──────────────────────────────────────────────────────
  @HomePageLoad @POM
  Scenario: Homepage loads with the correct browser title and URL
    * screenshot()
    * call read(homePage) @verifyTitle

  # ─── Tag: @NavBarVerification ────────────────────────────────────────────────
  @NavBarVerification @POM
  Scenario: All six primary navigation links are visible in the header
    * call read(homePage) @verifyNavBar
    * screenshot()

  # ─── Tag: @LogoVerification ──────────────────────────────────────────────────
  @LogoVerification @POM
  Scenario: Cricbuzz logo is present and its href references the brand
    * call read(homePage) @verifyLogo
    * screenshot()

  # ─── Tag: @NavigateToLiveScores ──────────────────────────────────────────────
  @NavigateToLiveScores @POM
  Scenario: Clicking Live Scores from the homepage navigates to the scores section
    * call read(homePage) @clickLiveScores
    Then match driver.url contains 'live-scores'
    * screenshot()

  # ─── Tag: @NavigateToSchedule ────────────────────────────────────────────────
  @NavigateToSchedule @POM
  Scenario: Clicking Schedule from the homepage navigates to the schedule section
    * call read(homePage) @clickSchedule
    Then match driver.url contains 'cricket-schedule'
    * screenshot()

  # ─── Tag: @NavigateToRankings ────────────────────────────────────────────────
  @NavigateToRankings @POM
  Scenario: Clicking Rankings from the homepage navigates to the ICC rankings section
    * call read(homePage) @clickRankings
    Then match driver.url contains 'cricket-team-rankings'
    * screenshot()
