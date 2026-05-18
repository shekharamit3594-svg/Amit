package tests.may15th2026;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OtherAttributesForOtherAnnotations {

    //Only For the Regression Group, Before method needs to be executed
    @BeforeMethod(groups = {"Regression","Smoke","Sanity"},onlyForGroups = "Regression")
    public void beforeMethod() {
        IO.println("Before the test case execution");
    }

    //Only for the Regression Group, After Method needs to be executed
    @AfterMethod(groups = {"Regression","Smoke","Sanity"},onlyForGroups = "Regression")
    public void afterMethod() {
        IO.println("After the test case execution");
    }

    @Test(description = "Sample Test Case One",groups = "Regression")
    public void sampleTestCaseOne() {
        IO.println("Sample Test Case One");
    }

    @Test(description = "Sample Test Case Two",groups = "Smoke")
    public void sampleTestCaseTwo() {
        IO.println("Sample Test Case Two");
    }

    @Test(description = "Sample Test Case Three",groups = "Regression")
    public void sampleTestCaseThree() {
        IO.println("Sample Test Case Three");
    }

    @Test(description = "Sample Test Case Four",groups = "Sanity")
    public void sampleTestCaseFour() {
        IO.println("Sample Test Case Four");
    }
}
