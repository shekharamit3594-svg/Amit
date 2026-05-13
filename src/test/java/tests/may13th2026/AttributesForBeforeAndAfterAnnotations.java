package tests.may13th2026;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AttributesForBeforeAndAfterAnnotations {

    //firstTimeOnly works when the invocationCount of a particular test case is greater than 1, we do not want to trigger
    //the Before method for each and every iteration of the invocation count
    @BeforeMethod(firstTimeOnly = true)
    public void beforeMethod() {
        System.out.println("Before method for each and every test case");
    }

    //lastTimeOnly works when the invocationCount of a particular test case is greater than 1, we do not want to trigger
    //the After method for each and every iteration of the invocation count
    @AfterMethod(lastTimeOnly = true)
    public void afterMethod() {
        System.out.println("After method for each and every test case");
    }

    @Test(description = "First Test Case",invocationCount = 10)
    public void firstTest() {
        System.out.println("First Test case");
    }

    @Test(description = "Second Test Case")
    public void secondTest() {
        System.out.println("Second Test case");
    }

    @Test(description = "Third Test Case")
    public void thirdTest() {
        System.out.println("Third Test case");
    }

    @Test(description = "Fourth Test Case")
    public void fourthTest() {
        System.out.println("Fourth Test case");
    }
}
