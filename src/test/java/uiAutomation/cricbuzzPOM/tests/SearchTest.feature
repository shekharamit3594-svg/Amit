Feature: Cricbuzz Search — POM Tests
  # ─── How this compares to the non-POM version (uiAutomation/cricbuzz/CricbuzzSearch.feature) ────
  #
  # NON-POM (old):
  #   Selectors like '.cb-search-btn', '#searchInput', '.cb-search-result' are
  #   repeated in every scenario. Changing the search icon CSS class requires
  #   edits scattered across every scenario that uses it.
  #
  # POM (this file):
  #   Selectors live only in pages/SearchPage.feature.
  #   This file describes WHAT is being tested ("search for a player"),
  #   not HOW to interact with the DOM ('.cb-search-btn', '#searchInput').
  #   Composed from two page objects: HomePage (to land on the site) + SearchPage (actions).
  # ─────────────────────────────────────────────────────────────────────────────────

  Background:
    * def homePage  = 'classpath:uiAutomation/cricbuzzPOM/pages/HomePage.feature'
    * def searchPage = 'classpath:uiAutomation/cricbuzzPOM/pages/SearchPage.feature'
    # Every test scenario starts from the homepage with the search bar open.
    * call read(homePage) @navigate
    * call read(searchPage) @openSearch

  # ─── Tag: @SearchByPlayer ────────────────────────────────────────────────────
  @SearchByPlayer @POM
  Scenario: Search for a player by name and verify results appear
    * call read(searchPage) @searchFor { query: 'Virat Kohli' }
    * screenshot()
    * call read(searchPage) @verifySearchResults { expectedText: 'Virat Kohli' }

  # ─── Tag: @SearchByTeam ──────────────────────────────────────────────────────
  @SearchByTeam @POM
  Scenario: Search for a team name and verify auto-suggest dropdown appears
    * call read(searchPage) @searchWithSuggestions { query: 'India' }
    * screenshot()
    # `suggestions` is the variable exposed by @searchWithSuggestions
    Then match suggestions contains 'India'

  # ─── Tag: @ClearAndRetype ────────────────────────────────────────────────────
  @ClearAndRetype @POM
  Scenario: Clear a search term and retype a different keyword
    * call read(searchPage) @searchWithSuggestions { query: 'Australia' }
    * call read(searchPage) @clearSearch
    # `clearedValue` is the variable exposed by @clearSearch (always '')
    And match clearedValue == ''
    * call read(searchPage) @searchWithSuggestions { query: 'IPL 2025' }
    * screenshot()
    Then match suggestions contains 'IPL'

  # ─── Tag: @EscapeCloses ──────────────────────────────────────────────────────
  @EscapeCloses @POM
  Scenario: Pressing Escape closes the search bar and returns the page to normal view
    * input('#searchInput', 'England')
    * call read(searchPage) @pressEscapeToClose
    * screenshot()
    Then assert exists('.cb-nav-main')
