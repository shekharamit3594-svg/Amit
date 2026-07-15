package framework;

import framework.constants.BrowserNames;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.URL;

@UtilityClass
//All the methods in this class will be static in nature by default
//We cannot create the object of this class
public class BrowserUtils {

    private WebDriver getWebDriver(String browserName){

        return switch(browserName.toUpperCase())
        {
            case "CHROME" -> new ChromeDriver();
            case "FIREFOX" -> new FirefoxDriver();
            case "EDGE" -> new EdgeDriver();
            case "SAFARI" -> new SafariDriver();

            default -> throw new GenericExceptions("Given browser: "+browserName+" is not supported");
        };

    }

    @SneakyThrows
    private WebDriver getRemoteWebDriver(String browserName){

        DesiredCapabilities capabilities = new DesiredCapabilities();
        return switch(browserName.toUpperCase())
        {
            case "CHROME" -> new RemoteWebDriver(new URL("http://localhost:4444"),capabilities.setBrowserName("chrome"));
            case "FIREFOX" -> new RemoteWebDriver(new URL("http://localhost:4444"),capabilities.setBrowserName("firefox"));
            case "EDGE" -> new RemoteWebDriver(new URL("http://localhost:4444"),capabilities.setBrowserName("edge"));
            default -> throw new GenericExceptions("Given browser: "+browserName+" is not supported in the remote browser");
        };
    }

    public WebDriver fetchDriver(BrowserNames browserName, boolean... remoteExecution)
    {
        String originalBrowserName = browserName.getName();
        if(remoteExecution.length > 0)
        {
            return remoteExecution[0]?getRemoteWebDriver(originalBrowserName):getWebDriver(originalBrowserName);
        }

        else
        {
            return getWebDriver(originalBrowserName);
        }
    }
}
