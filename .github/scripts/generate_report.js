#!/usr/bin/env node
/**
 * Custom HTML report generator for Rest Assured / TestNG test results.
 *
 * Reads Surefire XML files produced by maven-surefire-plugin and writes a
 * single self-contained HTML file. Uses only Node.js built-in modules —
 * no npm install required.
 *
 * Usage: node generate_report.js [surefire-dir] [output-html]
 * Defaults:
 *   surefire-dir  →  target/surefire-reports
 *   output-html   →  target/test-report.html
 */

'use strict';

const fs   = require('fs');
const path = require('path');

const SUREFIRE_DIR = process.argv[2] || 'target/surefire-reports';
const OUTPUT_FILE  = process.argv[3] || 'target/test-report.html';

// ── 1. Lightweight Surefire XML parser ───────────────────────────────────────
// Surefire XML is machine-generated and well-structured, making targeted
// regex reliable here — no third-party XML library needed.

function attr(tag, name, fallback = '') {
  const m = tag.match(new RegExp(`${name}="([^"]*)"`));
  return m ? m[1] : fallback;
}

function parseXml(xml) {
  const suiteTagMatch = xml.match(/<testsuite\b([^>]*)>/);
  if (!suiteTagMatch) return null;

  const sa = suiteTagMatch[1];
  const suite = {
    name:     attr(sa, 'name', 'Unknown Suite'),
    tests:    parseInt(attr(sa, 'tests',    '0'), 10),
    failures: parseInt(attr(sa, 'failures', '0'), 10),
    errors:   parseInt(attr(sa, 'errors',   '0'), 10),
    skipped:  parseInt(attr(sa, 'skipped',  '0'), 10),
    time:     parseFloat(attr(sa, 'time',   '0')),
    cases:    [],
  };

  // Match self-closing <testcase .../> and block <testcase ...>...</testcase>
  const caseRe = /<testcase\b([^>]*?)(?:\/>|>([\s\S]*?)<\/testcase>)/g;
  let m;
  while ((m = caseRe.exec(xml)) !== null) {
    const ca   = m[1];
    const body = m[2] || '';

    const failureM = body.match(/<failure\b([^>]*)>([\s\S]*?)<\/failure>/);
    const errorM   = body.match(/<error\b([^>]*)>([\s\S]*?)<\/error>/);
    const skippedM = body.match(/<skipped\b([^>]*?)\/?>/);

    let status = 'PASSED', message = '', stacktrace = '';

    if (failureM) {
      status     = 'FAILED';
      message    = attr(failureM[1], 'message');
      stacktrace = failureM[2].trim();
    } else if (errorM) {
      status     = 'ERROR';
      message    = attr(errorM[1], 'message');
      stacktrace = errorM[2].trim();
    } else if (skippedM) {
      status  = 'SKIPPED';
      message = attr(skippedM[1], 'message');
    }

    suite.cases.push({
      name:      attr(ca, 'name', '—'),
      classname: attr(ca, 'classname', ''),
      time:      parseFloat(attr(ca, 'time', '0')),
      status,
      message,
      stacktrace,
    });
  }

  return suite;
}

// ── 2. Collect results from all TEST-*.xml files ──────────────────────────────

let xmlFiles = [];
try {
  xmlFiles = fs.readdirSync(SUREFIRE_DIR)
    .filter(f => f.startsWith('TEST-') && f.endsWith('.xml'))
    .sort()
    .map(f => path.join(SUREFIRE_DIR, f));
} catch (e) {
  console.warn(`Warning: could not read '${SUREFIRE_DIR}': ${e.message}`);
}

if (!xmlFiles.length) {
  console.warn('No Surefire XML files found. Report will show zero results.');
}

const suites = [];
const totals = { tests: 0, passed: 0, failed: 0, skipped: 0, time: 0 };

for (const file of xmlFiles) {
  try {
    const suite = parseXml(fs.readFileSync(file, 'utf8'));
    if (!suite) { console.warn(`Could not parse ${file}`); continue; }

    suites.push(suite);
    totals.tests   += suite.tests;
    totals.failed  += suite.failures + suite.errors;
    totals.skipped += suite.skipped;
    totals.time    += suite.time;
  } catch (e) {
    console.warn(`Skipping ${file}: ${e.message}`);
  }
}

