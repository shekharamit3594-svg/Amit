package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyser implements IRetryAnalyzer {

    int initialCounter=0;
    int maxCounter=3;

    @Override
    public boolean retry(ITestResult result) {
        if(result.getStatus() == ITestResult.FAILURE){
            if(initialCounter<maxCounter)
            {
                initialCounter++;
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
