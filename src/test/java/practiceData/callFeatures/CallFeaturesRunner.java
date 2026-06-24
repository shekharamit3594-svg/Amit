package practiceData.callFeatures;

import com.intuit.karate.junit5.Karate;

public class CallFeaturesRunner {

    @Karate.Test
    Karate callingWithArgs() {
        // Demonstrates 7.4 — call read('feature') { key: value } and capturing return values
        return Karate.run("CallingWithArgs.feature").tags("@ChainedCallsWithExamples")
                .outputHtmlReport(true)
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate callonceSharedAuth() {
        // Demonstrates 7.5 callonce, 7.6 login as reusable feature, 7.7 shared auth token
        return Karate.run("CallonceSharedAuth.feature").relativeTo(getClass());
    }
}
