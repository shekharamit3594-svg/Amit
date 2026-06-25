package performanceTesting;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Thread-safe, in-process metrics collector for Karate performance feature files.
 *
 * WHY THIS CLASS EXISTS
 * ─────────────────────
 * Regular Karate runners report PASS/FAIL per scenario.  They do not collect or
 * aggregate timing data across parallel threads.  This helper fills that gap:
 *
 *   • startRequest()     — captures the instant before an HTTP call
 *   • recordSuccess()    — records elapsed time for a successful request
 *   • recordError()      — records elapsed time for a failed/errored request
 *   • getMetrics()       — returns aggregated stats (min/mean/p50/p95/p99/max,
 *                          throughput req/s, error rate %)
 *   • reset()            — clears all state between test runs
 *
 * USAGE IN A FEATURE FILE
 * ───────────────────────
 *   Background:
 *     * def metrics = Java.type('performanceTesting.PerformanceMetricsHelper')
 *
 *   Scenario: timed request
 *     * def start = metrics.startRequest()
 *     Given url baseUrl
 *     When method get
 *     * def elapsed = metrics.recordSuccess(start)
 *     * assert elapsed < 3000          ← SLA assertion
 *
 * THREAD SAFETY
 * ─────────────
 * CopyOnWriteArrayList and AtomicInteger ensure correctness when N parallel
 * Karate threads (virtual users) write concurrently.  getMetrics() sorts a
 * snapshot copy so in-flight requests do not affect the sorted view.
 */
public class PerformanceMetricsHelper {

    private static volatile long testStartTime = System.currentTimeMillis();
    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    private static final AtomicInteger errorCount    = new AtomicInteger(0);
    private static final List<Long> responseTimes    = new CopyOnWriteArrayList<>();

    // ── Public API ────────────────────────────────────────────────────────────

    /** Reset all counters — call once before a fresh test run. */
    public static synchronized void reset() {
        testStartTime = System.currentTimeMillis();
        totalRequests.set(0);
        errorCount.set(0);
        responseTimes.clear();
    }

    /**
     * Returns the current epoch time in ms.
     * Call immediately before the HTTP step in the feature file.
     */
    public static long startRequest() {
        return System.currentTimeMillis();
    }

    /**
     * Records a successful request.
     * @param startTimeMs  value returned by startRequest()
     * @return  elapsed time in ms (for SLA assertion in the feature file)
     */
    public static long recordSuccess(long startTimeMs) {
        long elapsed = System.currentTimeMillis() - startTimeMs;
        responseTimes.add(elapsed);
        totalRequests.incrementAndGet();
        return elapsed;
    }

    /**
     * Records a failed request (4xx/5xx or timeout).
     * @param startTimeMs  value returned by startRequest()
     * @return  elapsed time in ms
     */
    public static long recordError(long startTimeMs) {
        long elapsed = System.currentTimeMillis() - startTimeMs;
        responseTimes.add(elapsed);
        totalRequests.incrementAndGet();
        errorCount.incrementAndGet();
        return elapsed;
    }

    /**
     * Returns aggregated metrics across ALL virtual users / iterations.
     *
     * Map keys:
     *   totalRequests   — count of all recorded requests
     *   errors          — count of failed requests
     *   errorRatePct    — error rate as a percentage
     *   throughputPerSec — req/s since reset() or first startRequest()
     *   minMs / maxMs / meanMs — min, max, arithmetic mean response times
     *   p50Ms / p90Ms / p95Ms / p99Ms — response time percentiles
     */
    public static Map<String, Object> getMetrics() {
        List<Long> sorted = new ArrayList<>(responseTimes); // snapshot
        Collections.sort(sorted);
        int n = sorted.size();

        long durationMs = Math.max(1, System.currentTimeMillis() - testStartTime);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalRequests",   totalRequests.get());
        m.put("errors",          errorCount.get());
        m.put("errorRatePct",    n > 0 ? round1dp(errorCount.get() * 100.0 / n) : 0.0);
        m.put("throughputPerSec", round1dp(n * 1000.0 / durationMs));

        if (!sorted.isEmpty()) {
            long sum = 0;
            for (long t : sorted) sum += t;
            m.put("minMs",  sorted.get(0));
            m.put("maxMs",  sorted.get(n - 1));
            m.put("meanMs", Math.round(sum / (double) n));
            m.put("p50Ms",  percentile(sorted, 0.50));
            m.put("p90Ms",  percentile(sorted, 0.90));
            m.put("p95Ms",  percentile(sorted, 0.95));
            m.put("p99Ms",  percentile(sorted, 0.99));
        }
        return m;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static long percentile(List<Long> sorted, double p) {
        int idx = (int) Math.min(sorted.size() - 1, Math.floor(sorted.size() * p));
        return sorted.get(idx);
    }

    private static double round1dp(double v) {
        return Math.round(v * 10) / 10.0;
    }
}
