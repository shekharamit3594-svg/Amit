package framework;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Optional;

//Main purpose of this class is to maintain all the generic functions / browser activities related to selenium
@FieldDefaults(level = AccessLevel.PRIVATE) //Ensures that all the variables are private in nature
@Accessors(fluent = true)
@Getter
public class SeleniumUtils {

    WebDriver driver;
    SwitchingTabsAndWindows launchApplications;

    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
        this.launchApplications=new HandlingWindowsAndTabs(driver);
    }

    public interface SwitchingTabsAndWindows
    {
        void switchToNewWindow(String url);
        void switchToNewTab(String url);
        void launchApplication(String url);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class HandlingWindowsAndTabs implements SwitchingTabsAndWindows
    {
        WebDriver driver;
        public HandlingWindowsAndTabs(WebDriver driver) {
            this.driver=driver;
        }

        public void switchToNewWindow(String url) {
            driver.switchTo().newWindow(WindowType.WINDOW);
            launchApplication(url);
        }

        public void switchToNewTab(String url) {
            driver.switchTo().newWindow(WindowType.TAB);
            launchApplication(url);
        }

        @Override
        public void launchApplication(String url) {
            if(!url.startsWith("http"))
            {
                throw new GenericExceptions("URL is not starting with http, please check it");
            }

            else if(url.isBlank())
            {
                throw new GenericExceptions("URL is blank");
            }

            Optional.ofNullable(url).orElseThrow(()->new GenericExceptions("URL is a null value which is not acceptable"));

            driver.get(url);
        }
    }
}
