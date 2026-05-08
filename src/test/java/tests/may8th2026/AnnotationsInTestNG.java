package tests.may8th2026;

import org.testng.annotations.*;

public class AnnotationsInTestNG extends ParentSetOfTestCases{

    //BeforeSuite --> Runs once before all tests in the suite. Perfect for global setup like starting services or initializing drivers.

    //BeforeTest
    //BeforeGroups

    //BeforeClass --> Runs once before the first test method in the current class. Useful for creating the objects related to those class
        //Setup of framework related objects with respect to the execution etc.

    //BeforeMethod --> Runs once before each and every test case.
            //Ex: Login to the application, Launching the application

    //Test --> Actual Test Case will be executed

    //AfterMethod --> Runs once after each and every test case.
        //Ex: Logout of the application, Closing the application

    //AfterClass --> Runs once after all the test methods are executed in the current class.
        //Ex: Capturing the results of the test case, Generating some reports

    //AfterGroups
    //AfterTest

    //AfterSuite --> Runs once after the complete suite is being executed. Perfect for global teardown like stopping services or closing drivers.


    //BeforeSuite --> BeforeTest --> BeforeGroups --> BeforeClass --> BeforeMethod --> Test --> AfterMethod --> AfterClass --> AfterGroups --> AfterTest --> AfterSuite


    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Before Suite execution is happening");
    }

    @BeforeClass
    public void beforeClass() {
        System.out.println("Before Class execution is happening");
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("Before Method execution is happening for every test case");
    }

    @Test
    public void firstTestCase() {
        System.out.println("First Test Case execution is happening");
    }

    @Test
    public void secondTestCase() {
        System.out.println("Second Test Case execution is happening");
    }

    @Test
    public void thirdTestCase() {
        System.out.println("Third Test Case execution is happening");
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("After Method execution is happening");
    }

    @AfterClass
    public void afterClass() {
        System.out.println("After Class execution is happening");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("After Suite execution is happening");
    }
}
