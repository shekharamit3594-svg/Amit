@ignore
Feature: Search Page Object
  # ─── Page Object for the Cricbuzz Search Bar ──────────────────────────────────
  # All scenarios assume the browser is already open on Cricbuzz (call HomePage@navigate first).
  #
  # HOW TO USE IN A TEST FEATURE:
  #   * def searchPage = 'classpath:uiAutomation/cricbuzzPOM/pages/SearchPage.feature'
  #
  #   Open the search panel:
  #     * call read(searchPage) @openSearch
  #
  #   Search and verify:
  #     * call read(searchPage) @searchFor { query: 'Virat Kohli' }
  #     * call read(searchPage) @verifySearchResults { expectedText: 'Virat Kohli' }
  #
  # Variable contract:
  #   @searchWithSuggestions  → exposes  `suggestions` (String) to the calling scope
  #   @clearSearch            → exposes  `clearedValue` (String, always '') to the calling scope

  # ── @openSearch ───────────────────────────────────────────────────────────────
  @openSearch
  Scenario: Click the search icon to reveal the search input field
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    * waitFor('#searchInput')

  # ── @searchFor ────────────────────────────────────────────────────────────────
  # Args: { query: '<search text>' }
  @searchFor
  Scenario: Type the provided query into the search input and submit with Enter
    # __arg.query is the 'query' key from the map passed by the caller.
    # Example call: call read(searchPage) @searchFor { query: 'Virat Kohli' }
    * input('#searchInput', __arg.query)
    * input('#searchInput', Key.ENTER)
    * waitFor('.cb-search-result')

  # ── @verifySearchResults ──────────────────────────────────────────────────────
  # Args: { expectedText: '<text to assert>' }
  @verifySearchResults
  Scenario: Assert the search results container contains the expected text
    * def resultText = text('.cb-search-result')
    Then match resultText contains __arg.expectedText

  # ── @searchWithSuggestions ────────────────────────────────────────────────────
  # Args:    { query: '<search text>' }
  # Returns: suggestions (String) — the visible text of the auto-suggest dropdown
  @searchWithSuggestions
  Scenario: Type the provided query and wait for the auto-suggest dropdown to appear
    * input('#searchInput', __arg.query)
    * waitFor('.cb-search-suggestions')
    * def suggestions = text('.cb-search-suggestions')

  # ── @clearSearch ──────────────────────────────────────────────────────────────
  # Returns: clearedValue (String, always '') to confirm the field is empty
  @clearSearch
  Scenario: Clear all text from the search input field
    * clear('#searchInput')
    * def clearedValue = value('#searchInput')
    Then match clearedValue == ''

  # ── @pressEscapeToClose ───────────────────────────────────────────────────────
  @pressEscapeToClose
  Scenario: Send the Escape key to dismiss the search bar
    * input('#searchInput', Key.ESCAPE)
