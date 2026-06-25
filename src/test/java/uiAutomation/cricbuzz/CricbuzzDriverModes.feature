Feature: Cricbuzz Driver Configuration Modes
  # UI Concepts Covered:
  #   1. type: 'chromeheadless'      - run Chrome with no visible window (CI mode)
  #   2. Per-scenario driver override - configure driver inside a scenario to bypass
  #                                    the global driverConfig for that one scenario
  #   3. configure afterScenario     - hook that auto-captures a screenshot when
  #                                    a scenario fails (set in Background)
  #
  # NOTE: No global Background here — configure driver must be set before the
  # first driver 'url' call, so headless and override scenarios manage their own setup.

  # ─── Tag: @HeadlessMode ────────────────────────────────────────────────────────
  @HeadlessMode @DriverModes
  Scenario: Run a Cricbuzz smoke test in headless Chrome (no visible browser window)
    # type: 'chromeheadless' starts Chrome in headless mode — no GPU, no window.
    # All Karate driver APIs (click, input, screenshot, script, etc.) work identically.
    # Headless is ideal for CI pipelines that have no desktop environment.
    # --window-size is required in headless mode since there is no physical display.
    * configure driver = { type: 'chromeheadless', timeout: 20000, addOptions: ['--no-sandbox', '--disable-dev-shm-usage', '--window-size=1920,1080'] }
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')
    * screenshot()
    Then match driver.title contains 'Cricbuzz'
    And  match driver.url contains 'cricbuzz.com'
    And  assert exists("{a}Live")
    * screenshot()

  # ─── Tag: @PerScenarioDriver ───────────────────────────────────────────────────
  @PerScenarioDriver @DriverModes
  Scenario: Override global driverConfig for this scenario with a longer timeout
    # configure driver set inside a scenario replaces driverConfig only for that scenario.
    # The next scenario resets to the global driverConfig from karate-config.js.
    # Use this when one scenario needs different settings — a longer timeout, a
    # different browser type, or extra Chrome flags — without changing the global config.
    * configure driver = { type: 'chrome', timeout: 45000, addOptions: ['--no-sandbox', '--disable-dev-shm-usage', '--start-maximized'] }
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')
    Then match driver.title contains 'Cricbuzz'
    * screenshot()

  # ─── Tag: @AfterScenarioHook ───────────────────────────────────────────────────
  @AfterScenarioHook @DriverModes
  Scenario: Demonstrate configure afterScenario for automatic failure screenshots
    # configure afterScenario registers a JS function that Karate calls after every
    # scenario in this feature. If the scenario failed, karate.info.errorMessage
    # is non-null and we embed a screenshot into the HTML report automatically.
    #
    # In karate-config.js this is set globally for all features.
    # Here it is shown as a Background-level override for documentation purposes.
    * configure afterScenario = function(){ if (karate.info.errorMessage) { try { karate.embed(karate.screenshot(), 'image/png') } catch(e) {} } }
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')
    # This scenario passes — but if it had failed, a screenshot would appear
    # automatically in the HTML report under the failed step.
    Then match driver.title contains 'Cricbuzz'
    * screenshot()
