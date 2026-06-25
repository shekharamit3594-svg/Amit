Feature: Cricbuzz Homepage UI Verification
  # UI Concepts Covered:
  #   1. configure driver  - set up the browser (type, timeout, addOptions)
  #   2. driver 'url'      - open the browser and load a page
  #   3. driver.title      - read the browser tab title
  #   4. driver.url        - read the current URL
  #   5. waitFor()         - wait until an element appears in the DOM
  #   6. exists()          - check presence without throwing on failure
  #   7. screenshot()      - capture the current browser view
  #   8. {tag}text         - Karate text-based selector (finds by visible label)
  #   9. attribute()       - read an HTML attribute value

  Background:
    # driverConfig is defined in karate-config.js and switches automatically:
    #   dev    → local Chrome with addOptions (chromedriver must be on PATH)
    #   docker → remote WebDriver pointing to the Selenium Grid hub
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'

  # ─── Tag: @HomePageLoad ──────────────────────────────────────────────────────
  @HomePageLoad @HomePageVerification
  Scenario: Verify Cricbuzz homepage loads with the correct browser title
    # screenshot() takes a snapshot of the whole viewport at this moment.
    # Karate embeds screenshots in the HTML report automatically.
    * screenshot()
    # driver.title is a built-in Karate variable that maps to document.title.
    Then match driver.title contains 'Cricbuzz'
    # driver.url mirrors window.location.href at this instant.
    And match driver.url contains 'cricbuzz.com'

  # ─── Tag: @NavBarVerification ────────────────────────────────────────────────
  @NavBarVerification @HomePageVerification
  Scenario: Verify all primary navigation links are visible in the header
    # waitFor() blocks until the selector appears or timeout is reached.
    # It throws a clear error if the element never shows up.
    * waitFor('#main-header + div')
    # {a}Live is Karate's text-based selector shorthand.
    # It finds the first <a> element whose visible text equals 'Live'.
    # exists() returns true/false — it never throws, making it safe for assertions.
    Then assert exists("{a}Live Scores")
    And  assert exists("{a}Schedule")
    And  assert exists("{a}Series")
    And  assert exists("{a}Rankings")
    And  assert exists("{a}News")
    And  assert exists("{a}Videos")
    * screenshot()

  # ─── Tag: @LogoVerification ──────────────────────────────────────────────────
  @LogoVerification @HomePageVerification
  Scenario: Verify the Cricbuzz logo is present and has the correct alt text
    * waitFor('#main-header + div')
    Then assert exists('#main-header + div')
    # attribute(selector, attrName) reads any HTML attribute of a matched element.
    * def hyperlink = attribute('#main-header + div > div use', 'href')
    And match hyperlink contains 'cricbuzz'
    * screenshot()

  # ─── Tag: @NavigateFromHome ───────────────────────────────────────────────────
  @NavigateFromHome @HomePageVerification
  Scenario: Click the Live link from the homepage and verify navigation
    * waitFor('#main-header + div')
    # click() dispatches a mouse-click on the matched element.
    # The {a}Live Score text selector is the cleanest way to click by visible label.
    * click("{a}Live Scores")
    # waitForUrl() blocks until the current URL contains the given substring.
    # This is the standard way to wait for page transitions in Karate.
    * waitForUrl('live-scores')
    Then match driver.url contains 'live-scores'
    * screenshot()
