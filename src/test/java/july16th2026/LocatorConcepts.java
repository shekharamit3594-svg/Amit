package july16th2026;

import com.google.common.io.Files;
import framework.*;
import framework.constants.BrowserNames;
import lombok.SneakyThrows;
import org.openqa.selenium.*;
import org.testng.annotations.Test;

import java.io.File;

public class LocatorConcepts {

    //Element --> Button, TextBox, DropDown, etc....

    //Locator:
    //Locator is used to locate a button, textbox, radio button, drop down etc.. in a web page
    //Locator is like the latitude and longitude of the element that we want to find.

    //Why do we need to locate:
    //Because the automation tool needs to know where to click on a button, enter data in a text box
    //select a value from the drop down, etc..

    //Different types of locators in Selenium:
    //1. ID --> one of the easiest locators to find, and it shall be unique in that web page
    //2. NAME
    //3. CLASSNAME
    //4. LINKTEXT
    //5. PARTIALLINKTEXT
    //6. XPATH
    //7. TAG NAME
    //8. CSS Selector
    //9. Relative Locators

    @SneakyThrows
    @Test(description = "Understanding the concept of locators",priority = 1)
    public void performDifferentSetOfActions()
    {
        WebDriver driver= BrowserUtils.fetchDriver(BrowserNames.CHROME);

        Reports reports=new Reports(driver);
        ElementUtils elementUtils=new ElementUtils(driver);
        SeleniumUtils seleniumUtils = new SeleniumUtils(driver,elementUtils,reports);

        //driver.manage().window().maximize(); //Maximize the browser
        seleniumUtils.launchApplications().launchApplication("https://seleniumsessions.testingprofessor.net/");

        //practice-form-btn

        //driver --> Represents the browser that you have launched
        //driver.findElement() --> For the browser launched, you need to find the element in the web page, on what basis?????

        //Syntax of finding an element on the basis of "ID" attribute:
        //driver.findElement(By.id(value));

        //WebElement btn_PracticeForm=driver.findElement(By.id("practice-form-btn"));
        //btn_PracticeForm.click(); //.click() is used to perform the click action
        seleniumUtils.clickActions().performClick(By.id("practice-form-btn"));

        //WebElement txt_FirstName=driver.findElement(By.id("firstName"));
        //txt_FirstName.sendKeys("Amit"); //.sendKeys is used to enter the data into the text box
        seleniumUtils.typeData().enterText(By.id("firstName"),"Amit");

        //WebElement txt_LastName=driver.findElement(By.id("lastName"));
        //txt_LastName.sendKeys("Singh");
        seleniumUtils.typeData().enterText(By.id("lastName"),"Singh");

        //Finding an element on the basis of name attribute:
        //driver.findElement(By.name(value));

       // WebElement txt_Email=driver.findElement(By.name("email"));
//        txt_Email.sendKeys("Amit@Singh.com");
        seleniumUtils.typeData().enterText(By.name("email"),"Amit@Singh.com");

        //WebElement txt_Password=driver.findElement(By.name("password"));
        //txt_Password.sendKeys("Test@123");
        seleniumUtils.typeData().enterText(By.name("password"),"Test@123");

        //WebElement txt_ConfirmPassword=driver.findElement(By.name("confirmPassword"));
        //txt_ConfirmPassword.sendKeys("Test@123");
        seleniumUtils.typeData().enterText(By.name("confirmPassword"),"Test@123");

        //WebElement txt_City=driver.findElement(By.name("city"));
        //txt_City.sendKeys("Bangalore");
        seleniumUtils.typeData().enterText(By.name("city"),"Bangalore");

        PathUtils.applySleep(4);

        //ElementClickInterceptedException --> Element is found, and it is ready to perform the click operation, but some other element or the screen is blocking us to perform the action or activity
//        WebElement cbx_CSharp=driver.findElement(By.name("skill-c#"));
//        cbx_CSharp.click();

        //If ID or name is alphanumeric do not use them at all

        //(TakesScreenshot) --> It is the function used to capture the screenshot
        //((TakesScreenshot)driver) --> Captures the screenshot of the browser launched
        //((TakesScreenshot)driver).getScreenshotAs() --> Captures the screenshot of the browser launched in the form of file, base64 encoding or a byte array

        //Screenshot captured will be stored in the temp folder
//        File src=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//        File dest=new File("SampleImage.png");
//
//        //Copies the screenshot from the temp folder to the desired folder
//        Files.copy(src,dest);

        reports.captureScreenshot();

        PathUtils.applySleep(8);
        driver.quit(); //Close the browser
    }
}
