package tests.may8th2026;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class TestNGAttributes {

    //description --> Which provides the information about what does this test case do
        //By Default it will be empty

    //testName --> Providing the name of the test case, so that it will be used while displaying the results of the test case in the reports
        //By Default it will take the testMethod Name

    //suiteName --> We will mention that whether the test case is part of regression, sanity or smoke. This is an informative field which will be used in the reports to categorize the test cases based on the suite name
        //By Default it will be empty

    //invocationCount --> We determine how many times the test case shall be executed. By Default, it will be 1

    //skipFailedInvocation --> It is used in combination with invocationCount which is widely used to stop the executions of the test case, when it is failing
        //By Default it is set to false

    //timeOut --> Here we will mention within how much time the test case execution shall be completed. Here we pass the time in milliseconds.
        //By Default it is set to zero meaning there is no time limit for every test case.

    //By Default the test cases will be executed in the ASCII order of Method names
    //In Order to control the order of the execution we will use the attribute called as "priority".
    //Priority ranges from -n to n, the default priority of every test case is zero

    //enabled = false --> Execution of the given test case will be ignored/it will not be executed
        //By Default it will be true for every test case

    //expectedExceptions --> We know that this particular test case is a known failure, do not mark this test case as failed for now

    //dependsOnMethod --> If a particular test case is dependant on the result of another test case which is the key for the business/scenario that we are testing
        //If we are putting some random method name in the dependsOnMethod which does not exist in the current class, then it will not at all execute the test cases

    //We will be using some method name which does not exist in the current class, but in future we will write the logic for it
    //ignoreMissingDependencies = true --> If the dependant method does not exist in the current class, it ignores it and continues the execution
        //By Default it will be marked as false

    //alwaysRun = true ---> Execute the test case no matter whatever happens

    @Test(description = "Executing the first test case",testName = "First Case",suiteName = "Regression",invocationCount = 10,skipFailedInvocations = true,priority = -1,expectedExceptions = RuntimeException.class)
    public void firstTestCase()
    {
        IO.println("First Set of Test Cases");
        throw new RuntimeException("Failing the first test case");
    }

    @Test(description = "Executing the second test case",testName = "Second Case",suiteName = "Smoke",invocationCount = 5,priority = -2,enabled = false)
    public void secondTestCase()
    {
        IO.println("Second Test Cases");
    }

    @Ignore //Alternate for enabled where it will ignore this particular test case while executing the complete set of test case
    //Since the execution did not complete within the stipulated amount of time, it throws ThreadTimeOutException
    @Test(description = "Executing the third test case",testName = "Third Case",suiteName = "Sanity",timeOut = 2000,priority = 10)
    public void thirdTestCase() throws InterruptedException {
        IO.println("Third Test Cases");
        Thread.sleep(9000);
    }

    //dependsOnMethod will override the priority for the test cases
    @Test(description = "Executing the fourth test case",testName = "Fourth Case",suiteName = "Smoke",priority = -3,dependsOnMethods = "jsfsjhbgf",ignoreMissingDependencies = true)
    public void forthTestCase()
    {
        IO.println("Forth Test Cases");
    }

    @Test(description = "Executing the Sixth Test Case",testName = "Sixth Case",suiteName = "Regression",priority = 7,dependsOnMethods = "LoginToTheApplication",alwaysRun = true)
    public void SixthTestCase()
    {
        IO.println("Sixth Test Cases");
    }

    @Test(description = "Login to the Application",testName = "Login Scenario")
    public void LoginToTheApplication()
    {
        IO.println("Login to the Application");
        throw new RuntimeException("Failing the Login to the Application");
    }
}
