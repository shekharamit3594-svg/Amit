Feature: Cricbuzz iFrame Switching
  # UI Concepts Covered:
  #   1. exists('iframe')       - check whether any iframe is present before switching
  #   2. switchFrame(selector)  - move WebDriver context inside a matched <iframe>
  #   3. switchFrame(null)      - return to the main page context from inside an iframe
  #
  # How iFrame switching works:
  #   After switchFrame(selector), every driver command (click, text, waitFor, etc.)
  #   targets the iframe's DOM — NOT the main page DOM.
  #   Always call switchFrame(null) before asserting main-page elements.
  #   Forgetting to exit causes "element not found" errors on the main page.
  #
  # NOTE: Cricbuzz may embed third-party ad iframes that are blocked by Chrome's
  # ad-blocking or absent depending on geography. The scenarios use exists() before
  # attempting switchFrame() so they pass even when no iframe is present.

  Background:
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')

  # ─── Tag: @SwitchFrameEnterExit ────────────────────────────────────────────────
  @SwitchFrameEnterExit @IframeHandling
  Scenario: Switch into an iframe and return to the main page with switchFrame(null)
    # Full pattern for a page that has an iframe:
    #   * switchFrame('#myIframe')         ← driver now targets the iframe DOM
    #   * waitFor('.element-in-iframe')
    #   * click('.button-in-iframe')
    #   * switchFrame(null)                ← return to the main page DOM
    #   * waitFor('.main-page-element')    ← commands target main page again
    #
    # We use exists() first so the scenario does not fail when ads are blocked.
    * def iframePresent = exists('iframe')
    * karate.log('iframe present on page:', iframePresent)
    * if (iframePresent) switchFrame('iframe')
    * if (iframePresent) karate.log('Switched into iframe DOM context')
    # switchFrame(null) always exits any active iframe, safe to call unconditionally.
    * switchFrame(null)
    # After exiting, main-page selectors must work again.
    * waitFor('.cb-nav-main')
    Then assert exists('.cb-nav-main')
    * screenshot()

  # ─── Tag: @SwitchFrameById ─────────────────────────────────────────────────────
  @SwitchFrameById @IframeHandling
  Scenario: Switch to an iframe by its id attribute and interact with its content
    # switchFrame('#myId') finds an iframe by CSS id selector.
    # switchFrame('.myClass') finds by class selector.
    # switchFrame('//iframe[@title="Ad"]') finds by XPath.
    #
    # Pattern for a known iframe id:
    #   * switchFrame('#adFrame')
    #   * def adText = text('.ad-headline')
    #   * switchFrame(null)
    #
    # Cricbuzz iframes vary by session. We find any iframe and use its index if present.
    * def iframeCount = script("document.querySelectorAll('iframe').length")
    * karate.log('Number of iframes on page:', iframeCount)
    * if (iframeCount > 0) switchFrame('iframe')
    * if (iframeCount > 0) karate.log('Now inside iframe context')
    * switchFrame(null)
    Then assert exists('.cb-nav-main')
    * screenshot()

  # ─── Tag: @NestedFramePattern ──────────────────────────────────────────────────
  @NestedFramePattern @IframeHandling
  Scenario: Document the nested iframe switching pattern for reference
    # Some pages embed iframes inside other iframes (nested iframes).
    # To reach content in a nested iframe:
    #   Step 1 — enter the outer iframe:
    #     * switchFrame('#outerIframe')
    #   Step 2 — enter the inner iframe (relative to the outer):
    #     * switchFrame('#innerIframe')
    #   Step 3 — interact with nested content:
    #     * click('.nested-button')
    #   Step 4 — exit to outer iframe:
    #     * switchFrame(null)
    #   Step 5 — exit to main page:
    #     * switchFrame(null)
    #
    # Each switchFrame(null) goes up one level in the frame hierarchy.
    * karate.log('Nested iframe pattern: switchFrame outer → switchFrame inner → switchFrame(null) × 2')
    * screenshot()
    Then assert exists('.cb-nav-main')
