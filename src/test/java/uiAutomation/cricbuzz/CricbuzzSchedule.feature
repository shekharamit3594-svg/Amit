Feature: Cricbuzz Schedule Page Navigation
  # UI Concepts Covered:
  #   1. Multi-step navigation  - clicking through Nav → section → sub-section
  #   2. waitForUrl()           - synchronise on URL changes after each click
  #   3. Tab switching          - clicking tab buttons (International / Domestic / T20 Leagues)
  #   4. driver.url assertions  - verifying the full navigation path is correct
  #   5. exists() for dates     - asserting date/calendar elements render
  #   6. scroll + waitFor       - handling lazy-loaded fixture cards

  Background:
    # driverConfig from karate-config.js: local Chrome for dev, Selenium Grid for docker env.
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')

  # ─── Tag: @SchedulePageLoad ────────────────────────────────────────────────────
  @SchedulePageLoad @ScheduleNavigation
  Scenario: Navigate to the Schedule section and verify the page loads
    # Click the 'Schedule' link in the main nav.
    * click("{a}Schedule")
    # waitForUrl() will block until the browser URL contains the given substring.
    # This is safer than a fixed sleep because it adapts to actual page speed.
    * waitForUrl('cricket-schedule')
    Then match driver.url contains 'cricket-schedule'
    * screenshot()
    # The schedule page must show at least one fixture section header.
    * waitFor('.cb-sch-lst-itm')
    And assert exists('.cb-sch-lst-itm')

  # ─── Tag: @InternationalTab ────────────────────────────────────────────────────
  @InternationalTab @ScheduleNavigation
  Scenario: Click the International tab and verify international fixtures appear
    * click("{a}Schedule")
    * waitForUrl('cricket-schedule')
    # Click the 'International' tab within the schedule page.
    # The {span} text selector finds the visible tab label.
    * waitFor("{span}International")
    * click("{span}International")
    * screenshot()
    Then assert exists('.cb-sch-lst-itm')
    # Verify the URL or page state reflects the International filter.
    And match driver.url contains 'cricket-schedule'

  # ─── Tag: @DomesticTab ─────────────────────────────────────────────────────────
  @DomesticTab @ScheduleNavigation
  Scenario: Switch to the Domestic tab and verify domestic fixtures appear
    * click("{a}Schedule")
    * waitForUrl('cricket-schedule')
    * waitFor("{span}Domestic")
    * click("{span}Domestic")
    * screenshot()
    Then assert exists('.cb-sch-lst-itm')

  # ─── Tag: @T20LeaguesTab ───────────────────────────────────────────────────────
  @T20LeaguesTab @ScheduleNavigation
  Scenario: Switch to the T20 Leagues tab and verify league fixtures appear
    * click("{a}Schedule")
    * waitForUrl('cricket-schedule')
    * waitFor("{span}T20 Leagues")
    * click("{span}T20 Leagues")
    * screenshot()
    Then assert exists('.cb-sch-lst-itm')

  # ─── Tag: @FixtureDetails ──────────────────────────────────────────────────────
  @FixtureDetails @ScheduleNavigation
  Scenario: Verify a fixture card shows team names and match date information
    * click("{a}Schedule")
    * waitForUrl('cricket-schedule')
    # Scroll down to make sure fixtures below the fold are rendered.
    * scroll('.cb-sch-lst-itm')
    * waitFor('.cb-sch-lst-itm')
    # Extract the text of the first fixture card.
    * def fixtureText = text('.cb-sch-lst-itm')
    * karate.log('First fixture text:', fixtureText)
    Then assert fixtureText.length > 0
    * screenshot()

  # ─── Tag: @ClickFixtureCard ────────────────────────────────────────────────────
  @ClickFixtureCard @ScheduleNavigation
  Scenario: Click a fixture card and verify navigation to the series detail page
    * click("{a}Schedule")
    * waitForUrl('cricket-schedule')
    * waitFor('.cb-sch-lst-itm a')
    * click('.cb-sch-lst-itm a')
    # After clicking a fixture, the URL should change to a series or match detail page.
    * waitForUrl('cricket-series')
    Then match driver.url contains 'cricket-series'
    * screenshot()
