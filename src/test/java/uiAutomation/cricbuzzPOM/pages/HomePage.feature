@ignore
Feature: Home Page Object
  # ─── Page Object for the Cricbuzz Homepage ────────────────────────────────────
  # This file ONLY defines reusable page actions — it never runs as a standalone test.
  # The @ignore tag on the Feature line prevents Karate runners from executing it directly.
  #
  # HOW TO USE IN A TEST FEATURE:
  #   * def homePage = 'classpath:uiAutomation/cricbuzzPOM/pages/HomePage.feature'
  #
  #   Open browser and land on homepage:
  #     * call read(homePage) @navigate
  #
  #   Perform an action (driver session is already open from @navigate):
  #     * call read(homePage) @clickLiveScores
  #
  # Each tagged Scenario below is one page-level action.
  # Actions that do NOT open the browser assume it is already running.

  # ── @navigate ─────────────────────────────────────────────────────────────────
  # Opens the browser and lands on the Cricbuzz homepage.
  # Must be called FIRST before any other action on this page.
  @navigate
  Scenario: Open browser and navigate to the Cricbuzz homepage
    # driverConfig is defined in karate-config.js.
    # dev env  → local Chrome   (chromedriver must be on PATH)
    # docker   → remote WebDriver pointing to Selenium Grid hub
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')

  # ── @verifyTitle ──────────────────────────────────────────────────────────────
  @verifyTitle
  Scenario: Assert browser title and current URL contain Cricbuzz markers
    * def pageTitle = driver.title
    * def pageUrl   = driver.url
    Then match pageTitle contains 'Cricbuzz'
    And  match pageUrl   contains 'cricbuzz.com'

  # ── @verifyNavBar ─────────────────────────────────────────────────────────────
  @verifyNavBar
  Scenario: Assert all six primary navigation links are visible in the header
    * waitFor('#main-header + div')
    Then assert exists("{a}Live Scores")
    And  assert exists("{a}Schedule")
    And  assert exists("{a}Series")
    And  assert exists("{a}Rankings")
    And  assert exists("{a}News")
    And  assert exists("{a}Videos")

  # ── @verifyLogo ───────────────────────────────────────────────────────────────
  @verifyLogo
  Scenario: Assert the Cricbuzz logo element is present and references the brand name
    * waitFor('#main-header + div')
    Then assert exists('#main-header + div')
    * def hyperlink = attribute('#main-header + div > div use', 'href')
    And  match hyperlink contains 'cricbuzz'

  # ── @clickLiveScores ─────────────────────────────────────────────────────────
  @clickLiveScores
  Scenario: Click the Live Scores nav link and wait for the page transition
    * click("{a}Live Scores")
    * waitForUrl('live-scores')

  # ── @clickSchedule ───────────────────────────────────────────────────────────
  @clickSchedule
  Scenario: Click the Schedule nav link and wait for the page transition
    * click("{a}Schedule")
    * waitForUrl('cricket-schedule')

  # ── @clickRankings ───────────────────────────────────────────────────────────
  @clickRankings
  Scenario: Click the Rankings nav link and wait for the page transition
    * click("{a}Rankings")
    * waitForUrl('cricket-team-rankings')
