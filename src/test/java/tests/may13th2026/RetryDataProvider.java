package tests.may13th2026;

import org.testng.IDataProviderMethod;
import org.testng.IRetryDataProvider;

public class RetryDataProvider implements IRetryDataProvider {

    int initialCounter=0;
    int maxCounter=3;

    @Override
    public boolean retry(IDataProviderMethod dataProvider)
    {
        if(initialCounter<maxCounter) {
            initialCounter++;
            return true;
        }

        else
        {
            return false;
        }
    }
}
