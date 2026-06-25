Feature: Cricbuzz Browser State — Cookies and Web Storage
  # UI Concepts Covered:
  #   1. cookie(name)          - read a specific browser cookie by name
  #   2. deleteCookie(name)    - remove a named cookie from the browser
  #   3. clearCookies()        - remove all cookies for the current domain
  #   4. script() localStorage - read, write and remove items in localStorage
  #   5. script() sessionStorage - read, write and remove items in sessionStorage

  Background:
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')

  # ─── Tag: @CookieRead ─────────────────────────────────────────────────────────
  @CookieRead @BrowserState
  Scenario: Read browser cookies set by Cricbuzz on first load
    # cookie(name) returns the cookie object { name, value, domain, path, … } or null.
    # Cricbuzz sets analytics and session cookies when the page first loads.
    # document.cookie via script() returns all cookies as a single delimited string.
    * def allCookies = script("document.cookie")
    * karate.log('Raw document.cookie string:', allCookies)
    # Verify at least an empty string is returned (page may or may not set cookies).
    Then match allCookies == '#string'
    * screenshot()

  # ─── Tag: @CookieDelete ───────────────────────────────────────────────────────
  @CookieDelete @BrowserState
  Scenario: Delete the Google Analytics _gid cookie and verify it is removed
    # deleteCookie(name) removes the named cookie from the browser's cookie store.
    # After deletion, that cookie will not be sent in subsequent requests.
    # We first log what cookies exist, then delete the common _gid tracking cookie.
    * def before = script("document.cookie")
    * karate.log('Cookies before delete:', before)
    * deleteCookie('_gid')
    * def after = script("document.cookie")
    * karate.log('Cookies after deleteCookie("_gid"):', after)
    * screenshot()
    Then assert after != before || !before.contains('_gid')

  # ─── Tag: @ClearCookies ───────────────────────────────────────────────────────
  @ClearCookies @BrowserState
  Scenario: Clear all domain cookies and verify document.cookie is empty
    # clearCookies() removes every cookie visible to the current domain.
    # After clearing, the next navigation may present a fresh session or cookie banner.
    * clearCookies()
    * def cookiesAfter = script("document.cookie")
    * karate.log('Cookies after clearCookies():', cookiesAfter)
    # After clearing, document.cookie should be an empty string.
    Then match cookiesAfter == ''
    * screenshot()

  # ─── Tag: @LocalStorage ───────────────────────────────────────────────────────
  @LocalStorage @BrowserState
  Scenario: Write, read and remove a key in browser localStorage via script()
    # localStorage persists data in the browser with no expiry (cleared by clearStorage).
    # Karate accesses it via script() since there is no built-in localStorage step.
    * script("localStorage.setItem('karate_key', 'karate_value')")
    * def stored = script("localStorage.getItem('karate_key')")
    * karate.log('localStorage value:', stored)
    Then match stored == 'karate_value'
    # Read all keys to show how to inspect localStorage contents.
    * def keyCount = script("localStorage.length")
    * karate.log('Total localStorage keys:', keyCount)
    # Clean up after the test so it does not affect other scenarios.
    * script("localStorage.removeItem('karate_key')")
    * def afterRemove = script("localStorage.getItem('karate_key')")
    Then match afterRemove == '#null'
    * screenshot()

  # ─── Tag: @SessionStorage ─────────────────────────────────────────────────────
  @SessionStorage @BrowserState
  Scenario: Write, read and remove a key in sessionStorage via script()
    # sessionStorage is scoped to the browser tab and cleared when the tab closes.
    # Useful for testing single-page apps that store transient UI state here.
    * script("sessionStorage.setItem('session_key', 'session_value')")
    * def sessionVal = script("sessionStorage.getItem('session_key')")
    * karate.log('sessionStorage value:', sessionVal)
    Then match sessionVal == 'session_value'
    * script("sessionStorage.removeItem('session_key')")
    * def afterRemove = script("sessionStorage.getItem('session_key')")
    Then match afterRemove == '#null'
    * screenshot()
