package uiAutomation.cricbuzz;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parallel runner for all Cricbuzz UI feature files.
 *
 * Runner.path() discovers every .feature file under the given classpath directory.
 * .parallel(N) spawns N threads, each with its own independent browser session —
 * Karate manages driver lifecycle per-thread so screenshots and state do not bleed
 * across parallel scenarios.
 *
 * Usage:
 *   All UI features in parallel : mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzParallelRunner"
 *   With Selenium Grid           : mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzParallelRunner" -Dwebdriver.remote=true
 *   Tag filter                   : mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzParallelRunner" -Dkarate.options="--tags @BrowserControls"
 *
 * Recommended thread count = number of browser slots available on your Grid node.
 * The docker-compose.yml chrome-node is configured with SE_NODE_MAX_SESSIONS=3,
 * so parallel(3) is the default here.
 */
public class CricbuzzParallelRunner {

    @Test
    void runAllCricbuzzFeaturesInParallel() {
        Results results = Runner
                .path("classpath:uiAutomation/cricbuzz")
                .tags("~@ignore")
                .outputCucumberJson(true)
                .parallel(3);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    @Test
    void runBrowserControlsInParallel() {
        Results results = Runner
                .path("classpath:uiAutomation/cricbuzz")
                .tags("@BrowserControls")
                .parallel(2);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    @Test
    void runHomePageAndSearchInParallel() {
        Results results = Runner
                .path("classpath:uiAutomation/cricbuzz")
                .tags("@HomePageVerification,@SearchFunctionality")
                .parallel(2);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
