package july10th2026;

import framework.BrowserUtils;
import framework.PathUtils;
import framework.SeleniumUtils;
import framework.constants.BrowserNames;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WindowsSwitchingAndHandling {

    WebDriver driver;
    SeleniumUtils seleniumUtils;

    @BeforeMethod
    public void setupOfFrameworkObjects()
    {
        driver= BrowserUtils.fetchDriver(BrowserNames.CHROME);
        seleniumUtils=new SeleniumUtils(driver);
    }

    @SneakyThrows //Alternative to using throws Exception
    @Test(description = "Creating a New Tab",priority = 1)
    public void createNewTab(){

        //By Default selenium will wait for 5 mins for the page to be loaded
        //There is a Page Loading Strategy concept involved while loading the web page.
        //By Default it will be set to wait till the complete page is loaded

        seleniumUtils.launchApplications().launchApplication("https://www.google.com");
        //driver.get("https://www.google.com");

        //Open a new tab --> Selenium 4 Function
//        driver.switchTo().newWindow(WindowType.TAB);
//        driver.get("https://www.amazon.com");
        seleniumUtils.launchApplications().switchToNewTab("https://www.amazon.com");


//        //We are launching multiple tabs at a time
//        driver.switchTo().newWindow(WindowType.TAB);
//        driver.get("https://www.youtube.com");
        seleniumUtils.launchApplications().switchToNewTab("https://www.youtube.com");

//        driver.switchTo().newWindow(WindowType.TAB);
//        driver.get("https://www.gmail.com");

        seleniumUtils.launchApplications().switchToNewTab("https://www.gmail.com");

//        driver.switchTo().newWindow(WindowType.TAB);
//        driver.get("https://www.redbus.com");

        seleniumUtils.launchApplications().switchToNewTab("https://www.redbus.com");

//        driver.switchTo().newWindow(WindowType.TAB);
//        driver.get("https://www.cricbuzz.com");

        seleniumUtils.launchApplications().switchToNewTab("https://www.cricbuzz.com");

        //Pauses the execution for around 6 secs
        //Thread.sleep(6000);
        PathUtils.applySleep(6);

        //Closes that particular tab
        //driver.close();

        //Closes the browser that has been opened
        driver.quit();
    }

    @SneakyThrows
    @Test(description = "Creating a New Window",priority = 2)
    public void createNewWindow(){

        //driver.get("https://www.google.com");
        seleniumUtils.launchApplications().launchApplication("https://www.google.com");

        //Creating a new Window --> Selenium 4 Approach
//        driver.switchTo().newWindow(WindowType.WINDOW);
//        driver.get("https://www.youtube.com");
        seleniumUtils.launchApplications().switchToNewWindow("https://www.youtube.com");

//        driver.switchTo().newWindow(WindowType.WINDOW);
//        driver.get("https://www.pintrest.com");
        seleniumUtils.launchApplications().switchToNewWindow("https://www.pintrest.com");

//        driver.switchTo().newWindow(WindowType.WINDOW);
//        driver.get("https://www.cricinfo.com");

        seleniumUtils.launchApplications().switchToNewWindow("https://www.cricinfo.com");

//        driver.switchTo().newWindow(WindowType.WINDOW);
//        driver.get("https://www.snapchat.com");

        seleniumUtils.launchApplications().switchToNewTab("https://www.snapchat.com");

        //Pauses the execution for around 6 secs
        //Thread.sleep(6000);
        PathUtils.applySleep(6);

        //Closes that particular tab
        //driver.close();

        //Closes the browser that has been opened
        driver.quit();
    }
}
