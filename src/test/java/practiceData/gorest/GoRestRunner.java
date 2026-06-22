package practiceData.gorest;

import com.intuit.karate.junit5.Karate;

public class GoRestRunner {

    @Karate.Test
    Karate testUsers() {
        //Run the given feature file which is present inside the same folder
        return Karate.run("GoRestFeature.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate performQueryParameters() {
        //Run the given feature file which is present inside the same folder
        return Karate.run("PerformingQueryParameters.feature").relativeTo(getClass());
    }

}
