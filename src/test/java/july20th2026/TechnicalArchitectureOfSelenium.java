package july20th2026;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

public class TechnicalArchitectureOfSelenium {

    @Test(description = "Launching Driver in Different Ways",priority = 1)
    public void launchDriverInDifferentWays(){

        //First Way: Recommended to use when you have to test only in Chrome Browser
//        ChromeDriver driver=new ChromeDriver();
//        driver.get("https://www.amazon.com/");

        //Second Way: Recommended to use when you have to test the application in Chrome and Edge browsers only
//        ChromiumDriver driver=new EdgeDriver();
//        driver.navigate().to("https://www.amazon.com/");

        //Third Way: Not Recommended to use, as ChromiumDriver does not launch any sort of browser
        //and we need to pass lot of attributes
        //WebDriver driver=new ChromiumDriver();

        //Fourth Way: Not Recommended one, as we cannot launch any sort of application using the SearchContext Interface
        //It is primarily used to find the elements on the application
//        SearchContext s2=new FirefoxDriver();
//        s2.get

        //Fifth Way: Recommended to use when we want to perform the executions in a third party system
        //WebDriver driver=new RemoteWebDriver();

        //Sixth Way: Recommended to use
//        RemoteWebDriver driver=new FirefoxDriver();
//        driver.get("https://www.google.com");

        //Seventh Way: Not Receommended as we have to implement the logic for all the methods which is not our requirement

//        SearchContext s3=new WebDriver() {
//            @Override
//            public void get(String url) {
//
//            }
//
//            @Override
//            public @Nullable String getCurrentUrl() {
//                return "";
//            }
//
//            @Override
//            public @Nullable String getTitle() {
//                return "";
//            }
//
//            @Override
//            public List<WebElement> findElements(By by) {
//                return List.of();
//            }
//
//            @Override
//            public WebElement findElement(By by) {
//                return null;
//            }
//
//            @Override
//            public @Nullable String getPageSource() {
//                return "";
//            }
//
//            @Override
//            public void close() {
//
//            }
//
//            @Override
//            public void quit() {
//
//            }
//
//            @Override
//            public Set<String> getWindowHandles() {
//                return Set.of();
//            }
//
//            @Override
//            public String getWindowHandle() {
//                return "";
//            }
//
//            @Override
//            public TargetLocator switchTo() {
//                return null;
//            }
//
//            @Override
//            public Navigation navigate() {
//                return null;
//            }
//
//            @Override
//            public Options manage() {
//                return null;
//            }
//        }

        //8th Way: Not Recommended to use as ChromiumDriver is not eligible to be used for launching the browser
        //RemoteWebDriver r2=new ChromiumDriver();
    }
}