totals.passed = totals.tests - totals.failed - totals.skipped;
const passPct = totals.tests
  ? ((totals.passed / totals.tests) * 100).toFixed(1)
  : '0.0';

// ── 3. Environment metadata ───────────────────────────────────────────────────

const timestamp = new Date().toISOString().replace('T', ' ').slice(0, 16) + ' UTC';
const branch    = process.env.GITHUB_REF_NAME   || 'local';
const commitSha = (process.env.GITHUB_SHA || '').slice(0, 7) || 'local';
const runNumber = process.env.GITHUB_RUN_NUMBER  || '—';

// ── 4. HTML helpers ───────────────────────────────────────────────────────────

const esc = s => String(s)
  .replace(/&/g, '&amp;')
  .replace(/</g, '&lt;')
  .replace(/>/g, '&gt;')
  .replace(/"/g, '&quot;');

const BADGE = {
  PASSED:  '<span class="badge pass">&#10004; PASSED</span>',
  FAILED:  '<span class="badge fail">&#10008; FAILED</span>',
  ERROR:   '<span class="badge error">&#9888; ERROR</span>',
  SKIPPED: '<span class="badge skip">&#8212; SKIPPED</span>',
};

const ROW_CLASS = {
  PASSED: 'row-pass', FAILED: 'row-fail',
  ERROR:  'row-error', SKIPPED: 'row-skip',
};

function safeId(prefix, index) {
  return 'd_' + prefix.replace(/[^a-zA-Z0-9]/g, '_') + '_' + index;
}

function renderSuite(suite) {
  const suitePassed  = suite.cases.filter(c => c.status === 'PASSED').length;
  const suiteFailed  = suite.cases.filter(c => c.status === 'FAILED' || c.status === 'ERROR').length;
  const suiteSkipped = suite.cases.filter(c => c.status === 'SKIPPED').length;
  const suiteCls     = suiteFailed ? 'suite-fail' : 'suite-pass';

  const rows = suite.cases.map((tc, i) => {
    const id         = safeId(suite.name + tc.name, i);
    const badge      = BADGE[tc.status] || BADGE.PASSED;
    const rowCls     = ROW_CLASS[tc.status] || 'row-pass';
    const shortClass = tc.classname.split('.').pop() || '—';
    const hasDetail  = !!(tc.message || tc.stacktrace);

    const detailRow = hasDetail ? `
        <tr class="error-row" id="${id}">
          <td colspan="5">
            <div class="error-box">
              <p class="error-msg">&#10060; ${esc(tc.message)}</p>
              ${tc.stacktrace ? `<pre class="stack">${esc(tc.stacktrace)}</pre>` : ''}
            </div>
          </td>
        </tr>` : '';

    return `
        <tr class="${rowCls}${hasDetail ? ' clickable' : ''}"${hasDetail ? ` onclick="toggle('${id}')"` : ''}>
          <td>${esc(shortClass)}</td>
          <td>${esc(tc.name)}</td>
          <td>${badge}</td>
          <td>${tc.time.toFixed(2)}s</td>
          <td class="expand-cell">${hasDetail ? '&#9660; Details' : '&mdash;'}</td>
        </tr>${detailRow}`;
  }).join('');

  return `
  <section class="suite ${suiteCls}">
    <h2>
      ${esc(suite.name)}
      <span class="suite-meta">
        ${suite.cases.length} tests &nbsp;&bull;&nbsp;
        <span class="c-pass">${suitePassed} passed</span>&nbsp;
        <span class="c-fail">${suiteFailed} failed</span>&nbsp;
        <span class="c-skip">${suiteSkipped} skipped</span>
      </span>
    </h2>
    <table>
      <thead>
        <tr><th>Class</th><th>Test Method</th><th>Status</th><th>Duration</th><th></th></tr>
      </thead>
      <tbody>${rows}
      </tbody>
    </table>
  </section>`;
}

const allSuitesHtml = suites.map(renderSuite).join('\n');

const barColor = passPct >= 100 ? '#27ae60' : passPct < 60 ? '#e74c3c' : '#f39c12';
const [overallLabel, overallCls] =
  totals.tests  === 0  ? ['NO TESTS RUN', 'banner-skip'] :
  totals.failed  >  0  ? ['BUILD FAILED',  'banner-fail'] :
                         ['BUILD PASSED',  'banner-pass'];

// ── 5. Full HTML document ─────────────────────────────────────────────────────

const HTML = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Test Report &mdash; Run #${runNumber}</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
                   "Helvetica Neue", Arial, sans-serif;
      background: #f0f2f5; color: #1a1a2e; font-size: 14px; line-height: 1.5;
    }

    /* Header */
    header {
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 60%, #0f3460 100%);
      color: #e8eaf6; padding: 28px 36px 20px;
    }
    header h1 { font-size: 22px; font-weight: 700; letter-spacing: .4px; }
    .meta-row {
      display: flex; gap: 28px; flex-wrap: wrap;
      margin-top: 8px; font-size: 12px; opacity: .65;
    }
    .meta-row span::before { content: "• "; }

    /* Banner */
    .banner { padding: 10px 36px; font-size: 13px; font-weight: 700; letter-spacing: .6px; text-align: center; }
    .banner-pass { background: #27ae60; color: #fff; }
    .banner-fail { background: #e74c3c; color: #fff; }
    .banner-skip { background: #95a5a6; color: #fff; }

    /* Summary cards */
    .summary { display: flex; gap: 16px; padding: 24px 36px; flex-wrap: wrap; }
    .card {
      flex: 1; min-width: 110px; background: #fff;
      border-radius: 12px; padding: 18px 22px;
      box-shadow: 0 2px 10px rgba(0,0,0,.07); text-align: center;
    }
    .card .val { font-size: 38px; font-weight: 800; line-height: 1.1; }
    .card .lbl { font-size: 11px; text-transform: uppercase; letter-spacing: 1.2px; margin-top: 6px; color: #888; }
    .card.total .val { color: #34495e; }
    .card.pass  .val { color: #27ae60; }
    .card.fail  .val { color: #e74c3c; }
    .card.skip  .val { color: #f39c12; }
    .card.time  .val { font-size: 28px; color: #2980b9; }

    /* Progress bar */
    .progress-wrap { padding: 0 36px 20px; }
    .progress-bar { height: 10px; border-radius: 5px; background: #e0e0e0; overflow: hidden; }
    .progress-bar .fill { height: 100%; border-radius: 5px; background: ${barColor}; width: ${passPct}%; }
    .progress-label { text-align: right; font-size: 12px; color: #777; margin-top: 5px; }

    /* Suite sections */
    .suite {
      background: #fff; margin: 0 36px 22px;
      border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,.07); overflow: hidden;
    }
    .suite h2 {
      padding: 14px 20px; font-size: 14px; font-weight: 600;
      border-left: 5px solid #ccc; background: #fafbfc;
      display: flex; align-items: baseline; gap: 10px; flex-wrap: wrap;
    }
    .suite-pass h2 { border-color: #27ae60; }
    .suite-fail h2 { border-color: #e74c3c; }
    .suite-meta { font-size: 12px; font-weight: 400; color: #888; }
    .c-pass { color: #27ae60; font-weight: 600; }
    .c-fail { color: #e74c3c; font-weight: 600; }
    .c-skip { color: #f39c12; font-weight: 600; }

    /* Table */
    table { width: 100%; border-collapse: collapse; }
    thead th {
      background: #f4f5f7; padding: 10px 18px; text-align: left;
      font-size: 11px; text-transform: uppercase; letter-spacing: .9px;
      color: #666; border-bottom: 2px solid #eaeaea;
    }
    tbody tr { border-bottom: 1px solid #f2f2f2; transition: background .12s; }
    tbody tr:last-child { border-bottom: none; }
    tbody td { padding: 11px 18px; vertical-align: middle; }
    .clickable { cursor: pointer; }
    .clickable:hover { background: #f7f9ff !important; }
    .row-fail  td:first-child { border-left: 3px solid #e74c3c; padding-left: 15px; }
    .row-error td:first-child { border-left: 3px solid #e67e22; padding-left: 15px; }
    .row-skip  td:first-child { border-left: 3px solid #f39c12; padding-left: 15px; }
    .row-pass  td:first-child { border-left: 3px solid #27ae60; padding-left: 15px; }

    /* Badges */
    .badge {
      display: inline-block; padding: 3px 11px; border-radius: 20px;
      font-size: 11px; font-weight: 700; letter-spacing: .4px; white-space: nowrap;
    }
    .badge.pass  { background: #e8f8f0; color: #1a6b38; }
    .badge.fail  { background: #fdecea; color: #b71c1c; }
    .badge.error { background: #fff3e0; color: #bf360c; }
    .badge.skip  { background: #fffde7; color: #8a6d00; }

    /* Error detail */
    .expand-cell { color: #999; font-size: 12px; white-space: nowrap; }
    .error-row { display: none; }
    .error-box {
      background: #fff8f8; border-left: 4px solid #e74c3c;
      padding: 14px 18px; margin: 6px 0; border-radius: 0 8px 8px 0;
    }
    .error-msg { color: #c0392b; font-weight: 600; margin-bottom: 10px; word-break: break-word; }
    .stack {
      font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
      font-size: 12px; line-height: 1.6; color: #555;
      white-space: pre-wrap; word-break: break-all;
      max-height: 320px; overflow-y: auto;
      background: #fff; padding: 10px; border-radius: 4px; border: 1px solid #f0d0d0;
    }

    /* Footer */
    footer { text-align: center; padding: 28px 36px; font-size: 12px; color: #aaa; }
  </style>
</head>
<body>

  <header>
    <h1>&#129514; API Test Report &mdash; Run #${runNumber}</h1>
    <div class="meta-row">
      <span>Branch: ${esc(branch)}</span>
      <span>Commit: ${esc(commitSha)}</span>
      <span>Generated: ${timestamp}</span>
      <span>Suite: Banking API (TestNG)</span>
    </div>
  </header>

  <div class="banner ${overallCls}">${overallLabel}</div>

  <div class="summary">
    <div class="card total"><div class="val">${totals.tests}</div><div class="lbl">Total</div></div>
    <div class="card pass"> <div class="val">${totals.passed}</div><div class="lbl">Passed</div></div>
    <div class="card fail"> <div class="val">${totals.failed}</div><div class="lbl">Failed</div></div>
    <div class="card skip"> <div class="val">${totals.skipped}</div><div class="lbl">Skipped</div></div>
    <div class="card time"> <div class="val">${totals.time.toFixed(1)}s</div><div class="lbl">Duration</div></div>
  </div>

  <div class="progress-wrap">
    <div class="progress-bar"><div class="fill"></div></div>
    <div class="progress-label">${passPct}% of tests passed</div>
  </div>

  ${allSuitesHtml}

  <footer>
    Generated By: <strong>Testing Professor</strong> &nbsp;&bull;&nbsp; ${timestamp}
  </footer>

  <script>
    function toggle(id) {
      const row = document.getElementById(id);
      if (row) row.style.display = row.style.display === 'table-row' ? 'none' : 'table-row';
    }
  </script>
</body>
</html>`;

// ── 6. Write output ───────────────────────────────────────────────────────────

const outDir = path.dirname(OUTPUT_FILE);
if (outDir && outDir !== '.') fs.mkdirSync(outDir, { recursive: true });

fs.writeFileSync(OUTPUT_FILE, HTML, 'utf8');

console.log(`Report written -> ${OUTPUT_FILE}`);
console.log(`Results  : ${totals.tests} total  |  ${totals.passed} passed  |  ${totals.failed} failed  |  ${totals.skipped} skipped`);
console.log(`Pass rate: ${passPct}%  |  Duration: ${totals.time.toFixed(2)}s`);
