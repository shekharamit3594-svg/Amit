package tests.may8th2026;

import org.testng.annotations.*;

public class ParentSetOfTestCases
{

    @BeforeSuite
    public void beforeSuiteParent()
    {
        IO.println("ParentSetOfTestCases before suite");
    }

    @AfterSuite
    public void afterSuiteParent()
    {
        IO.println("ParentSetOfTestCases after suite");
    }

    @BeforeClass
    public void beforeClassParent()
    {
        IO.println("ParentSetOfTestCases beforeClass");
    }

    @AfterClass
    public void afterClassParent()
    {
        IO.println("ParentSetOfTestCases afterClass");
    }

    @BeforeMethod
    public void beforeMethodParent()
    {
        IO.println("ParentSetOfTestCases beforeMethod");
    }

    @Parameters("URL")
    @AfterMethod
    public void afterMethodParent(String appURL)
    {
        IO.println("ParentSetOfTestCases afterMethod");
        IO.println("URL :"+appURL);
    }

    @Test
    public void testParentSetOfTestCases()
    {
        IO.println("ParentSetOfTestCases testParentSetOfTestCases");
    }
}
