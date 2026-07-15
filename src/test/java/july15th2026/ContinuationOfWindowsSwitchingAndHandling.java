package july15th2026;

import framework.BrowserUtils;
import framework.PathUtils;
import framework.SeleniumUtils;
import framework.constants.BrowserNames;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

public class ContinuationOfWindowsSwitchingAndHandling {

    WebDriver driver;
    SeleniumUtils seleniumUtils;

    @BeforeMethod
    public void setupOfFrameworkObjects()
    {
        driver= BrowserUtils.fetchDriver(BrowserNames.CHROME);
        seleniumUtils=new SeleniumUtils(driver);
    }

    @Test(description = "Continuation of Windows Switching And Handling",priority = 1)
    @SneakyThrows
    public void switchToWindow()
    {
        seleniumUtils.launchApplications().launchApplication("https://www.google.com");
        String googelHandle=driver.getWindowHandle(); //Gets the unique reference number for the window/tab launched

        seleniumUtils.launchApplications().switchToNewTab("https://www.cricbuzz.com"); //Both the applications will be launched in the first window

        seleniumUtils.launchApplications().switchToNewWindow("https://www.youtube.com");
        String youtubeHandle=driver.getWindowHandle();

        seleniumUtils.launchApplications().switchToNewTab("https://www.snapchat.com"); //Both the applications will be launched in the second window
        String snapHandle=driver.getWindowHandle();

        seleniumUtils.launchApplications().switchToNewWindow("https://www.instagram.com"); //This will be launched in the third window

        PathUtils.applySleep(3);
        driver.switchTo().window(googelHandle); //Switching to the google tab

        PathUtils.applySleep(3);
        driver.switchTo().window(youtubeHandle);

        PathUtils.applySleep(3);
        driver.switchTo().window(snapHandle);
    }

    @Test(description = "Continuation of Windows Switching And Handling - Part 2",priority = 2)
    @SneakyThrows
    public void switchToMultipleWindowsAndTabs()
    {
        seleniumUtils.launchApplications().launchApplication("https://www.google.com");

        seleniumUtils.launchApplications().switchToNewTab("https://www.cricbuzz.com"); //Both the applications will be launched in the first window

        seleniumUtils.launchApplications().switchToNewWindow("https://www.youtube.com");

        seleniumUtils.launchApplications().switchToNewTab("https://www.snapchat.com"); //Both the applications will be launched in the second window

        seleniumUtils.launchApplications().switchToNewWindow("https://www.instagram.com"); //This will be launched in the third window

        PathUtils.applySleep(3);
        //driver.switchTo().window(googelHandle); //Switching to the google tab

//        PathUtils.applySleep(3);
//        driver.switchTo().window(youtubeHandle);
//
//        PathUtils.applySleep(3);
//        driver.switchTo().window(snapHandle);

        //Fetches all the unique reference numbers for the windows/tabs launched
        Set<String> handles=driver.getWindowHandles();

        handles.forEach(s->{
            driver.switchTo().window(s);
            PathUtils.applySleep(3);
        });
    }

}
