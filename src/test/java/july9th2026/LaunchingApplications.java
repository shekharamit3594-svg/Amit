package july9th2026;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class LaunchingApplications {

    @Test(description = "Launching Applications",priority = 1)
    public void launchingApplications() throws MalformedURLException {

        //First Way:

        //Setting the path of the browser manually and launching the browser
//        System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"//src//test//resources//Drivers//chromedriver");
//
//        WebDriver driver = new ChromeDriver();
//
//        //Once the browser is launched, then open the google application
//        driver.get("https://www.google.com");

        //Above approach will not work in mac, but it will work in windows or linux based systems
        //If there is any need to upgrade the browser, we need to download the drivers manually and set the path
        //Equal chance that someone might delete the driver file in that path
        //Sometimes the driver file might be corrupted

        //Second way:
        //Using a third party tool called as WebDriverManager to manage the browsers in the given system

        //Downloads the chromedriver library/file from the official sources and sets the path of the driver automatically
        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com");

        //Third Way:
        //With the same library, we can create the driver in one single shot

        //Downloads the edgedriver file from the official sources
        //Sets the Path of the driver file automatically
        //Creates the driver object
        //Once the code execution is completed, it will close the browser automatically
        driver=WebDriverManager.edgedriver().create();

        //Launching the google site in the firefox browser
        driver.get("https://www.google.com");

        //Fourth Way:
        //Considering that we need to use a third party library to manage the browsers, Selenium team has introduced the concept of Selenium Manager
        //which internally manages the browsers automatically, and it is available from selenium 4.12.0 onwards

        driver=new FirefoxDriver();
        driver.get("https://www.google.com");

        //Fifth Way:
        //Imagine that we do not have any browsers installed in the system, and we want to perform the executions in some third party system

        driver=new RemoteWebDriver(new URL("http://localhost:4444"),new ChromeOptions());
        driver.get("https://www.google.com");
    }
}
