package sampleRunner;

import com.intuit.karate.junit5.Karate;

//Karate internally uses JUNIT as the testing engine
public class SampleRunner {

    @Karate.Test
    Karate testUsers() {
        //Run the given feature file which is present inside the same folder
        return Karate.run("users.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate testUsers_Syntax() {
        //Run the given feature file which is present inside the same folder
        return Karate.run("SyntaxesInKarateFramework.feature").tags("@JSONParsing").relativeTo(getClass());
    }

}
