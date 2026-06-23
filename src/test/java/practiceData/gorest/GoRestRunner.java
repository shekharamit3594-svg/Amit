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

    @Karate.Test
    Karate dataDrivenUserCreation() {
        //Run the data-driven feature that reads test data from a CSV file
        return Karate.run("DataDrivenUserCreation.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate understandingKarateVariables() {
        //Run all variable concept scenarios — filter by tag to run a specific concept
        return Karate.run("UnderstandingKarateVariables.feature")
                .tags("@JSFunctions")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate dataDrivenExamples() {
        //Run all four Examples patterns — use tags to isolate a specific pattern or dataset
        return Karate.run("DataDrivenExamples.feature")
                .tags("@JSONFileExamples")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate putOperationUsingPostId() {
        //Run the PUT operation that uses the ID captured from PostOperation.feature
        return Karate.run("PutOperationUsingPostId.feature").relativeTo(getClass());
    }

}
