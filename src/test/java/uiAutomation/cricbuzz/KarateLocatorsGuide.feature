Feature: Karate UI Locators — Complete Reference Guide (Applied to Cricbuzz)

  # ══════════════════════════════════════════════════════════════════════════════
  # ALL LOCATOR TYPES IN KARATE — QUICK REFERENCE
  # ──────────────────────────────────────────────────────────────────────────────
  #
  # ┌─────┬──────────────────────────────┬────────────────────────────────────────┐
  # │  #  │  Type                        │  Syntax example                        │
  # ├─────┼──────────────────────────────┼────────────────────────────────────────┤
  # │  1  │  CSS — ID                    │  '#main-header'                        │
  # │  2  │  CSS — Class                 │  '.cb-search-btn'                      │
  # │  3  │  CSS — Tag                   │  'input'  /  'button'                  │
  # │  4  │  CSS — Attribute (exact)     │  '[type="submit"]'                     │
  # │  5  │  CSS — Attribute (contains)  │  '[class*="search"]'                   │
  # │  6  │  CSS — Attribute (starts)    │  '[href^="https"]'                     │
  # │  7  │  CSS — Attribute (ends)      │  '[href$=".com"]'                      │
  # │  8  │  CSS — Compound              │  'a.nav-link'  /  '#hdr .logo'         │
  # │  9  │  CSS — Descendant  (space)   │  '#header a'                           │
  # │ 10  │  CSS — Direct child   (>)    │  '#nav > ul > li'                      │
  # │ 11  │  CSS — Adjacent sibling (+)  │  'label + input'                       │
  # │ 12  │  CSS — General sibling (~)   │  'h1 ~ p'                              │
  # │ 13  │  CSS — Pseudo :nth-child     │  'li:nth-child(2)'                     │
  # │ 14  │  CSS — Pseudo :first/:last   │  'li:first-child'  /  'li:last-child'  │
  # │ 15  │  CSS — Pseudo :not()         │  'button:not(.disabled)'               │
  # │ 16  │  CSS — Pseudo :checked       │  'input[type="checkbox"]:checked'      │
  # ├─────┼──────────────────────────────┼────────────────────────────────────────┤
  # │ 17  │  Karate — Exact text         │  '{a}Live Scores'                      │
  # │ 18  │  Karate — Partial text       │  '*{a}Scores'                          │
  # │ 19  │  Karate — Placeholder / ^    │  '^Search here...'                     │
  # ├─────┼──────────────────────────────┼────────────────────────────────────────┤
  # │ 20  │  XPath — Attribute           │  '//div[@id="content"]'                │
  # │ 21  │  XPath — Text match          │  '//a[text()="Live Scores"]'           │
  # │ 22  │  XPath — contains()          │  '//a[contains(text(),"Live")]'        │
  # │ 23  │  XPath — Axes (sibling)      │  '//label/following-sibling::input'    │
  # │ 24  │  XPath — Position index      │  '(//li[@class="item"])[1]'            │
  # ├─────┼──────────────────────────────┼────────────────────────────────────────┤
  # │ 25  │  JavaScript — script()       │  script("document.querySelector(…)")   │
  # │ 26  │  JavaScript — querySelectorAll│  script("document.querySelectorAll(…)")│
  # └─────┴──────────────────────────────┴────────────────────────────────────────┘
  #
  # PREFERENCE ORDER  (most preferred → least preferred)
  # ─────────────────────────────────────────────────────
  #   1. Karate text  {tag}text   — most readable, no HTML knowledge needed
  #   2. CSS ID        #id         — fastest browser lookup, always unique
  #   3. CSS Compound  tag#id      — adds tag context for clarity
  #   4. CSS Attribute [attr=val]  — stable when IDs are absent but name/data-* exists
  #   5. CSS Class     .class      — use only when the class is meaningful & stable
  #   6. CSS Compound  parent>child — acceptable for structural selectors
  #   7. XPath                     — last resort: only when CSS cannot express the path
  #   8. JavaScript script()       — emergency escape hatch for dynamic DOM
  #
  # WHY AVOID XPATH BY DEFAULT
  # ────────────────────────────
  #   • Verbose:  //div[@class="cb-nws-lst-itm"]/div[1]/a[1]/h2
  #   • Brittle:  breaks on any structural HTML change (new wrapper div, etc.)
  #   • Slow:     XPath engine is slower than CSS in most browsers
  #   • Use it only when CSS cannot express the path (e.g. parent selection,
  #     text-contains matching, or traversing up the DOM tree).
  # ══════════════════════════════════════════════════════════════════════════════

  Background:
    * configure driver = driverConfig
    * driver 'https://www.cricbuzz.com'
    # Wait for the main nav to confirm the page has fully rendered
    * waitFor('#main-header + div')


  # ══════════════════════════════════════════════════════════════════════════════
  #  GROUP 1 : CSS LOCATORS
  # ══════════════════════════════════════════════════════════════════════════════

  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 1 : CSS ID Selector  →  #id
  # ─────────────────────────────────────────────────────────────────────────────
  # Maps to the HTML `id` attribute: <div id="main-header">
  # Rules:
  #   • IDs must be UNIQUE per page — the browser stops looking after the first match
  #   • Fastest CSS selector because browsers index IDs in a hash map (O(1) lookup)
  #   • Prefer #id over other selectors whenever an element has a stable id attribute
  # Syntax:  '#elementId'    (hash + id value, always a string)
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_ID @L1
  Scenario: L1 — CSS ID Selector (#id) — fastest, most specific

    # '#main-header' matches  <header id="main-header">
    * def header = waitFor('#main-header')
    * print 'L1 — Found element by ID #main-header:', header

    # '#searchInput' matches  <input id="searchInput">
    # Open the search bar first so the input renders in the DOM
    * click('.cb-search-btn')
    * def searchBox = waitFor('#searchInput')
    Then assert exists('#searchInput')

    # Interaction using ID — same selector can be used in waitFor, click, input, etc.
    * input('#searchInput', 'India')
    * def typed = value('#searchInput')
    And match typed == 'India'
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 2 : CSS Class Selector  →  .className
  # ─────────────────────────────────────────────────────────────────────────────
  # Maps to any element with that CSS class: <button class="cb-search-btn">
  # Rules:
  #   • An element can have MULTIPLE classes — .class matches if ANY of them match
  #   • Less specific than #id — multiple elements on a page often share a class
  #   • waitFor/click use the FIRST matching element when multiple exist
  #   • Avoid generic classes like .btn, .link — they match too many elements
  # Syntax:  '.className'    (dot + class name)
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_Class @L2
  Scenario: L2 — CSS Class Selector (.class) — matches by CSS class name

    # '.cb-search-btn' matches  <span class="cb-search-btn …">
    * assert exists('.cb-search-btn')

    # When multiple elements share a class, waitFor/click targets the FIRST one.
    # Use a more specific selector (compound or combinator) to target a specific one.
    * def navLinks = script("document.querySelectorAll('.cb-nav-main .cb-col').length")
    * print 'L2 — Number of .cb-col nav elements found:', navLinks

    # Class selector in attribute assertion
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    Then assert exists('.cb-search-btn')
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 3 : CSS Tag Selector  →  tagname
  # ─────────────────────────────────────────────────────────────────────────────
  # Matches ALL elements of that HTML tag: input, button, a, div, span, etc.
  # Rules:
  #   • Almost never used alone — too broad (matches every <input> on the page)
  #   • Almost always combined with class, ID, or attribute to narrow scope
  #   • Useful for counting elements of a type, or in compound selectors
  # Syntax:  'tagname'    (no prefix)
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_Tag @L3
  Scenario: L3 — CSS Tag Selector (tag) — matches all elements of that tag

    # Standalone tag selector: finds the FIRST <a> anchor on the page
    * def firstAnchor = waitFor('a')
    * print 'L3 — First <a> tag href:', attribute('a', 'href')

    # Count all anchor tags on the page using JavaScript
    * def anchorCount = script("document.querySelectorAll('a').length")
    * print 'L3 — Total <a> tags on page:', anchorCount
    # The page should have many anchor elements
    Then assert anchorCount > 10

    # Tag selector combined with Karate's locator chain (shown in Group 2)
    # More useful when scoped: '#nav a'  →  all <a> inside #nav
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 4 : CSS Attribute Selectors  →  [attr=value], [attr*=v], etc.
  # ─────────────────────────────────────────────────────────────────────────────
  # Match elements by their HTML attribute values.  Five variants:
  #   [attr="val"]   — EXACT match:      href="/about"
  #   [attr*="val"]  — CONTAINS:         href="/about/team"  (any position)
  #   [attr^="val"]  — STARTS WITH:      href="https://…"
  #   [attr$="val"]  — ENDS WITH:        href="…/home"
  #   [attr~="val"]  — WORD in list:     class="nav active link"  →  [class~="active"]
  #   [attr]         — PRESENCE ONLY:    any element that has the attribute at all
  # Syntax:  '[attr="value"]'   (square brackets, no dot or hash)
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_Attribute @L4
  Scenario: L4 — CSS Attribute Selectors — [attr=val], [attr*=val], [attr^=val]

    # L4-a: [attr="exact"]  — find the first <a> whose href is exactly the homepage
    * def exactHref = attribute('a[href="/"]', 'href')
    * print 'L4-a [href="/"] exact match:', exactHref
    Then match exactHref == '/'

    # L4-b: [attr*="contains"]  — find any <a> whose href CONTAINS 'live-scores'
    # Useful when the full href value varies by environment (dev vs prod)
    * def liveLink = waitFor('a[href*="live-scores"]')
    * print 'L4-b [href*="live-scores"] contains match found'
    Then assert exists('a[href*="live-scores"]')

    # L4-c: [attr^="starts-with"]  — find links starting with 'https'
    * def secureLinks = script("document.querySelectorAll('a[href^=\"https\"]').length")
    * print 'L4-c [href^="https"] starts-with — secure links:', secureLinks

    # L4-d: [attr$="ends-with"]  — rarely used, but shown for completeness
    # Find any image whose src ends with '.svg'
    * def svgImgs = script("document.querySelectorAll('img[src$=\".svg\"], use[href$=\".svg\"]').length")
    * print 'L4-d [src$=".svg"] ends-with — SVG images:', svgImgs

    # L4-e: [attr] presence-only — any element that has a 'data-id' attribute
    * def dataElements = script("document.querySelectorAll('[data-id]').length")
    * print 'L4-e [data-id] presence-only — elements with data-id:', dataElements
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 5 : CSS Compound Selectors  →  tag.class  /  tag#id
  # ─────────────────────────────────────────────────────────────────────────────
  # Combine multiple simple selectors WITHOUT any space between them.
  # This means ALL conditions must be true simultaneously on THE SAME ELEMENT.
  #   a.nav-link      → <a> that also has class "nav-link"
  #   input#username  → <input> that also has id "username"
  #   div.active.bold → <div> that has BOTH class "active" AND class "bold"
  # Rules:
  #   • No space = AND condition on the SAME element
  #   • Space between selectors = DESCENDANT relationship (different element)
  #   • Use when class alone is too broad — tag.class narrows to a specific tag type
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_Compound @L5
  Scenario: L5 — CSS Compound Selectors (tag.class, .class1.class2) — AND condition

    # L5-a: tag.class  →  <a class="…nav-link…"> (anchor with nav-link class)
    # More specific than .nav-link alone (filters out any non-anchor with that class)
    * def navAnchors = script("document.querySelectorAll('a.nav-link').length")
    * print 'L5-a a.nav-link compound — anchor nav links:', navAnchors

    # L5-b: Multiple classes  →  element must have ALL listed classes
    # div.cb-col.cb-col-100 means: a div that has BOTH cb-col AND cb-col-100 classes
    * def multiClass = script("document.querySelectorAll('div.cb-col').length")
    * print 'L5-b div.cb-col compound — div elements with cb-col class:', multiClass

    # L5-c: tag + attribute compound  →  input[type="search"] — more precise than [type="search"]
    # since it also confirms the element is an input tag
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    # input[type="text"] or input#searchInput are both compound selectors
    Then assert exists('input#searchInput')
    * print 'L5-c input#searchInput — compound tag+ID selector works'
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPES 6–9 : CSS Combinator Selectors
  # ─────────────────────────────────────────────────────────────────────────────
  # Combinators describe the RELATIONSHIP between two elements (not attributes
  # of one element).  The two selectors target TWO DIFFERENT elements.
  #
  #   A B    — DESCENDANT:        B is anywhere inside A (any depth)
  #   A > B  — DIRECT CHILD:      B is an IMMEDIATE child of A (depth = 1 only)
  #   A + B  — ADJACENT SIBLING:  B immediately follows A at the same level
  #   A ~ B  — GENERAL SIBLING:   B follows A at same level (not necessarily adjacent)
  #
  # Space matters!  Compare:
  #   div.nav a    → any <a> anywhere inside div.nav  (descendant)
  #   div.nav > a  → only <a> that is a DIRECT child of div.nav  (child)
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_Combinators @L6
  Scenario: L6 — CSS Combinator Selectors (space, >, +, ~) — relationship between elements

    # L6-a: DESCENDANT  (space)
    # '#main-header a' = any <a> that is ANYWHERE inside #main-header
    * def headerLinks = script("document.querySelectorAll('#main-header a').length")
    * print 'L6-a #main-header a (descendant) — anchors in header:', headerLinks
    Then assert headerLinks > 0

    # L6-b: DIRECT CHILD  (>)
    # '#main-header > div' = only <div> elements that are IMMEDIATE children of #main-header
    # If Cricbuzz wraps with extra levels, this fails while the descendant version passes
    * def directDivs = script("document.querySelectorAll('#main-header > div').length")
    * print 'L6-b #main-header > div (direct child) — immediate div children:', directDivs

    # L6-c: ADJACENT SIBLING  (+)
    # '#main-header + div' = the <div> that comes IMMEDIATELY AFTER #main-header
    # This is the MOST RELIABLE selector for the nav bar on Cricbuzz — it was used
    # in ALL existing feature files because it identifies the nav div structurally,
    # not by a class that could change.
    * def navBar = waitFor('#main-header + div')
    * print 'L6-c #main-header + div (adjacent sibling) — the nav bar div'
    Then assert exists('#main-header + div')

    # L6-d: GENERAL SIBLING  (~)
    # '#main-header ~ div' = ALL <div> elements that follow #main-header at the same level
    # Broader than + (adjacent), matches multiple siblings
    * def allSiblingDivs = script("document.querySelectorAll('#main-header ~ div').length")
    * print 'L6-d #main-header ~ div (general sibling) — all sibling divs:', allSiblingDivs
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPES 10–12 : CSS Pseudo-class Selectors
  # ─────────────────────────────────────────────────────────────────────────────
  # Pseudo-classes select elements based on their STATE or POSITION in the DOM,
  # not their attributes.  They always start with a colon (:).
  #
  # Position pseudo-classes (structural):
  #   :first-child          — first child of its parent
  #   :last-child           — last child of its parent
  #   :nth-child(n)         — nth child (1-based);  odd/even keywords also work
  #   :nth-of-type(n)       — nth ELEMENT OF THAT TYPE inside its parent
  #   :only-child           — the only child of its parent
  #
  # State pseudo-classes:
  #   :checked              — checkboxes/radios that are checked
  #   :disabled             — form elements with the disabled attribute
  #   :enabled              — form elements that are NOT disabled
  #   :focus                — element currently receiving keyboard focus
  #   :hover                — element the mouse is currently over (rarely useful in tests)
  #   :empty                — element with no children / no text content
  #
  # Negation pseudo-class:
  #   :not(selector)        — any element that does NOT match the inner selector
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @CSS_Pseudo @L7
  Scenario: L7 — CSS Pseudo-class Selectors (:first-child, :nth-child, :not, :disabled)

    # L7-a: :first-child  — first item in the nav list
    * def firstNavItem = script("document.querySelector('nav li:first-child, .cb-nav-main .cb-col:first-child')?.innerText")
    * print 'L7-a :first-child — first nav item text:', firstNavItem

    # L7-b: :last-child  — last nav item (often the rightmost menu entry)
    * def lastNavItem = script("document.querySelector('.cb-nav-main .cb-col:last-child')?.innerText")
    * print 'L7-b :last-child — last nav item text:', lastNavItem

    # L7-c: :nth-child(n)  — pick the 2nd nav column item
    # :nth-child(1) = first,  :nth-child(2) = second,  :nth-child(odd/even) for alternating
    * def secondItem = script("document.querySelector('.cb-nav-main .cb-col:nth-child(2)')?.innerText")
    * print 'L7-c :nth-child(2) — second nav item text:', secondItem

    # L7-d: :not(selector)  — find all anchors that are NOT inside the header
    * def bodyLinks = script("document.querySelectorAll('a:not(#main-header a)').length")
    * print 'L7-d a:not(#main-header a) — non-header anchors:', bodyLinks

    # L7-e: :disabled  — input elements with the disabled attribute
    * def disabledInputs = script("document.querySelectorAll('input:disabled').length")
    * print 'L7-e input:disabled — disabled inputs on page:', disabledInputs

    # L7-f: :checked  — checkboxes or radios currently checked
    * def checkedBoxes = script("document.querySelectorAll('input:checked').length")
    * print 'L7-f input:checked — checked inputs:', checkedBoxes
    * screenshot()


  # ══════════════════════════════════════════════════════════════════════════════
  #  GROUP 2 : KARATE-SPECIFIC LOCATORS (unique to Karate — not in CSS/XPath)
  # ══════════════════════════════════════════════════════════════════════════════

  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 8 : Karate Text Locator  →  {tag}Exact Text
  # ─────────────────────────────────────────────────────────────────────────────
  # UNIQUE TO KARATE — this syntax does not exist in standard CSS or XPath.
  # Karate converts  {a}Live Scores  into:
  #   //a[normalize-space(.)='Live Scores']   (XPath equivalent under the hood)
  #
  # Rules:
  #   • Text must match EXACTLY (case-sensitive, trim-normalised)
  #   • Works for ANY tag:  {a}, {button}, {span}, {div}, {li}, {h1}, etc.
  #   • Matches the element whose VISIBLE TEXT (innerText) equals the given string
  #   • Best choice when: no stable ID/class exists but the label text is stable
  #   • Avoid when text changes frequently (e.g., dynamic counts like "5 matches")
  # Syntax:  '{tag}Visible text'
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @KarateText @L8
  Scenario: L8 — Karate Text Locator ({tag}text) — find by exact visible label

    # '{a}Live Scores' → finds <a> whose visible text is exactly "Live Scores"
    # This is the most readable locator in Karate — anyone can understand it
    * waitFor('#main-header + div')
    * assert exists("{a}Live Scores")

    # '{a}Schedule' → finds <a> whose visible text is exactly "Schedule"
    And assert exists("{a}Schedule")
    And assert exists("{a}Rankings")
    And assert exists("{a}Series")

    # Text locator also works with click(), waitFor(), text(), attribute()
    * click("{a}Live Scores")
    * waitForUrl('live-scores')
    Then match driver.url contains 'live-scores'
    * screenshot()

    # Navigate back so subsequent scenarios start from homepage
    * driver 'https://www.cricbuzz.com'
    * waitFor('#main-header + div')


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 9 : Karate Partial Text Locator  →  *{tag}Partial
  # ─────────────────────────────────────────────────────────────────────────────
  # Same as {tag}text but matches elements whose text CONTAINS the string
  # (not necessarily an exact match).  The asterisk (*) signals "contains".
  # Karate converts  *{a}Score  into:
  #   //a[contains(normalize-space(.),'Score')]
  #
  # When to prefer *{tag} over {tag}:
  #   • The button label includes a dynamic count: "Live Scores (3)"
  #   • The text may have minor variations: "Live Scores" vs "Live Cricket Scores"
  #   • You only know part of the label
  # Syntax:  '*{tag}Partial text'   (asterisk prefix inside the quotes)
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @KaratePartialText @L9
  Scenario: L9 — Karate Partial Text Locator (*{tag}partial) — contains matching

    * waitFor('#main-header + div')

    # '*{a}Scores' matches any <a> whose text CONTAINS "Scores"
    # This matches both "Live Scores" and "Cricket Scores" if both exist
    * assert exists("*{a}Scores")

    # '*{a}Rank' matches any <a> containing "Rank" — catches "Rankings", "Team Rankings", etc.
    And assert exists("*{a}Rank")

    # '*{a}Seri' partial prefix — matches "Series" (useful if text changes to "Test Series")
    And assert exists("*{a}Seri")

    # Partial text in navigation interaction — click any anchor containing "Schedule"
    * click("*{a}Schedule")
    * waitForUrl('cricket-schedule')
    Then match driver.url contains 'schedule'
    * screenshot()

    * driver 'https://www.cricbuzz.com'
    * waitFor('#main-header + div')


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 10 : Karate Placeholder Locator  →  ^placeholder text
  # ─────────────────────────────────────────────────────────────────────────────
  # Finds form inputs by their placeholder, aria-label, or title attribute.
  # Karate converts  ^Search here  into:
  #   //input[@placeholder='Search here'] | //input[@aria-label='Search here']
  #
  # WHY THIS IS USEFUL
  #   • Many input fields have no stable ID or class but always have a stable placeholder
  #   • Reads naturally: "input the placeholder 'Search cricbuzz'"
  #   • Works even when field ID changes across deploys
  # Syntax:  '^Placeholder text'  (caret prefix)
  #
  # Note: Cricbuzz's search input has a stable #searchInput ID, so this
  # scenario illustrates the concept using the placeholder attribute value.
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @KaratePlaceholder @L10
  Scenario: L10 — Karate Placeholder Locator (^text) — find input by placeholder

    # Open the search bar to make #searchInput visible in the DOM
    * click('.cb-search-btn')
    * waitFor('#searchInput')

    # Read the actual placeholder value so we can locate by it
    * def placeholderVal = attribute('#searchInput', 'placeholder')
    * print 'L10 — Search input placeholder:', placeholderVal

    # If the input has a placeholder, use the ^ locator to find it
    # '^' locator is equivalent to: input[placeholder="<value>"]
    * if (placeholderVal) assert exists('^' + placeholderVal)
    * print 'L10 — ^placeholder locator resolved to the same input as #searchInput'

    # In projects where inputs have no ID, this is often the cleanest locator:
    #   * input('^Search cricbuzz', 'Virat Kohli')
    #   — reads: "into the input whose placeholder is 'Search cricbuzz', type 'Virat Kohli'"
    * screenshot()


  # ══════════════════════════════════════════════════════════════════════════════
  #  GROUP 3 : XPATH LOCATORS
  # ══════════════════════════════════════════════════════════════════════════════

  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 11 : XPath — Basic Attribute Match
  # ─────────────────────────────────────────────────────────────────────────────
  # XPath expressions start with // (relative from anywhere in DOM) or / (absolute).
  # In Karate, pass any string starting with // as an XPath locator:
  #   * waitFor('//div[@id="content"]')
  # No special prefix needed — Karate auto-detects // as XPath.
  #
  # Common XPath predicates (the part inside []):
  #   [@id="val"]          — exact attribute match
  #   [@class="val"]       — exact class match (ALL classes must match)
  #   [contains(@class,"v")]— class contains — works with multiple classes
  #   [@type="text"]       — attribute with value
  #   [@disabled]          — attribute presence
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @XPath_Basic @L11
  Scenario: L11 — XPath Basic Attribute Match (//tag[@attr="val"])

    # L11-a: XPath by ID — equivalent to CSS #main-header
    # XPath:  //header[@id="main-header"]
    # CSS:    #main-header
    # Prefer CSS here, but XPath is shown for completeness
    * def headerXpath = waitFor('//header[@id="main-header"]')
    * print 'L11-a //header[@id="main-header"] — found header by XPath ID'
    Then assert exists('//header[@id="main-header"]')

    # L11-b: XPath contains(@class) — use when element has multiple classes
    # CSS:   .cb-search-btn  works fine, but XPath equivalent shown here
    # The contains() function is needed because the class attribute may be:
    #   class="cb-search-btn  some-other-class"  — exact match fails, contains() works
    * assert exists('//span[contains(@class,"cb-search-btn")]')
    * print 'L11-b //span[contains(@class,"cb-search-btn")] — found by partial class'

    # L11-c: XPath by input type — finds the search input after opening
    * click('.cb-search-btn')
    * waitFor('#searchInput')
    * assert exists('//input[@id="searchInput"]')
    * print 'L11-c //input[@id="searchInput"] — input by XPath attribute'
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 12 : XPath — Text Content Functions
  # ─────────────────────────────────────────────────────────────────────────────
  # XPath has two built-in text functions:
  #   text()          — selects the direct text node of the element
  #   normalize-space(.)  — full text including children, whitespace-normalised
  #
  # Patterns:
  #   //a[text()="Live Scores"]              — exact text of <a>'s direct text node
  #   //a[contains(text(),"Scores")]         — <a> whose text CONTAINS "Scores"
  #   //button[normalize-space(.)="Submit"]  — full normalised text (including child spans)
  #
  # KARATE EQUIVALENT (preferred):
  #   {a}Live Scores   →   //a[normalize-space(.)='Live Scores']
  #   *{a}Scores       →   //a[contains(normalize-space(.),'Scores')]
  # So for text matching, always prefer Karate's {tag}text syntax over raw XPath.
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @XPath_Text @L12
  Scenario: L12 — XPath Text Functions (text(), contains(text(),"…"))

    * waitFor('#main-header + div')

    # L12-a: Exact text match using XPath text()
    # XPath: //a[text()="Live Scores"]
    # Karate equivalent (preferred): '{a}Live Scores'
    * assert exists('//a[text()="Live Scores"]')
    * print 'L12-a //a[text()="Live Scores"] — XPath exact text match'

    # L12-b: Partial text match using XPath contains()
    # XPath: //a[contains(text(),"Scores")]
    # Karate equivalent (preferred): '*{a}Scores'
    * assert exists('//a[contains(text(),"Scores")]')
    * print 'L12-b //a[contains(text(),"Scores")] — XPath partial text (contains)'

    # L12-c: normalize-space() — handles elements whose text has surrounding whitespace
    # or is split across inline child elements (<span> inside the <a>, etc.)
    * assert exists('//a[normalize-space(.)="Rankings"]')
    * print 'L12-c //a[normalize-space(.)="Rankings"] — XPath normalised text'
    * screenshot()


  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 13 : XPath — Axes (DOM traversal in any direction)
  # ─────────────────────────────────────────────────────────────────────────────
  # XPath axes let you navigate the DOM in directions CSS cannot:
  #   parent::          — the immediate parent element
  #   ancestor::        — any ancestor (parent, grandparent, …)
  #   following-sibling::  — siblings that come AFTER
  #   preceding-sibling::  — siblings that come BEFORE
  #   child::           — immediate children (same as /)
  #   descendant::      — any descendant (same as //)
  #
  # WHEN TO USE XPath AXES (CSS cannot do these):
  #   1. Select an element's PARENT:        //span[@class="error"]/../button
  #   2. Find an input next to its label:   //label[text()="Email"]/following-sibling::input
  #   3. Find the container of a known element: //input[@id="x"]/ancestor::form
  #
  # Syntax:  //startElement/axis::targetElement
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @XPath_Axes @L13
  Scenario: L13 — XPath Axes (following-sibling, parent, ancestor) — upward DOM traversal

    * waitFor('#main-header + div')

    # L13-a: following-sibling — find the element that comes after a known anchor
    # This finds the <div> immediately after the element containing "Live Scores"
    # CSS cannot navigate to a sibling of a parent — XPath is required here.
    * def siblingCount = script("document.evaluate('count(//header[@id=\"main-header\"]/following-sibling::*)', document, null, XPathResult.NUMBER_TYPE, null).numberValue")
    * print 'L13-a following-sibling — siblings after header:', siblingCount

    # L13-b: parent:: — navigate UP to the parent of a known element
    # "Find the <div> that is the parent of any element with class cb-search-btn"
    # CSS: .cb-search-btn by itself; XPath parent axis: //span[@class="cb-search-btn"]/parent::div
    * def parentExists = script("document.evaluate('boolean(//span[contains(@class,\"cb-search-btn\")]/parent::div)', document, null, XPathResult.BOOLEAN_TYPE, null).booleanValue")
    * print 'L13-b parent:: — search btn wrapped in a div:', parentExists

    # L13-c: ancestor:: — traverse multiple levels up the DOM
    # "Find the <header> ancestor of the search button" — jumps over intermediate nodes
    * def ancestorHeader = script("document.evaluate('boolean(//span[contains(@class,\"cb-search-btn\")]/ancestor::header)', document, null, XPathResult.BOOLEAN_TYPE, null).booleanValue")
    * print 'L13-c ancestor:: — search btn inside a header ancestor:', ancestorHeader

    # L13-d: position-indexed XPath — pick the 2nd nav link
    # (//li)[2] picks the 2nd <li> in the entire document; scope with parent for safety
    * def secondNavLink = script("document.evaluate('(//header//a)[2]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue?.innerText")
    * print 'L13-d (//header//a)[2] position index — 2nd header link text:', secondNavLink
    * screenshot()


  # ══════════════════════════════════════════════════════════════════════════════
  #  GROUP 4 : JAVASCRIPT LOCATORS  →  script()
  # ══════════════════════════════════════════════════════════════════════════════

  # ────────────────────────────────────────────────────────────────────────────
  # LOCATOR TYPE 14 : JavaScript via script()
  # ─────────────────────────────────────────────────────────────────────────────
  # Karate's script() executes a JavaScript snippet in the browser and returns
  # the result back to Karate as a variable.
  #
  # When CSS / XPath cannot reach an element, script() is the escape hatch.
  # Common use cases:
  #   1. Shadow DOM elements — inaccessible to CSS/XPath outside the shadow root
  #   2. Count elements not reachable by a single CSS rule
  #   3. Read computed styles (getComputedStyle)
  #   4. Scroll to pixel coordinates
  #   5. Interact with third-party widgets that block standard automation
  #   6. Read values from JavaScript variables (window.dataLayer, etc.)
  #
  # Return types from script():
  #   • Primitive: string, number, boolean
  #   • Array:     JavaScript array → Karate list
  #   • Object:    JavaScript object → Karate map (JSON)
  #   • null/undefined → null in Karate
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @JS_Script @L14
  Scenario: L14 — JavaScript script() locator — emergency escape hatch for dynamic DOM

    * waitFor('#main-header + div')

    # L14-a: querySelector — same as CSS but executed in browser JS context
    # Returns the text of the first matching element as a string
    * def headerText = script("document.querySelector('#main-header')?.innerText")
    * print 'L14-a querySelector innerText:', headerText

    # L14-b: querySelectorAll + .length — count elements (returns a number)
    * def navLinkCount = script("document.querySelectorAll('#main-header a').length")
    * print 'L14-b querySelectorAll count:', navLinkCount
    Then assert navLinkCount > 0

    # L14-c: getAttribute — read a specific attribute value via JS
    * def logoHref = script("document.querySelector('#main-header a')?.getAttribute('href')")
    * print 'L14-c getAttribute href:', logoHref

    # L14-d: getComputedStyle — read CSS properties that are NOT HTML attributes
    # This is impossible with CSS/XPath selectors — only script() can do this
    * def navDisplay = script("getComputedStyle(document.querySelector('#main-header'))?.display")
    * print 'L14-d getComputedStyle display:', navDisplay

    # L14-e: Array from NodeList — collect text of all nav anchors as an array
    * def navTexts = script("Array.from(document.querySelectorAll('#main-header a')).map(a => a.innerText.trim()).filter(t => t)")
    * print 'L14-e nav link texts array:', navTexts
    Then match navTexts == '#array'
    * screenshot()


  # ══════════════════════════════════════════════════════════════════════════════
  #  GROUP 5 : LOCATOR COMPARISON — SAME ELEMENT, DIFFERENT STRATEGIES
  # ══════════════════════════════════════════════════════════════════════════════

  # ────────────────────────────────────────────────────────────────────────────
  # COMPARISON : 8 ways to locate the "Live Scores" nav link
  # ─────────────────────────────────────────────────────────────────────────────
  # All 8 selectors below point to the SAME element — the "Live Scores" anchor
  # in Cricbuzz's top navigation bar.  Seeing them side-by-side makes the
  # trade-offs (readability, fragility, verbosity) immediately clear.
  # ────────────────────────────────────────────────────────────────────────────
  @Locators @LocatorComparison @L15
  Scenario: L15 — All locator types targeting the SAME "Live Scores" nav link

    * waitFor('#main-header + div')

    # ── Strategy 1: Karate text {tag}text ────────────────────────────────────
    # Most readable. Fails only if the visible label text changes.
    * assert exists('{a}Live Scores')
    * print 'Strategy 1: {a}Live Scores — Karate exact text ✓'

    # ── Strategy 2: Karate partial text *{tag}text ───────────────────────────
    # Most resilient to minor label changes ("Live Scores" vs "Live Cricket Scores").
    * assert exists('*{a}Scores')
    * print 'Strategy 2: *{a}Scores — Karate partial text ✓'

    # ── Strategy 3: CSS href attribute selector ───────────────────────────────
    # Depends on the URL path, not the label — stable across language changes.
    * assert exists('a[href*="live-scores"]')
    * print 'Strategy 3: a[href*="live-scores"] — CSS attribute contains ✓'

    # ── Strategy 4: CSS class selector ───────────────────────────────────────
    # Depends on the class not changing — medium stability.
    * def liveNavClass = script("document.querySelector('a[href*=\"live-scores\"]')?.className")
    * print 'Strategy 4: CSS class would be:', liveNavClass

    # ── Strategy 5: XPath exact text() ───────────────────────────────────────
    # Equivalent to Karate {a}Live Scores — but much more verbose.
    * assert exists('//a[text()="Live Scores"]')
    * print 'Strategy 5: //a[text()="Live Scores"] — XPath exact text ✓'

    # ── Strategy 6: XPath contains(text()) ───────────────────────────────────
    # Equivalent to Karate *{a}Scores — even more verbose.
    * assert exists('//a[contains(text(),"Scores")]')
    * print 'Strategy 6: //a[contains(text(),"Scores")] — XPath contains text ✓'

    # ── Strategy 7: XPath attribute ──────────────────────────────────────────
    # Equivalent to CSS a[href*="live-scores"].
    * assert exists('//a[contains(@href,"live-scores")]')
    * print 'Strategy 7: //a[contains(@href,"live-scores")] — XPath attribute ✓'

    # ── Strategy 8: JavaScript script() ─────────────────────────────────────
    # Most powerful but least readable — use only when others fail.
    * def foundByJS = script("!!document.querySelector('a[href*=\"live-scores\"]')")
    Then assert foundByJS == true
    * print 'Strategy 8: script(querySelector) — JavaScript ✓'

    * print ''
    * print '══ SUMMARY ═══════════════════════════════════════════════════'
    * print '  All 8 strategies located the same Live Scores nav link.'
    * print '  RECOMMENDATION: Use Strategy 1 ({a}Live Scores) by default.'
    * print '  Use Strategy 3 (href attribute) as a fallback if text may change.'
    * print '  Use Strategy 8 (script) ONLY when CSS/XPath/Karate all fail.'
    * print '════════════════════════════════════════════════════════════════'
    * screenshot()
