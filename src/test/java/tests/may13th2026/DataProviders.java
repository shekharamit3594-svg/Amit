package tests.may13th2026;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tests.may12th2026.RetryLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DataProviders {

    int initialCounter=0;
    //Data Provider --> Nothing but providing the test data to the test cases

    //There are different return Types of Data Provider:
    //1. Return a 1D Object Array
    //2. Return a 2D Object Array
    //3. Return an Iterator of Collection Object --> Introduced since 7.8 or 7.9
    //4. Return an Iterator of Collection Object Array --> Introduced since 7.8 or 7.9

    @DataProvider(name = "getData_1DArray")
    public Object[] getData_OneDimensionalArray()
    {
        Object[] data = new Object[4];

        data[0]= true;
        data[1]= 'h';
        data[2]= "Hello World";
        data[3]= 198.242;

        return data;
    }

    @Test(description = "Running the test cases with multiple sets of test data",dataProvider = "getData_1DArray")
    public void testWithDataProvider_OneDimensionalArray(Object data)
    {
        IO.println("The test data is :"+data);
    }

    @DataProvider(name = "getData_2DArray",retryUsing = RetryDataProvider.class,cacheDataForTestRetries = false)
    public Object[][] getData_TwoDimensionalArray()
    {
        Object[][] data = new Object[4][2];

        //Referring to the data that is present at first row and first column with value as 500
        data[0][0]= 500;
        data[0][1]= 200;

        //Referring to the data that is present at second row and first column with value as "Hello World"
        data[1][0]= "Hello World";
        data[1][1]= -29901;


        Arrays.fill(data[2],"Sample Info");

        Arrays.fill(data[3],298.242);

        if(initialCounter!=2) {
            initialCounter++;
            throw new RuntimeException("Error in Generating the data");
        }

        else
            return data;
    }

    @Test(description = "Running the test cases with multiple sets of test data -> 2D Array",dataProvider = "getData_2DArray")
    public void testWithDataProvider_TwoDimensionalArray(Object dataOne, Object dataTwo)
    {
        IO.println("The test data is :"+dataOne+" for the first column");
        IO.println("The test data is :"+dataTwo+" for the second column");

        IO.println("***".repeat(10));
    }

    @DataProvider(name = "getData_IteratorCollections")
    public Iterator<String> getData_IteratorCollections()
    {
        List<String> names= new ArrayList<>();

        names.add("First");
        names.add("Second");
        names.add("Third");
        names.add("Fourth");

        return names.iterator();
    }

    @Test(description = "Running the test cases with multiple sets of test data -> Iterator Collections",dataProvider = "getData_IteratorCollections")
    public void testWithDataProvider_IteratorCollections(String name)
    {
        IO.println("The test data from the iterator is :"+name);
    }

}
