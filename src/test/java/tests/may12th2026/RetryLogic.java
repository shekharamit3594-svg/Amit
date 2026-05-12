package tests.may12th2026;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

//IRetryAnalyser is the interface present in TESTNG Library where we need to override the method present inside the interface
public class RetryLogic implements IRetryAnalyzer {

    int initialCounter = 0;
    int maxNoOfRetries = 3;

    @Override
    public boolean retry(ITestResult result) {

        //result.getStatus() --> Gets the exact status of the Test Execution
        //result.getStatus() == ITestResult.FAILURE --> Checking if the given test case is failing or not
        if (result.getStatus() == ITestResult.FAILURE && initialCounter < maxNoOfRetries) {
            initialCounter++;
            return true;
        }

        else
        {
            return false;
        }
    }
}
