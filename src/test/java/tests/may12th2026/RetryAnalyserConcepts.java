package tests.may12th2026;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

//RetryAnalyser --> Retrying/Re-run the failed test cases for certain number of iterations, before we actually fail the test case
public class RetryAnalyserConcepts {

    int counter = 0;

    //retryAnalyser is the attribute used to mention which class is responsible for retrying the failed test cases
    @Test(description = "Understanding the Retry Concept",retryAnalyzer = RetryLogic.class)
    public void rerunFailedTestCases()
    {
        IO.println("Re run the failed test cases");

        if(counter != 1)
        {
            counter++;
            throw new RuntimeException("Failing the test cases/scenarios");
        }
    }
}
