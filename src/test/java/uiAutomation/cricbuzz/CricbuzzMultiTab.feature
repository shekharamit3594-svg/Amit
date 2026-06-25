Feature: Cricbuzz Multiple Browser Tabs
  # UI Concepts Covered:
  #   1. script("window.open(url, '_blank')") - open a new browser tab via JS
  #   2. switchPage(urlSubstring)             - switch driver focus to a tab by URL
  #   3. switchPage(titleSubstring)           - switch driver focus to a tab by title
  #
  # After switchPage(), ALL subsequent driver commands target the new tab.
  # Call switchPage() again with the original tab's URL or title to switch back.

  Background:
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')

  # ─── Tag: @NewTabViaScript ─────────────────────────────────────────────────────
  @NewTabViaScript @MultiTab
  Scenario: Open a new browser tab via JavaScript and switch to it
    # window.open(url, '_blank') instructs the browser to open a new tab.
    # Karate does not have a built-in "open new tab" step, so script() is used here.
    * script("window.open('https://www.cricbuzz.com/cricket-team-rankings', '_blank')")
    # switchPage(urlSubstring) finds the tab whose URL contains the given string
    # and moves the WebDriver session to that tab. All commands now target it.
    * switchPage('cricket-team-rankings')
    * waitForUrl('cricket-team-rankings')
    Then match driver.url contains 'cricket-team-rankings'
    * screenshot()

  # ─── Tag: @SwitchPageByTitle ───────────────────────────────────────────────────
  @SwitchPageByTitle @MultiTab
  Scenario: Open a second tab and switch back to the original tab by title
    * script("window.open('https://www.cricbuzz.com/cricket-team-rankings', '_blank')")
    # Switch forward to the new tab by URL substring.
    * switchPage('cricket-team-rankings')
    * waitForUrl('cricket-team-rankings')
    * karate.log('Now on new tab:', driver.url)
    # switchPage() also accepts a title substring — useful when the URL is dynamic.
    # This switches back to the original Cricbuzz homepage tab by its title.
    * switchPage('Cricbuzz - Live Cricket Scores')
    * waitFor('.cb-nav-main')
    Then match driver.url contains 'cricbuzz.com'
    And  assert exists(".cb-nav-main")
    * screenshot()

  # ─── Tag: @MultiTabNavigation ──────────────────────────────────────────────────
  @MultiTabNavigation @MultiTab
  Scenario: Open two additional tabs and navigate between all three
    # Open Rankings tab
    * script("window.open('https://www.cricbuzz.com/cricket-team-rankings', '_blank')")
    # Open Schedule tab
    * script("window.open('https://www.cricbuzz.com/cricket-schedule', '_blank')")
    # Switch to Rankings tab.
    * switchPage('cricket-team-rankings')
    * waitForUrl('cricket-team-rankings')
    * karate.log('Tab 2 - Rankings URL:', driver.url)
    * screenshot()
    # Switch to Schedule tab.
    * switchPage('cricket-schedule')
    * waitForUrl('cricket-schedule')
    * karate.log('Tab 3 - Schedule URL:', driver.url)
    * screenshot()
    # Switch back to original tab.
    * switchPage('Cricbuzz')
    * waitFor('.cb-nav-main')
    Then assert exists('.cb-nav-main')
    * screenshot()
