Feature: Auth Setup — Reusable Login Feature

  # This feature simulates a login / token-acquisition step.
  # It is intended to be called with `callonce` from a Background block so that
  # authentication runs exactly once per suite, not once per scenario.
  #
  # In a real OAuth / session-based project this scenario would:
  #   1. POST credentials to /auth/login
  #   2. Extract the token from the response
  #   3. Return it so every subsequent scenario can use it
  #
  # GoRest uses a static bearer token configured in karate-config.js, so we
  # return it directly from config here. The callonce pattern is identical
  # whether the token is static or dynamically acquired from a login endpoint.
  #
  # Caller syntax (7.5 / 7.6 — callonce for shared auth):
  #   Background:
  #     * def auth  = callonce read('AuthSetup.feature')
  #     * def token = auth.authToken

  Scenario: Obtain auth token — simulate login

    # ── Real-world pattern (commented out) ────────────────────────────────────
    # Given url authBaseUrl
    # And path '/auth/login'
    # And header Content-Type = 'application/json'
    # And request { username: '#(username)', password: '#(password)' }
    # When method post
    # Then status 200
    # * def authToken = response.token
    # ──────────────────────────────────────────────────────────────────────────

    # GoRest: bearer token is pre-configured in karate-config.js
    * def authToken = bearerToken
    * print 'Auth setup complete — token acquired:', authToken

    # authToken is returned as part of the callonce result object.
    # The caller accesses it as:  auth.authToken
