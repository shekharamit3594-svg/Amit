#!/usr/bin/env python3
"""
Custom HTML report generator for Rest Assured / TestNG test results.

Reads Surefire XML files produced by maven-surefire-plugin and writes a single
self-contained HTML file — no external dependencies, no internet access needed.

Usage:
  python3 generate_report.py [surefire-dir] [output-html]
  Defaults:
    surefire-dir  →  target/surefire-reports
    output-html   →  target/test-report.html
"""

import os
import sys
import glob
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from html import escape

# ── Config ───────────────────────────────────────────────────────────────────

SUREFIRE_DIR = sys.argv[1] if len(sys.argv) > 1 else "target/surefire-reports"
OUTPUT_FILE  = sys.argv[2] if len(sys.argv) > 2 else "target/test-report.html"

# ── 1. Parse Surefire XML files ───────────────────────────────────────────────

class TestCase:
    __slots__ = ("name", "classname", "time_s", "status", "message", "stacktrace")

    def __init__(self, name, classname, time_s, status, message, stacktrace):
        self.name       = name
        self.classname  = classname
        self.time_s     = time_s
        self.status     = status       # PASSED | FAILED | ERROR | SKIPPED
        self.message    = message
        self.stacktrace = stacktrace


suites = []   # [(suite_name, [TestCase, ...]), ...]
totals = dict(tests=0, passed=0, failed=0, skipped=0, time=0.0)

xml_files = sorted(glob.glob(os.path.join(SUREFIRE_DIR, "TEST-*.xml")))
if not xml_files:
    print(f"⚠  No Surefire XML files found in '{SUREFIRE_DIR}'.")
    print("   Report will be generated with zero results.")

for xml_file in xml_files:
    try:
        root = ET.parse(xml_file).getroot()
    except ET.ParseError as exc:
        print(f"⚠  Skipping {xml_file}: {exc}")
        continue

    suite_name = root.attrib.get("name", os.path.basename(xml_file))
    cases = []

    for tc in root.findall("testcase"):
        name      = tc.attrib.get("name", "—")
        classname = tc.attrib.get("classname", "")
        time_s    = float(tc.attrib.get("time", "0") or "0")

        failure = tc.find("failure")
        error   = tc.find("error")
        skipped = tc.find("skipped")

        if failure is not None:
            status     = "FAILED"
            message    = failure.attrib.get("message", "") or ""
            stacktrace = (failure.text or "").strip()
        elif error is not None:
            status     = "ERROR"
            message    = error.attrib.get("message", "") or ""
            stacktrace = (error.text or "").strip()
        elif skipped is not None:
            status     = "SKIPPED"
            message    = skipped.attrib.get("message", "") or ""
            stacktrace = ""
        else:
            status     = "PASSED"
            message    = ""
            stacktrace = ""

        cases.append(TestCase(name, classname, time_s, status, message, stacktrace))

    suites.append((suite_name, cases))

    # Use suite-level attributes — more reliable than counting individual elements
    t_tests   = int(root.attrib.get("tests",    0))
    t_fail    = int(root.attrib.get("failures", 0)) + int(root.attrib.get("errors", 0))
    t_skip    = int(root.attrib.get("skipped",  0))
    t_time    = float(root.attrib.get("time",   0))

    totals["tests"]   += t_tests
    totals["failed"]  += t_fail
    totals["skipped"] += t_skip
    totals["time"]    += t_time

totals["passed"] = totals["tests"] - totals["failed"] - totals["skipped"]
pass_pct = round(totals["passed"] / totals["tests"] * 100, 1) if totals["tests"] else 0.0

# ── 2. Environment metadata ──────────────────────────────────────────────────

timestamp  = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
branch     = os.environ.get("GITHUB_REF_NAME", "local")
commit_sha = (os.environ.get("GITHUB_SHA", "") or "")[:7] or "local"
run_number = os.environ.get("GITHUB_RUN_NUMBER", "—")

# ── 3. HTML helpers ───────────────────────────────────────────────────────────

BADGE = {
    "PASSED":  '<span class="badge pass">&#10004; PASSED</span>',
    "FAILED":  '<span class="badge fail">&#10008; FAILED</span>',
    "ERROR":   '<span class="badge error">&#9888; ERROR</span>',
    "SKIPPED": '<span class="badge skip">&#8212; SKIPPED</span>',
}

