package uiAutomation.cricbuzz;

import com.intuit.karate.junit5.Karate;

public class CricbuzzRunner {

    @Karate.Test
    Karate homePageTests() {
        return Karate.run("CricbuzzHomePage.feature").
                tags("@NavigateFromHome").
                relativeTo(getClass());
    }

    @Karate.Test
    Karate searchTests() {
        return Karate.run("CricbuzzSearch.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate liveScoresTests() {
        return Karate.run("CricbuzzLiveScores.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate scheduleTests() {
        return Karate.run("CricbuzzSchedule.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate rankingsTests() {
        return Karate.run("CricbuzzRankings.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate browserControlsTests() {
        return Karate.run("CricbuzzBrowserControls.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate driverModesTests() {
        return Karate.run("CricbuzzDriverModes").relativeTo(getClass());
    }

    @Karate.Test
    Karate browserStateTests() {
        return Karate.run("CricbuzzBrowserState").relativeTo(getClass());
    }

    @Karate.Test
    Karate multiTabTests() {
        return Karate.run("CricbuzzMultiTab").relativeTo(getClass());
    }

    @Karate.Test
    Karate formAndDialogTests() {
        return Karate.run("CricbuzzFormAndDialog").relativeTo(getClass());
    }

    @Karate.Test
    Karate iframeTests() {
        return Karate.run("CricbuzzIframe").relativeTo(getClass());
    }

    // ── UI Locators Reference Guide ──────────────────────────────────────────
    // Runs all 15 locator scenarios in sequence.  Each scenario is independently
    // tagged (@L1 through @L15) so individual locator types can be run in isolation.
    //
    // Run a single locator type:
    //   mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzRunner#locatorsGuide" -Dkarate.options="--tags @L8"
    // Run all comparison scenarios:
    //   mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzRunner#locatorsGuide" -Dkarate.options="--tags @LocatorComparison"

    @Karate.Test
    Karate locatorsGuide() {
        return Karate.run("KarateLocatorsGuide.feature")
                .outputHtmlReport(true)
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate cssLocators() {
        return Karate.run("KarateLocatorsGuide.feature")
                .tags("@CSS_ID", "@CSS_Class", "@CSS_Attribute", "@CSS_Compound",
                      "@CSS_Combinators", "@CSS_Pseudo")
                .outputHtmlReport(true)
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate karateLocators() {
        return Karate.run("KarateLocatorsGuide.feature")
                .tags("@KarateText", "@KaratePartialText", "@KaratePlaceholder")
                .outputHtmlReport(true)
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate xpathLocators() {
        return Karate.run("KarateLocatorsGuide.feature")
                .tags("@XPath_Basic", "@XPath_Text", "@XPath_Axes")
                .outputHtmlReport(true)
                .relativeTo(getClass());
    }
}
