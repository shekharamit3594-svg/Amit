@ignore
Feature: Schedule Page Object
  # ─── Page Object for the Cricbuzz Schedule section ────────────────────────────
  # Assumes the browser is already on the Schedule page.
  # Navigate there first via: call read(homePage) @clickSchedule
  #
  # HOW TO USE IN A TEST FEATURE:
  #   * def schedulePage = 'classpath:uiAutomation/cricbuzzPOM/pages/SchedulePage.feature'
  #
  #   Then in a Scenario:
  #     * call read(schedulePage) @verifyPageLoaded
  #     * call read(schedulePage) @clickInternationalTab
  #
  # Variable contract:
  #   @getFixtureDetails → exposes `fixtureText` (String) to the calling scope

  # ── @verifyPageLoaded ─────────────────────────────────────────────────────────
  @verifyPageLoaded
  Scenario: Assert the schedule page URL and fixture list container are present
    Then match driver.url contains 'cricket-schedule'
    * waitFor('.cb-sch-lst-itm')
    And  assert exists('.cb-sch-lst-itm')

  # ── @clickInternationalTab ────────────────────────────────────────────────────
  @clickInternationalTab
  Scenario: Click the International tab and wait for its fixture list to render
    * waitFor("{span}International")
    * click("{span}International")
    * waitFor('.cb-sch-lst-itm')

  # ── @clickDomesticTab ─────────────────────────────────────────────────────────
  @clickDomesticTab
  Scenario: Click the Domestic tab and wait for its fixture list to render
    * waitFor("{span}Domestic")
    * click("{span}Domestic")
    * waitFor('.cb-sch-lst-itm')

  # ── @clickT20LeaguesTab ───────────────────────────────────────────────────────
  @clickT20LeaguesTab
  Scenario: Click the T20 Leagues tab and wait for its fixture list to render
    * waitFor("{span}T20 Leagues")
    * click("{span}T20 Leagues")
    * waitFor('.cb-sch-lst-itm')

  # ── @getFixtureDetails ────────────────────────────────────────────────────────
  # Returns: fixtureText (String) — visible text of the first fixture card
  @getFixtureDetails
  Scenario: Scroll to the first fixture card and return its visible text
    * scroll('.cb-sch-lst-itm')
    * waitFor('.cb-sch-lst-itm')
    * def fixtureText = text('.cb-sch-lst-itm')
    Then assert fixtureText.length > 0

  # ── @clickFirstFixture ────────────────────────────────────────────────────────
  @clickFirstFixture
  Scenario: Click the first fixture card link and wait for the series detail page
    * waitFor('.cb-sch-lst-itm a')
    * click('.cb-sch-lst-itm a')
    * waitForUrl('cricket-series')
    Then match driver.url contains 'cricket-series'