ROW_CLASS = {
    "PASSED": "row-pass",
    "FAILED": "row-fail",
    "ERROR":  "row-error",
    "SKIPPED":"row-skip",
}

def safe_id(text, index):
    """Produce a valid DOM id from arbitrary text."""
    slug = "".join(c if c.isalnum() else "_" for c in text)
    return f"detail_{slug}_{index}"


def render_suite(suite_index, suite_name, cases):
    suite_passed  = sum(1 for c in cases if c.status == "PASSED")
    suite_failed  = sum(1 for c in cases if c.status in ("FAILED", "ERROR"))
    suite_skipped = sum(1 for c in cases if c.status == "SKIPPED")
    suite_cls     = "suite-fail" if suite_failed else "suite-pass"

    rows = []
    for i, tc in enumerate(cases):
        detail_id  = safe_id(suite_name + tc.name, i)
        badge      = BADGE.get(tc.status, BADGE["PASSED"])
        row_cls    = ROW_CLASS.get(tc.status, "row-pass")
        has_detail = bool(tc.message or tc.stacktrace)

        # Short class name (last segment only)
        short_class = tc.classname.split(".")[-1] if tc.classname else "—"

        # Error / stack detail row (hidden by default, toggled by JS)
        detail_html = ""
        if has_detail:
            stack_html = (
                f'<pre class="stack">{escape(tc.stacktrace)}</pre>'
                if tc.stacktrace else ""
            )
            detail_html = f"""
      <tr class="error-row" id="{detail_id}">
        <td colspan="5">
          <div class="error-box">
            <p class="error-msg">&#10060; {escape(tc.message)}</p>
            {stack_html}
          </div>
        </td>
      </tr>"""

        toggle_attrs = (
            f'onclick="toggle(\'{detail_id}\')" class="{row_cls} clickable"'
            if has_detail else
            f'class="{row_cls}"'
        )
        expand_cell = "&#9660; Details" if has_detail else "&mdash;"

        rows.append(f"""
      <tr {toggle_attrs}>
        <td>{escape(short_class)}</td>
        <td>{escape(tc.name)}</td>
        <td>{badge}</td>
        <td>{tc.time_s:.2f}s</td>
        <td class="expand-cell">{expand_cell}</td>
      </tr>{detail_html}""")

    rows_html = "\n".join(rows)

    return f"""
  <section class="suite {suite_cls}">
    <h2>
      {escape(suite_name)}
      <span class="suite-meta">
        {len(cases)} tests &nbsp;&bull;&nbsp;
        <span class="c-pass">{suite_passed} passed</span>&nbsp;
        <span class="c-fail">{suite_failed} failed</span>&nbsp;
        <span class="c-skip">{suite_skipped} skipped</span>
      </span>
    </h2>
    <table>
      <thead>
        <tr>
          <th>Class</th>
          <th>Test Method</th>
          <th>Status</th>
          <th>Duration</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {rows_html}
      </tbody>
    </table>
  </section>"""


all_suites_html = "\n".join(
    render_suite(idx, name, cases)
    for idx, (name, cases) in enumerate(suites)
)

# Progress bar colour: green if 100%, red if <60%, amber otherwise
if pass_pct == 100:
    bar_color = "#27ae60"
elif pass_pct < 60:
    bar_color = "#e74c3c"
else:
    bar_color = "#f39c12"

# Overall status banner
if totals["tests"] == 0:
    overall_label, overall_cls = "NO TESTS RUN", "banner-skip"
elif totals["failed"] > 0:
    overall_label, overall_cls = "BUILD FAILED", "banner-fail"
else:
    overall_label, overall_cls = "BUILD PASSED", "banner-pass"

# ── 4. Full HTML document ─────────────────────────────────────────────────────

