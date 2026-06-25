package uiAutomation.cricbuzzPOM;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runner for the Cricbuzz POM test suite.
 *
 * Structure:
 *   pages/  — page objects (@ignore features, called via call read)
 *   tests/  — test scenarios that compose page objects
 *
 * @Karate.Test methods run a single test feature sequentially (one scenario at a time).
 * runAllPOMTests() uses Runner.parallel(3) to run all five test features concurrently,
 * each with its own independent browser session — matching SE_NODE_MAX_SESSIONS=3
 * in docker-compose.yml.
 *
 * Run commands:
 *   All POM tests (parallel)  : mvn test -Dtest="uiAutomation.cricbuzzPOM.CricbuzzPOMRunner#runAllPOMTests"
 *   Single feature             : mvn test -Dtest="uiAutomation.cricbuzzPOM.CricbuzzPOMRunner#homePage"
 *   Single tag across features : mvn test -Dtest="uiAutomation.cricbuzzPOM.CricbuzzPOMRunner" -Dkarate.options="--tags @POM"
 *   Remote Selenium Grid       : add -Dwebdriver.remote=true (resolved in karate-config.js)
 */
public class CricbuzzPOMRunner {

    @Karate.Test
    Karate homePage() {
        return Karate.run("tests/HomePageTest").relativeTo(getClass());
    }

    @Karate.Test
    Karate search() {
        return Karate.run("tests/SearchTest").relativeTo(getClass());
    }

    @Karate.Test
    Karate liveScores() {
        return Karate.run("tests/LiveScoresTest").relativeTo(getClass());
    }

    @Karate.Test
    Karate schedule() {
        return Karate.run("tests/ScheduleTest").relativeTo(getClass());
    }

    @Karate.Test
    Karate rankings() {
        return Karate.run("tests/RankingsTest").relativeTo(getClass());
    }

    @Test
    void runAllPOMTests() {
        Results results = Runner
                .path("classpath:uiAutomation/cricbuzzPOM/tests")
                .tags("~@ignore")
                .parallel(3);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
