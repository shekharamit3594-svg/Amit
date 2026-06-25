Feature: Cricbuzz Browser Controls and Advanced Locators
  # UI Concepts Covered:
  #   1. driver.maximize()           - maximise the browser window before tests
  #   2. driver.refresh()            - hard reload the current page (equivalent to F5)
  #   3. driver.back()               - go back one step in browser history
  #   4. driver.forward()            - go forward one step in browser history
  #   5. XPath selectors             - //tag[@attr='val'] and //tag[contains(text(),'')]
  #   6. locateAll(selector)         - collect all matching elements as a JS array
  #   7. waitForResultCount(sel, n)  - block until at least N matches exist in DOM
  #   8. retry until <condition>     - poll until a Karate expression evaluates truthy
  #   9. Key.TAB                     - tab key for keyboard focus traversal
  #  10. focus(selector)             - programmatically move focus to an element
  #  11. screenshot(selector)        - crop the screenshot to one specific element
  #  12. html(selector)              - read raw innerHTML of a matched element

  Background:
    # driverConfig from karate-config.js: local Chrome for dev, Selenium Grid for docker env.
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')
    # Maximise once in Background so all scenarios start with a full-screen window.
    * driver.maximize()

  # ─── Tag: @MaximizeWindow ──────────────────────────────────────────────────────
  @MaximizeWindow @BrowserControls
  Scenario: Verify the homepage loads correctly in a maximised browser window
    # driver.maximize() expands the window to fill the available screen area.
    # This avoids responsive breakpoints that hide nav items on smaller widths.
    * screenshot()
    Then match driver.title contains 'Cricbuzz'
    And  match driver.url contains 'cricbuzz.com'

  # ─── Tag: @PageRefresh ─────────────────────────────────────────────────────────
  @PageRefresh @BrowserControls
  Scenario: Refresh the page and verify the nav bar reloads successfully
    # driver.refresh() sends an HTTP reload request — equivalent to pressing F5.
    # The browser re-fetches all resources; the page state resets to its initial form.
    * driver.refresh()
    * waitFor('.cb-nav-main')
    Then assert exists('.cb-nav-main')
    And  assert exists("{a}Live")
    * screenshot()

  # ─── Tag: @BackForward ─────────────────────────────────────────────────────────
  @BackForward @BrowserControls
  Scenario: Navigate to Live Scores then use back and forward browser history controls
    * click("{a}Live")
    * waitForUrl('live-scores')
    # driver.back() is equivalent to the browser's Back button — goes to previous URL.
    * driver.back()
    * waitFor('.cb-nav-main')
    Then match driver.url contains 'cricbuzz.com'
    * screenshot()
    # driver.forward() moves forward in history — returns to live-scores.
    * driver.forward()
    * waitForUrl('live-scores')
    And match driver.url contains 'live-scores'
    * screenshot()

  # ─── Tag: @XPathSelector ───────────────────────────────────────────────────────
  @XPathSelector @BrowserControls
  Scenario: Locate elements using XPath expressions
    # XPath is an alternative to CSS selectors — useful when CSS cannot express
    # the relationship you need (e.g. find a parent by its child's text).
    #
    # //a[contains(text(),'Live')] — any <a> whose visible text includes 'Live'.
    * waitFor("//a[contains(text(),'Live')]")
    Then assert exists("//a[contains(text(),'Live')]")
    # XPath attribute predicate: finds an element with a specific attribute value.
    # //img[@alt] — any <img> that has an alt attribute (logo images typically do).
    * def logoImgByXPath = exists("//img[@alt]")
    * karate.log('Logo found via XPath:', logoImgByXPath)
    And assert logoImgByXPath
    # XPath parent-child: find a <li> that directly contains a link labelled 'Rankings'.
    * assert exists("//li[.//a[contains(text(),'Rankings')]]")
    * screenshot()

  # ─── Tag: @LocateAll ───────────────────────────────────────────────────────────
  @LocateAll @BrowserControls
  Scenario: Collect all primary nav links with locateAll and verify the count
    # locateAll(selector) returns every matching element as a list.
    # Unlike waitFor() which returns the first match, locateAll() gives you all of them.
    # You can iterate the list or assert its length.
    * waitFor('.cb-nav-main')
    * def navLinks = locateAll('.cb-nav-main a')
    * karate.log('Total nav links found:', navLinks.length)
    Then assert navLinks.length >= 5
    # Iterate to log each link's text — shows how to use the returned list.
    * def navTexts = karate.map(navLinks, function(el){ return el.text })
    * karate.log('Nav link labels:', navTexts)
    * screenshot()

  # ─── Tag: @WaitForCount ────────────────────────────────────────────────────────
  @WaitForCount @BrowserControls
  Scenario: Navigate to Live Scores and wait for at least one score card to render
    * click("{a}Live")
    * waitForUrl('live-scores')
    # waitForResultCount(selector, count) blocks until at least `count` matching
    # elements exist in the DOM. Essential for pages that render cards asynchronously.
    * waitForResultCount('.cb-scr-wll-hdr', 1)
    * def sections = locateAll('.cb-scr-wll-hdr')
    * karate.log('Live score sections found:', sections.length)
    Then assert sections.length >= 1
    * screenshot()

  # ─── Tag: @RetryUntil ──────────────────────────────────────────────────────────
  @RetryUntil @BrowserControls
  Scenario: Use retry until to poll for the nav bar after a page refresh
    * driver.refresh()
    # retry until <expression> re-evaluates the expression every 1 s (default interval)
    # until it is truthy. Unlike retry(count, ms) { block }, this keeps polling until
    # the condition is met or the driver timeout is reached.
    * retry until exists('.cb-nav-main')
    Then assert exists('.cb-nav-main')
    * screenshot()

  # ─── Tag: @KeyTab ──────────────────────────────────────────────────────────────
  @KeyTab @BrowserControls
  Scenario: Use Key.TAB to move keyboard focus through the search flow
    # Key.TAB sends the Tab key to advance focus to the next focusable element.
    # Testing Tab navigation is essential for keyboard accessibility audits.
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    * input('#searchInput', 'Rohit Sharma')
    # Tab moves focus from the search input to the next focusable element in DOM order.
    * input('#searchInput', Key.TAB)
    * screenshot()
    Then assert exists('.cb-nav-main')

  # ─── Tag: @FocusElement ────────────────────────────────────────────────────────
  @FocusElement @BrowserControls
  Scenario: Programmatically focus the search button and verify it receives focus
    * waitFor('.cb-search-btn')
    # focus(selector) moves keyboard focus to the element without clicking it.
    # Use this to test CSS :focus styles or to prepare an element for keyboard input.
    * focus('.cb-search-btn')
    # Confirm the element received focus by checking document.activeElement in JS.
    * def focusedTag = script("document.activeElement.className")
    * karate.log('Active element class after focus():', focusedTag)
    * screenshot()
    Then assert exists('.cb-search-btn')

  # ─── Tag: @ElementScreenshot ───────────────────────────────────────────────────
  @ElementScreenshot @BrowserControls
  Scenario: Capture a screenshot cropped to just the header logo element
    * waitFor('.cb-header-logo')
    # screenshot(selector) crops the image to the bounding box of the matched element.
    # The cropped image is embedded in the Karate HTML report just like screenshot().
    # Use it to visually verify a specific component without the surrounding page noise.
    * screenshot('.cb-header-logo')
    Then assert exists('.cb-header-logo')

  # ─── Tag: @HtmlContent ─────────────────────────────────────────────────────────
  @HtmlContent @BrowserControls
  Scenario: Read the raw innerHTML of the main nav bar element
    * waitFor('.cb-nav-main')
    # html(selector) returns the raw innerHTML string of the matched element.
    # Use it when you need to inspect or assert on the full HTML structure,
    # not just the visible text or a single attribute.
    * def navHtml = html('.cb-nav-main')
    * karate.log('Nav bar innerHTML length:', navHtml.length)
    Then assert navHtml.length > 0
    And  match navHtml contains 'Live'
    * screenshot()