HTML = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Test Report &mdash; Run #{run_number}</title>
  <style>
    /* ── Reset ────────────────────────────────────────────────────────── */
    *, *::before, *::after {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
                   "Helvetica Neue", Arial, sans-serif;
      background: #f0f2f5;
      color: #1a1a2e;
      font-size: 14px;
      line-height: 1.5;
    }}

    /* ── Header ───────────────────────────────────────────────────────── */
    header {{
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 60%, #0f3460 100%);
      color: #e8eaf6;
      padding: 28px 36px 20px;
    }}
    header h1 {{ font-size: 22px; font-weight: 700; letter-spacing: .4px; }}
    .meta-row {{
      display: flex; gap: 28px; flex-wrap: wrap;
      margin-top: 8px; font-size: 12px; opacity: .65;
    }}
    .meta-row span::before {{ content: "&#x25CF;  "; font-size: 8px; vertical-align: middle; }}

    /* ── Overall banner ───────────────────────────────────────────────── */
    .banner {{
      padding: 10px 36px; font-size: 13px; font-weight: 700;
      letter-spacing: .6px; text-align: center;
    }}
    .banner-pass {{ background: #27ae60; color: #fff; }}
    .banner-fail {{ background: #e74c3c; color: #fff; }}
    .banner-skip {{ background: #95a5a6; color: #fff; }}

    /* ── Summary cards ────────────────────────────────────────────────── */
    .summary {{
      display: flex; gap: 16px; padding: 24px 36px; flex-wrap: wrap;
    }}
    .card {{
      flex: 1; min-width: 110px; background: #fff;
      border-radius: 12px; padding: 18px 22px;
      box-shadow: 0 2px 10px rgba(0,0,0,.07); text-align: center;
    }}
    .card .val {{
      font-size: 38px; font-weight: 800; line-height: 1.1;
    }}
    .card .lbl {{
      font-size: 11px; text-transform: uppercase; letter-spacing: 1.2px;
      margin-top: 6px; color: #888;
    }}
    .card.total .val {{ color: #34495e; }}
    .card.pass  .val {{ color: #27ae60; }}
    .card.fail  .val {{ color: #e74c3c; }}
    .card.skip  .val {{ color: #f39c12; }}
    .card.time  .val {{ font-size: 28px; color: #2980b9; }}

    /* ── Progress bar ─────────────────────────────────────────────────── */
    .progress-wrap {{ padding: 0 36px 20px; }}
    .progress-bar {{
      height: 10px; border-radius: 5px;
      background: #e0e0e0; overflow: hidden;
    }}
    .progress-bar .fill {{
      height: 100%; border-radius: 5px;
      background: {bar_color};
      width: {pass_pct}%;
    }}
    .progress-label {{
      text-align: right; font-size: 12px;
      color: #777; margin-top: 5px;
    }}

    /* ── Suite sections ───────────────────────────────────────────────── */
    .suite {{
      background: #fff;
      margin: 0 36px 22px;
      border-radius: 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,.07);
      overflow: hidden;
    }}
    .suite h2 {{
      padding: 14px 20px;
      font-size: 14px; font-weight: 600;
      border-left: 5px solid #ccc;
      background: #fafbfc;
      display: flex; align-items: baseline; gap: 10px;
      flex-wrap: wrap;
    }}
    .suite-pass h2 {{ border-color: #27ae60; }}
    .suite-fail h2 {{ border-color: #e74c3c; }}
    .suite-meta {{
      font-size: 12px; font-weight: 400; color: #888;
    }}
    .c-pass {{ color: #27ae60; font-weight: 600; }}
    .c-fail {{ color: #e74c3c; font-weight: 600; }}
    .c-skip {{ color: #f39c12; font-weight: 600; }}

    /* ── Table ────────────────────────────────────────────────────────── */
    table {{ width: 100%; border-collapse: collapse; }}
    thead th {{
      background: #f4f5f7;
      padding: 10px 18px; text-align: left;
      font-size: 11px; text-transform: uppercase;
      letter-spacing: .9px; color: #666;
      border-bottom: 2px solid #eaeaea;
    }}
    tbody tr {{
      border-bottom: 1px solid #f2f2f2;
      transition: background .12s;
    }}
    tbody tr:last-child {{ border-bottom: none; }}
    tbody td {{ padding: 11px 18px; vertical-align: middle; }}
    .clickable {{ cursor: pointer; }}
    .clickable:hover {{ background: #f7f9ff !important; }}

    /* Left accent stripe per status */
    .row-fail  td:first-child {{ border-left: 3px solid #e74c3c; padding-left: 15px; }}
    .row-error td:first-child {{ border-left: 3px solid #e67e22; padding-left: 15px; }}
    .row-skip  td:first-child {{ border-left: 3px solid #f39c12; padding-left: 15px; }}
    .row-pass  td:first-child {{ border-left: 3px solid #27ae60; padding-left: 15px; }}

    /* ── Status badges ────────────────────────────────────────────────── */
    .badge {{
      display: inline-block; padding: 3px 11px;
      border-radius: 20px; font-size: 11px;
      font-weight: 700; letter-spacing: .4px; white-space: nowrap;
    }}
    .badge.pass  {{ background: #e8f8f0; color: #1a6b38; }}
    .badge.fail  {{ background: #fdecea; color: #b71c1c; }}
    .badge.error {{ background: #fff3e0; color: #bf360c; }}
    .badge.skip  {{ background: #fffde7; color: #8a6d00; }}

    /* ── Expand column ────────────────────────────────────────────────── */
    .expand-cell {{ color: #999; font-size: 12px; white-space: nowrap; }}

    /* ── Error detail row ─────────────────────────────────────────────── */
    .error-row {{ display: none; }}
    .error-box {{
      background: #fff8f8;
      border-left: 4px solid #e74c3c;
      padding: 14px 18px; margin: 6px 0;
      border-radius: 0 8px 8px 0;
    }}
    .error-msg {{
      color: #c0392b; font-weight: 600;
      margin-bottom: 10px; word-break: break-word;
    }}
    .stack {{
      font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
      font-size: 12px; line-height: 1.6; color: #555;
      white-space: pre-wrap; word-break: break-all;
      max-height: 320px; overflow-y: auto;
      background: #fff; padding: 10px; border-radius: 4px;
      border: 1px solid #f0d0d0;
    }}

    /* ── Footer ───────────────────────────────────────────────────────── */
    footer {{
      text-align: center; padding: 28px 36px;
      font-size: 12px; color: #aaa;
    }}
  </style>
</head>
<body>

  <header>
    <h1>&#129514; API Test Report &mdash; Run #{run_number}</h1>
    <div class="meta-row">
      <span>Branch: {escape(branch)}</span>
      <span>Commit: {escape(commit_sha)}</span>
      <span>Generated: {timestamp}</span>
      <span>Suite: Banking API (TestNG)</span>
    </div>
  </header>

  <div class="banner {overall_cls}">{overall_label}</div>

  <div class="summary">
    <div class="card total">
      <div class="val">{totals["tests"]}</div>
      <div class="lbl">Total</div>
    </div>
    <div class="card pass">
      <div class="val">{totals["passed"]}</div>
      <div class="lbl">Passed</div>
    </div>
    <div class="card fail">
      <div class="val">{totals["failed"]}</div>
      <div class="lbl">Failed</div>
    </div>
    <div class="card skip">
      <div class="val">{totals["skipped"]}</div>
      <div class="lbl">Skipped</div>
    </div>
    <div class="card time">
      <div class="val">{totals["time"]:.1f}s</div>
      <div class="lbl">Duration</div>
    </div>
  </div>

  <div class="progress-wrap">
    <div class="progress-bar"><div class="fill"></div></div>
    <div class="progress-label">{pass_pct}% of tests passed</div>
  </div>

  {all_suites_html}

  <footer>
    Generated by <strong>generate_report.py</strong> &nbsp;&bull;&nbsp; {timestamp}
  </footer>

  <script>
    function toggle(id) {{
      var row = document.getElementById(id);
      if (!row) return;
      row.style.display = (row.style.display === "table-row") ? "none" : "table-row";
    }}
  </script>
</body>
</html>"""

# ── 5. Write output ───────────────────────────────────────────────────────────

out_dir = os.path.dirname(OUTPUT_FILE)
if out_dir:
    os.makedirs(out_dir, exist_ok=True)

with open(OUTPUT_FILE, "w", encoding="utf-8") as fh:
    fh.write(HTML)

print(f"Report written -> {OUTPUT_FILE}")
print(
    f"Results  : {totals['tests']} total  |  "
    f"{totals['passed']} passed  |  "
    f"{totals['failed']} failed  |  "
    f"{totals['skipped']} skipped"
)
print(f"Pass rate: {pass_pct}%  |  Duration: {totals['time']:.2f}s")
