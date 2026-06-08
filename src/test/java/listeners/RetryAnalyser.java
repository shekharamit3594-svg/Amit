package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyser implements IRetryAnalyzer {

    int initalCounter=0;
    int maxCounter=3;

    @Override
    public boolean retry(ITestResult result) {
        if(result.getStatus() == ITestResult.FAILURE){
            if(initalCounter<maxCounter)
            {
                initalCounter++;
                return true;
            }

            else
            {
                return false;
            }
        }

        else
        {
            return false;
        }
    }
}
