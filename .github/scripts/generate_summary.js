#!/usr/bin/env node
/**
 * Emits GitHub-flavoured Markdown to stdout.
 * Pipe to $GITHUB_STEP_SUMMARY so results appear inline on the Actions run page.
 *
 * Usage: node generate_summary.js [surefire-dir]
 * Default surefire-dir: target/surefire-reports
 */

'use strict';

const fs   = require('fs');
const path = require('path');

const SUREFIRE_DIR = process.argv[2] || 'target/surefire-reports';

// ── Minimal Surefire XML parser (mirrors generate_report.js, no dependencies) ─

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

  const caseRe = /<testcase\b([^>]*?)(?:\/>|>([\s\S]*?)<\/testcase>)/g;
  let m;
  while ((m = caseRe.exec(xml)) !== null) {
    const ca   = m[1];
    const body = m[2] || '';
    const failureM = body.match(/<failure\b([^>]*)>([\s\S]*?)<\/failure>/);
    const errorM   = body.match(/<error\b([^>]*)>([\s\S]*?)<\/error>/);
    const skippedM = body.match(/<skipped\b([^>]*?)\/?>/);

    let status = 'PASSED', message = '', stacktrace = '';
    if      (failureM) { status = 'FAILED';  message = attr(failureM[1], 'message'); stacktrace = failureM[2].trim(); }
    else if (errorM)   { status = 'ERROR';   message = attr(errorM[1],   'message'); stacktrace = errorM[2].trim();   }
    else if (skippedM) { status = 'SKIPPED'; message = attr(skippedM[1], 'message'); }

    suite.cases.push({
      name:      attr(ca, 'name', '—'),
      classname: attr(ca, 'classname', '').split('.').pop() || '—',
      time:      parseFloat(attr(ca, 'time', '0')),
      status, message, stacktrace,
    });
  }
  return suite;
}

// ── Read all TEST-*.xml files ──────────────────────────────────────────────────

let xmlFiles = [];
try {
  xmlFiles = fs.readdirSync(SUREFIRE_DIR)
    .filter(f => f.startsWith('TEST-') && f.endsWith('.xml'))
    .sort()
    .map(f => path.join(SUREFIRE_DIR, f));
} catch (e) {
  console.log('> [!WARNING]');
  console.log(`> No Surefire reports found in \`${SUREFIRE_DIR}\` — tests may not have run.`);
  process.exit(0);
}

const suites = [];
const totals = { tests: 0, passed: 0, failed: 0, skipped: 0, time: 0 };

for (const file of xmlFiles) {
  try {
    const suite = parseXml(fs.readFileSync(file, 'utf8'));
    if (!suite) continue;
    suites.push(suite);
    totals.tests   += suite.tests;
    totals.failed  += suite.failures + suite.errors;
    totals.skipped += suite.skipped;
    totals.time    += suite.time;
  } catch (_) {}
}

totals.passed = totals.tests - totals.failed - totals.skipped;
const passPct = totals.tests
  ? ((totals.passed / totals.tests) * 100).toFixed(1)
  : '0.0';

const STATUS_ICON  = { PASSED: '✅', FAILED: '❌', ERROR: '❌', SKIPPED: '⏭️' };
const overallEmoji = totals.failed > 0 ? '❌' : totals.tests === 0 ? '⚠️' : '✅';
const overallLabel = totals.failed > 0 ? 'FAILED' : totals.tests === 0 ? 'NO TESTS RAN' : 'PASSED';

// ── Build markdown ─────────────────────────────────────────────────────────────

const out = [];

out.push(`## ${overallEmoji} Banking API Tests — ${overallLabel}`);
out.push('');
out.push('| Metric | Value |');
out.push('|---|---|');
out.push(`| Total tests | **${totals.tests}** |`);
out.push(`| ✅ Passed   | **${totals.passed}** |`);
out.push(`| ❌ Failed   | **${totals.failed}** |`);
out.push(`| ⏭️ Skipped  | **${totals.skipped}** |`);
out.push(`| Pass rate  | **${passPct}%** |`);
out.push(`| Duration   | **${totals.time.toFixed(2)}s** |`);
out.push('');

for (const suite of suites) {
  const suiteFailed  = suite.cases.filter(c => c.status === 'FAILED' || c.status === 'ERROR').length;
  const suiteEmoji   = suiteFailed > 0 ? '❌' : '✅';

  out.push(`### ${suiteEmoji} ${suite.name}`);
  out.push('');
  out.push('| Class | Test | Status | Duration |');
  out.push('|---|---|:---:|---:|');

  for (const tc of suite.cases) {
    const icon = STATUS_ICON[tc.status] || '❓';
    out.push(`| \`${tc.classname}\` | ${tc.name} | ${icon} ${tc.status} | ${tc.time.toFixed(2)}s |`);
  }
  out.push('');
}

// Failure details as collapsible blocks
const allFailed = suites.flatMap(s =>
  s.cases.filter(c => c.status === 'FAILED' || c.status === 'ERROR')
);

if (allFailed.length > 0) {
  out.push('---');
  out.push('');
  out.push('### ❌ Failure Details');
  out.push('');

  for (const tc of allFailed) {
    out.push('<details>');
    out.push(`<summary><b>${tc.classname} → ${tc.name}</b></summary>`);
    out.push('');
    if (tc.message) {
      out.push(`**Message:** ${tc.message}`);
      out.push('');
    }
    if (tc.stacktrace) {
      out.push('```');
      out.push(tc.stacktrace.slice(0, 3000));   // cap at 3 k chars to avoid huge summaries
      out.push('```');
    }
    out.push('</details>');
    out.push('');
  }
}

out.push('---');
out.push('');
out.push(`*Suite: Banking API · TestNG · Branch: \`${process.env.GITHUB_REF_NAME || 'local'}\` · Run #${process.env.GITHUB_RUN_NUMBER || '—'}*`);

console.log(out.join('\n'));
