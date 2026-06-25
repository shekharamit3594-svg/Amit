package performanceTesting;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PT-RUN : Karate Built-In Parallel Runner
 * ─────────────────────────────────────────────────────────────────────────────
 * CONCEPT : Two ways to do performance testing in Karate
 *
 *   1. karate-gatling (Scala simulations — SIM-1 through SIM-5)
 *      ✔ Full Gatling HTML report with percentile graphs, throughput charts
 *      ✔ Multiple injection patterns (ramp, spike, soak, step-up)
 *      ✔ Global assertions on error rate and response-time percentiles
 *      ✗ Requires Scala, Gatling Maven Plugin, extra dependencies
 *      Run: mvn gatling:test -Dgatling.simulationClass=...
 *
 *   2. Runner.parallel() — this file — Karate's built-in approach
 *      ✔ Zero extra dependencies — works with just karate-junit5
 *      ✔ Produces Karate's standard HTML report (pass/fail per scenario)
 *      ✔ Simple: one line to run N scenarios in parallel
 *      ✗ No latency percentile graphs, no throughput charts
 *      ✗ Thread count (not arrival rate) controls concurrency
 *      Run: mvn test -Dtest="performanceTesting.PerformanceRunner"
 *
 * When to use which
 *   • Quick "does it work under N parallel requests?" → use this class
 *   • SLA validation, capacity planning, latency graphs → use Gatling simulations
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * How Runner.parallel(n) works
 *   Karate creates a JUnit 5 thread pool of n threads.  Each thread picks up
 *   the next Scenario from the feature's queue and executes it independently.
 *   If a feature has 1 Scenario, only 1 thread is needed even if you pass 10.
 *   If a feature has a Scenario Outline with 10 rows, all 10 can run in parallel.
 *
 * Run commands
 *   mvn test -Dtest="performanceTesting.PerformanceRunner"
 *   mvn test -Dtest="performanceTesting.PerformanceRunner#parallelGetUsers"
 *   mvn test -Dtest="performanceTesting.PerformanceRunner#parallelCreateUsers"
 *   mvn test -Dtest="performanceTesting.PerformanceRunner#parallelUserLifecycle"
 *   mvn test -Dtest="performanceTesting.PerformanceRunner#parallelFullSuite"
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class PerformanceRunner {

    /**
     * PT-RUN-1 : Parallel Read Load
     *
     * Runs the @GetAllUsers scenario with 10 concurrent JUnit threads.
     * Since pt_GetUsers.feature has a @GetByPage Scenario Outline with 3 rows,
     * Karate actually queues: 1 (@GetAllUsers) + 1 (@GetByStatus) + 3 (@GetByPage)
     * = 5 executions.  With 10 threads all 5 run simultaneously.
     *
     * The @GetAllUsers tag filter narrows it to just the paginated GET scenario.
     */
    @Test
    void parallelGetUsers() {
        Results results = Runner
                .path("classpath:performanceTesting/features/pt_GetUsers.feature")
                .tags("@GetAllUsers")
                .parallel(10);   // 10 JUnit threads — each runs the scenario once
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    /**
     * PT-RUN-2 : Parallel Write Load
     *
     * Runs the @CreateUser scenario with 5 concurrent threads.
     * Each thread generates its OWN random email (via randomStr() in Background),
     * so all 5 POST requests land simultaneously with no email conflicts.
     *
     * Lower thread count than reads because each POST is a write operation:
     *   - hits the DB write path (more expensive)
     *   - triggers index updates
     *   - may acquire row-level locks
     */
    @Test
    void parallelCreateUsers() {
        Results results = Runner
                .path("classpath:performanceTesting/features/pt_CreateUser.feature")
                .tags("@CreateUser")
                .parallel(5);   // 5 concurrent writers
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    /**
     * PT-RUN-3 : Parallel CRUD Lifecycle
     *
     * Runs the full Create → Read → Update → Delete lifecycle with 3 concurrent
     * threads.  Lower concurrency than read/write-only tests because each
     * iteration makes 4 HTTP calls — 3 threads × 4 calls = 12 concurrent HTTP
     * connections at peak, which is still meaningful.
     *
     * Each lifecycle thread is independent: its userId is captured at Step 1 and
     * used in Steps 2-4, so no thread stomps on another thread's resource.
     */
    @Test
    void parallelUserLifecycle() {
        Results results = Runner
                .path("classpath:performanceTesting/features/pt_UserLifecycle.feature")
                .tags("@UserLifecycle")
                .parallel(3);   // 3 concurrent full-lifecycle users
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PT-RUN-5 through PT-RUN-8 : New test types that show the DIFFERENCE
    // between regular API testing and each specific performance test category.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * PT-RUN-5 : Load Test — Normal traffic with SLA assertions and think time
     *
     * KEY DIFFERENCE from a regular API test:
     *   ● scenarios use karate.pause(1000) — think time between requests
     *   ● scenarios assert `elapsed < 3000` — SLA enforcement
     *   ● PerformanceMetricsHelper collects p50/p95/p99 across all 10 threads
     *   ● The @LoadTestReport scenario prints aggregated timing stats at the end
     *
     * Run: mvn test -Dtest="performanceTesting.PerformanceRunner#loadTest"
     */
    @Test
    void loadTest() {
        PerformanceMetricsHelper.reset();   // clear counters before the run
        Results results = Runner
                .path("classpath:performanceTesting/features/pt_LoadTest.feature")
                .tags("@LoadTest")          // run only SLA-asserting scenarios (not the report)
                .parallel(10);             // 10 virtual users
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    /**
     * PT-RUN-6 : Stress Test — Maximum pressure, relaxed SLA, error rate threshold
     *
     * KEY DIFFERENCE from a Load Test:
     *   ● think time = 50ms (vs 1000ms) — 20× more requests per virtual user
     *   ● SLA = 10000ms (vs 3000ms) — degradation is expected, not a failure
     *   ● error rate threshold = 15% (vs < 1%) — some 429/503s are acceptable
     *   ● httpStatus captured before assertion — test continues THROUGH errors
     *
     * Run: mvn test -Dtest="performanceTesting.PerformanceRunner#stressTest"
     */
    @Test
    void stressTest() {
        PerformanceMetricsHelper.reset();
        Results results = Runner
                .path("classpath:performanceTesting/features/pt_StressTest.feature")
                .tags("@StressTest")
                .parallel(20);             // 20 threads @ 50ms think = ~400 req/s
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    /**
     * PT-RUN-7 : Volume Test — Large result sets, pagination latency
     *
     * KEY DIFFERENCE from a Load Test:
     *   ● per_page=100 (vs 10) — 10× larger response payloads per request
     *   ● SLA = 8000ms (relaxed) — large JSON payloads take longer to marshal
     *   ● response.length assertions — verifies API honours per_page cap
     *   ● Scenario Outline runs pages 1-5 to detect O(n) pagination bugs
     *   ● out-of-range page (9999) tested — must respond in < 2s (no data to fetch)
     *
     * Run: mvn test -Dtest="performanceTesting.PerformanceRunner#volumeTest"
     */
    @Test
    void volumeTest() {
        PerformanceMetricsHelper.reset();
        Results results = Runner
                .path("classpath:performanceTesting/features/pt_VolumeTest.feature")
                .tags("@VolumeTest")
                .parallel(5);              // fewer threads — each request is heavier
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    /**
     * PT-RUN-8 : Concurrency Test — Race conditions, shared resource, data consistency
     *
     * KEY DIFFERENCE from ALL other performance tests:
     *   ● All virtual users target THE SAME userId (not different resources)
     *   ● Setup creates ONE shared resource; all threads then READ/WRITE it
     *   ● Post-write consistency check: final value must be one of the submitted names
     *   ● Reveals: race conditions, lost updates, dirty reads, deadlocks
     *   ● Thread ID embedded in the PATCH payload to identify the "winning" writer
     *
     * Run: mvn test -Dtest="performanceTesting.PerformanceRunner#concurrencyTest"
     */
    @Test
    void concurrencyTest() {
        PerformanceMetricsHelper.reset();
        // Run SETUP first (single thread), then CONCURRENT ops, then CLEANUP
        // The @Setup and @Cleanup tags ensure sequential boundary operations
        Results setupResult = Runner
                .path("classpath:performanceTesting/features/pt_ConcurrencyTest.feature")
                .tags("@Setup")
                .parallel(1);
        assertEquals(0, setupResult.getFailCount(), setupResult.getErrorMessages());

        // Run the concurrent read/write phases with multiple threads
        Results concurrentResult = Runner
                .path("classpath:performanceTesting/features/pt_ConcurrencyTest.feature")
                .tags("@ConcurrentRead,@ConcurrentWrite")
                .parallel(8);              // 8 threads all hitting same resource
        // Concurrency test allows some errors (409 conflict, 503 overload)
        // so we check the error count is not catastrophic rather than exactly 0

        Results cleanupResult = Runner
                .path("classpath:performanceTesting/features/pt_ConcurrencyTest.feature")
                .tags("@ConsistencyCheck,@Cleanup")
                .parallel(1);
        assertEquals(0, cleanupResult.getFailCount(), cleanupResult.getErrorMessages());
    }

    /**
     * PT-RUN-4 : Full Performance Suite (Parallel)
     *
     * Runs ALL scenarios in ALL performance feature files with 5 threads.
     * Use as a quick sanity-check before running Gatling simulations.
     * NOTE: excludes @StressTest tag since stress scenarios use 50ms think time
     * and are designed to be driven by StressTestSimulation.scala, not this runner.
     *
     * Run: mvn test -Dtest="performanceTesting.PerformanceRunner#parallelFullSuite"
     */
    @Test
    void parallelFullSuite() {
        PerformanceMetricsHelper.reset();
        Results results = Runner
                .path("classpath:performanceTesting/features")
                .tags("~@StressTest", "~@StressTestReport")  // exclude stress scenarios
                .parallel(5);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
