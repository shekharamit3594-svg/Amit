Feature: Cricbuzz Form Interactions and Dialog Handling
  # UI Concepts Covered:
  #   1. select(selector, value)       - choose a native <select> option by its value attr
  #   2. select(selector, {text: ''})  - choose a native <select> option by visible label
  #   3. submit(selector)              - submit a form programmatically
  #   4. input(selector, 'filePath')   - set a file on a <input type="file"> element
  #   5. mouse().down().move().up()    - drag and drop via the mouse fluent API
  #   6. dialog(true)                  - accept a native browser alert / confirm
  #   7. dialog(false)                 - dismiss a native browser alert / confirm
  #
  # NOTE: Cricbuzz is a modern SPA that uses custom CSS components instead of
  # native HTML <select> elements, <form> submissions, or file inputs.
  # Scenarios for select(), submit(), file upload and drag-and-drop demonstrate
  # the Karate API with inline code comments showing real-world usage patterns.
  # The alert / dialog scenarios trigger native dialogs via script() to show the
  # full dialog(true/false) flow on the live Cricbuzz site.

  Background:
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    * waitFor('.cb-nav-main')

  # ─── Tag: @SelectDropdown ──────────────────────────────────────────────────────
  @SelectDropdown @FormInteractions
  Scenario: Demonstrate select() for choosing from a native HTML dropdown
    # select(selector, value) clicks a <select> element and picks the <option>
    # whose 'value' attribute matches the given string.
    #
    # Example — an application with <select id="format">:
    #   * select('#format', 'T20I')
    #
    # select(selector, {text: 'label'}) picks the option by its visible display text
    # instead of the value attribute:
    #   * select('#format', {text: 'Twenty20 International'})
    #
    # Cricbuzz does not use native <select> elements; this scenario logs the API
    # pattern and asserts the page is intact.
    * karate.log('select() API — use select(selector, value) on native <select> elements')
    * karate.log('select() API — use select(selector, {text: label}) for visible label match')
    * screenshot()
    Then assert exists('.cb-nav-main')

  # ─── Tag: @SubmitForm ──────────────────────────────────────────────────────────
  @SubmitForm @FormInteractions
  Scenario: Use submit() to programmatically trigger a search form submission
    # submit(selector) calls the HTML form's submit() method on the matched element.
    # It is equivalent to the user pressing Enter inside an input that belongs to a form.
    # Karate finds the nearest ancestor <form> and submits it.
    #
    # On Cricbuzz, the search uses a custom implementation without a <form> element,
    # so we demonstrate submit() on the input and fall back to Key.ENTER behavior.
    * waitFor('.cb-search-btn')
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    * input('#searchInput', 'MS Dhoni')
    # submit() works when the input is inside a <form>; on sites without <form>
    # it may behave like pressing Enter on the field.
    * submit('#searchInput')
    * screenshot()
    Then assert exists('.cb-nav-main')

  # ─── Tag: @FileUpload ──────────────────────────────────────────────────────────
  @FileUpload @FormInteractions
  Scenario: Demonstrate file upload API for an input[type=file] element
    # input(selector, filePath) sets the value of a file input element.
    # Karate resolves the path relative to the classpath root or as an absolute path.
    # After setting the file, click the upload or submit button to trigger the upload.
    #
    # Full pattern for a page with a file upload:
    #   * input('input[type=file]', 'classpath:test-data/sample.pdf')
    #   * click('#uploadButton')
    #   * waitFor('.upload-success-message')
    #
    # Cricbuzz has no file upload feature; this scenario documents the API pattern.
    * karate.log('File upload API: input(selector, filePath) on input[type=file]')
    * karate.log('After setting the file, click the upload/submit button to proceed')
    * screenshot()
    Then assert exists('.cb-nav-main')

  # ─── Tag: @DragAndDrop ─────────────────────────────────────────────────────────
  @DragAndDrop @FormInteractions
  Scenario: Demonstrate drag-and-drop using the Karate mouse fluent API
    # mouse().down(source).move(target).up() performs a click-hold-drag-release sequence.
    # This is the standard pattern for sortable lists, sliders, kanban boards, etc.
    #
    # Example on a page with draggable elements:
    #   * mouse().down('.draggable-card').move('.drop-zone').up()
    #
    # For pixel-precise offsets instead of element selectors:
    #   * mouse().down('.handle').move(400, 200).up()
    #
    # Cricbuzz has no drag-and-drop UI. This scenario shows the mouse API syntax
    # by moving the mouse over an existing element (harmless on a live site).
    * waitFor('.cb-nav-main')
    * mouse().move('.cb-nav-main')
    * karate.log('Drag-drop API: mouse().down(source).move(target).up()')
    * screenshot()
    Then assert exists('.cb-nav-main')

  # ─── Tag: @AlertDialog ─────────────────────────────────────────────────────────
  @AlertDialog @FormInteractions
  Scenario: Trigger a native browser alert via JavaScript and accept it with dialog(true)
    # dialog(true) clicks OK on the next native browser dialog (alert / confirm / prompt).
    # Native dialogs are triggered by window.alert(), window.confirm(), window.prompt().
    # They are NOT CSS overlays or React modals — those are handled with click() instead.
    #
    # We use script() to trigger an alert so we can demonstrate the full flow
    # on the live Cricbuzz page without needing any special app feature.
    * script("window.alert('Karate dialog demo — this is a native alert')")
    # dialog(true) accepts the dialog (clicks OK / Accept).
    * dialog(true)
    * screenshot()
    Then assert exists('.cb-nav-main')

  # ─── Tag: @ConfirmDialog ───────────────────────────────────────────────────────
  @ConfirmDialog @FormInteractions
  Scenario: Trigger a native confirm dialog and dismiss it with dialog(false)
    # window.confirm() shows a dialog with OK and Cancel buttons.
    # dialog(false) clicks Cancel / Dismiss.
    # The return value of window.confirm() will be false when dismissed.
    * def confirmed = script("window.confirm('Proceed with this action?')")
    # dialog(false) clicks Cancel on the confirm dialog.
    * dialog(false)
    * screenshot()
    Then assert exists('.cb-nav-main')
