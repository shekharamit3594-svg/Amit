package tests.may12th2026;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class GroupingOfTestCases {

/*
        Grouping of Test Cases --> It is the concept of categorizing the test cases based on the functionality, module, priority, etc.
        and then we can execute the test cases based on the group name.
        We can create our own custom groups 
        We can also have multiple groups for a single test case and we can also have a test case without any group.
*/
    
    @Test(description = "First Test Case",groups = "Smoke")
    public void firstTest(){
        IO.println("First test cases");
    }

    @Parameters({"URL","Browser"}) //Mention the variables which we have declared in the testng.xml file, so that we can use those variables in the test case
    @Test(description = "Second Test Case",groups = "Regression")
    public void secondTest(String appURL,String browserName){
        IO.println("Second test cases");

        IO.println("URL :"+appURL);
        IO.println("Browser :"+browserName);
    }
    
    @Test(description = "Third Test Case",groups = "Sanity")
    public void thirdTest(){
        IO.println("Third test cases");
    }
    
    @Test(description = "Fourth Test Case",groups = {"Smoke","Regression"})
    public void fourthTest() {
        IO.println("Fourth test cases");
    }
}
