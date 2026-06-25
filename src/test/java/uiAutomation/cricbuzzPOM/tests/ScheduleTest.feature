Feature: Cricbuzz Schedule — POM Tests
  # ─── How this compares to the non-POM version (uiAutomation/cricbuzz/CricbuzzSchedule.feature) ───
  #
  # NON-POM (old):
  #   @InternationalTab, @DomesticTab, and @T20LeaguesTab each repeat the full
  #   navigation path (click Schedule → waitForUrl → waitFor tab → click tab)
  #   because there is no shared action to call.
  #
  # POM (this file):
  #   Background navigates to Schedule once.  Each Scenario adds exactly ONE
  #   tab-switch or assertion call.  The duplication from the old file is gone.
  # ─────────────────────────────────────────────────────────────────────────────────

  Background:
    * def homePage    = 'classpath:uiAutomation/cricbuzzPOM/pages/HomePage.feature'
    * def schedulePage = 'classpath:uiAutomation/cricbuzzPOM/pages/SchedulePage.feature'
    * call read(homePage) @navigate
    * call read(homePage) @clickSchedule

  # ─── Tag: @SchedulePageLoad ────────────────────────────────────────────────────
  @SchedulePageLoad @POM
  Scenario: Schedule page loads with the fixture list present
    * call read(schedulePage) @verifyPageLoaded
    * screenshot()

  # ─── Tag: @InternationalTab ────────────────────────────────────────────────────
  @InternationalTab @POM
  Scenario: International tab shows international fixtures after clicking
    * call read(schedulePage) @clickInternationalTab
    * screenshot()
    Then assert exists('.cb-sch-lst-itm')

  # ─── Tag: @DomesticTab ─────────────────────────────────────────────────────────
  @DomesticTab @POM
  Scenario: Domestic tab shows domestic fixtures after clicking
    * call read(schedulePage) @clickDomesticTab
    * screenshot()
    Then assert exists('.cb-sch-lst-itm')

  # ─── Tag: @T20LeaguesTab ───────────────────────────────────────────────────────
  @T20LeaguesTab @POM
  Scenario: T20 Leagues tab shows league fixtures after clicking
    * call read(schedulePage) @clickT20LeaguesTab
    * screenshot()
    Then assert exists('.cb-sch-lst-itm')

  # ─── Tag: @FixtureDetails ──────────────────────────────────────────────────────
  @FixtureDetails @POM
  Scenario: Fixture cards display team and date information
    * call read(schedulePage) @getFixtureDetails
    # `fixtureText` is exposed by @getFixtureDetails
    * karate.log('First fixture card text:', fixtureText)
    * screenshot()

  # ─── Tag: @ClickFixtureCard ────────────────────────────────────────────────────
  @ClickFixtureCard @POM
  Scenario: Clicking a fixture card navigates to the series detail page
    * call read(schedulePage) @clickFirstFixture
    * screenshot()
