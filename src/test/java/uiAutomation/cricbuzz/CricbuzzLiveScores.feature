Feature: Cricbuzz Live Scores Section
  # UI Concepts Covered:
  #   1. scroll()          - scroll the viewport to bring an element into view
  #   2. script()          - execute raw JavaScript and capture its return value
  #   3. retry()           - retry a failing assertion a fixed number of times
  #   4. waitFor() chain   - waitFor returns the element so you can .click() it inline
  #   5. mouse hover       - mouse().move(selector) to trigger CSS :hover states
  #   6. Dynamic content   - patterns for pages that load content via AJAX/lazy load
  #   7. notExists()       - assert that a loading spinner has disappeared

  Background:
    # driverConfig from karate-config.js: local Chrome for dev, Selenium Grid for docker env.
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    # Navigate to the Live Scores section from the nav bar.
    * waitFor('#main-header + div')
    * click("{a}Live Scores")
    * waitForUrl('live-scores')

  # ─── Tag: @LiveScoresPageLoad ─────────────────────────────────────────────────
  @LiveScoresPageLoad @LiveScores
  Scenario: Verify the Live Scores page loads and the section header is present
    * screenshot()
    Then match driver.url contains 'live-scores'
    # The main scores container should be present on the page.
    * waitFor('#main-nav-web')
    And assert exists('#main-nav-web')

  # ─── Tag: @LiveMatchCards ─────────────────────────────────────────────────────
  @LiveMatchCards @LiveScores
  Scenario: Verify live match score cards are rendered on the page
    # scroll() scrolls the page so the element is visible in the viewport.
    # On long pages this also triggers lazy-loaded content to render.
    * scroll('.cb-lv-scr-card')
    * waitFor('.cb-lv-scr-card')
    # script() executes a JavaScript snippet inside the browser and returns the result.
    # Here we count how many score cards are currently rendered in the DOM.
    * def cardCount = script("document.querySelectorAll('.cb-lv-scr-card').length")
    Then match cardCount == '#number'
    * karate.log('Live match cards found on page:', cardCount)
    * screenshot()

  # ─── Tag: @LiveScoreContent ───────────────────────────────────────────────────
  @LiveScoreContent @LiveScores
  Scenario: Verify a live score card contains team names and a score or status
    * waitFor('.cb-lv-scr-card')
    # text(selector) returns the trimmed innerText of the first matched element.
    * def firstCardText = text('.cb-lv-scr-card')
    * karate.log('First score card content:', firstCardText)
    # A live score card should show at least one of: a team name, a score, or a status label.
    # We use a JavaScript expression to check the text is not empty.
    Then assert firstCardText.length > 0
    * screenshot()

  # ─── Tag: @SpinnerGone ────────────────────────────────────────────────────────
  @SpinnerGone @LiveScores
  Scenario: Verify loading spinner disappears once scores have loaded
    # waitForUrl ensures page navigation is complete before we look for the spinner.
    * waitForUrl('live-scores')
    # notExists() is the inverse of exists() — it returns true when the element is absent.
    # The retry(3, 2000) wrapper re-evaluates the block up to 3 times with a 2 s gap,
    # allowing the spinner time to finish before we conclude it is gone.
    * retry(3, 2000) { notExists('.cb-loading-spinner') }
    Then assert notExists('.cb-loading-spinner')

  # ─── Tag: @ClickMatchCard ─────────────────────────────────────────────────────
  @ClickMatchCard @LiveScores
  Scenario: Click a live match card and verify navigation to the scorecard page
    * waitFor('.cb-lv-scr-card')
    # waitFor(selector).click() is the idiomatic Karate one-liner:
    # waitFor() blocks until the element is present, then returns it so
    # you can immediately chain .click() without a separate step.
    * waitFor('.cb-lv-scr-card a').click()
    # After clicking a match card, the URL should change to a scorecard page.
    * waitForUrl('cricket-scores')
    Then match driver.url contains 'cricket-scores'
    * screenshot()

  # ─── Tag: @HoverOnMatchCard ───────────────────────────────────────────────────
  @HoverOnMatchCard @LiveScores
  Scenario: Hover over a match card to trigger any tooltip or hover state
    * waitFor('.cb-lv-scr-card')
    # mouse().move(selector) simulates moving the cursor over the element.
    # This is essential for testing CSS :hover effects or JavaScript mouseover events.
    * mouse().move('.cb-lv-scr-card')
    * screenshot()
    Then assert exists('.cb-lv-scr-card')
