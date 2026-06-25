Feature: Cricbuzz Search Functionality
  # UI Concepts Covered:
  #   1. click()           - click a button or icon to trigger an action
  #   2. input()           - type text into a focused input field
  #   3. clear()           - erase existing text from an input
  #   4. Key.ENTER         - send a keyboard key press
  #   5. waitFor()         - wait for the search results container to appear
  #   6. text()            - read the visible inner text of an element
  #   7. value()           - read the current value of an <input> element
  #   8. Script execution  - run JavaScript for assertions not possible via DOM alone

  Background:
    # driverConfig from karate-config.js: local Chrome for dev, Selenium Grid for docker env.
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'

  # ─── Tag: @SearchByPlayer ────────────────────────────────────────────────────
  @SearchByPlayer @SearchFunctionality
  Scenario: Search for a player by name and verify results appear
    # The search icon in Cricbuzz's header triggers the search bar.
    # We use the CSS class selector for the search icon button.
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    # After clicking the icon, the search input box should appear.
    * def searchInput = waitFor('#searchInput')
    # input(selector, text) types the given string into the element character by character.
    * input('#searchInput', 'Virat Kohli')
    # value() reads the current value of an <input> / <textarea> element.
    # This confirms the text was actually typed into the field.
    * def typedValue = value('#searchInput')
    Then match typedValue contains 'Virat'
    # Key.ENTER sends the Enter key to submit the search.
    * input('#searchInput', Key.ENTER)
    # Wait for the search results container to appear after submission.
    * waitFor('.cb-search-result')
    * screenshot()
    # text() reads the innerText of the matched element.
    * def resultText = text('.cb-search-result')
    And match resultText contains 'Virat Kohli'

  # ─── Tag: @SearchByTeam ──────────────────────────────────────────────────────
  @SearchByTeam @SearchFunctionality
  Scenario: Search for a team name and verify search suggestions appear
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    * input('#searchInput', 'India')
    # After typing, Cricbuzz shows auto-suggest results in a dropdown.
    # We wait for the suggestion list to appear before asserting.
    * waitFor('.cb-search-suggestions')
    * screenshot()
    * def suggestions = text('.cb-search-suggestions')
    Then match suggestions contains 'India'

  # ─── Tag: @SearchClearAndRetype ──────────────────────────────────────────────
  @SearchClearAndRetype @SearchFunctionality
  Scenario: Clear a search term and search again with a different keyword
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    * input('#searchInput', 'Australia')
    * waitFor('.cb-search-suggestions')
    # clear() removes all text from an input element — equivalent to Ctrl+A then Delete.
    # This is essential when re-using an input field in the same scenario.
    * clear('#searchInput')
    # Verify the input is now empty after clearing.
    * def clearedValue = value('#searchInput')
    Then match clearedValue == ''
    # Type a new search term after clearing.
    * input('#searchInput', 'IPL 2025')
    * waitFor('.cb-search-suggestions')
    * screenshot()
    * def newSuggestions = text('.cb-search-suggestions')
    And match newSuggestions contains 'IPL'

  # ─── Tag: @SearchEscape ──────────────────────────────────────────────────────
  @SearchEscape @SearchFunctionality
  Scenario: Press Escape to close the search bar and return to normal view
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    * input('#searchInput', 'England')
    # Key.ESCAPE closes most dropdown/overlay UIs.
    # This tests that the search bar dismisses cleanly.
    * input('#searchInput', Key.ESCAPE)
    * screenshot()
    # After Escape, the main nav bar should still be visible.
    Then assert exists('.cb-nav-main')
