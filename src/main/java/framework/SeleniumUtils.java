package framework;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

//Main purpose of this class is to maintain all the generic functions / browser activities related to selenium
@FieldDefaults(level = AccessLevel.PRIVATE) //Ensures that all the variables are private in nature
@Accessors(fluent = true)
@Getter
public class SeleniumUtils {

    WebDriver driver;
    CreatingNewTabsAndWindows launchApplications;
    SwitchToWindows switchBetweenTabsOrWindows;

    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
        this.launchApplications=new CreatingWindowsAndTabs(driver);
        this.switchBetweenTabsOrWindows=new SwitchBetweenWindows(driver);
    }

    public interface CreatingNewTabsAndWindows
    {
        String launchNewWindow(String url);
        String launchNewTab(String url);
        String launchApplication(String url);
    }

    public interface SwitchToWindows
    {
        void switchToWindow(String windowHandle);
        void switchAllWindows();
        void switchToParticularWindow(String title);
        void closeParticularTabOrWindow(String title);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    static class CreatingWindowsAndTabs implements CreatingNewTabsAndWindows
    {
        WebDriver driver;

        public String launchNewWindow(String url) {
            driver.switchTo().newWindow(WindowType.WINDOW);
            return launchApplication(url);
        }

        public String launchNewTab(String url) {
            driver.switchTo().newWindow(WindowType.TAB);
            return launchApplication(url);
        }

        @Override
        public String launchApplication(String url) {
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

            return driver.getWindowHandle();
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    static class SwitchBetweenWindows implements SwitchToWindows
    {
        WebDriver driver;

        @Override
        public void switchToWindow(String windowHandle) {
            driver.switchTo().window(windowHandle);
        }

        @Override
        public void switchAllWindows() {
            driver.getWindowHandles().forEach(window -> {
                PathUtils.applySleep(1);
                driver.switchTo().window(window);
            });
        }

        @Override
        public void switchToParticularWindow(String title) {

            driver.getWindowHandles().forEach(window -> {
                PathUtils.applySleep(1);
                driver.switchTo().window(window);

                if(driver.getTitle().equalsIgnoreCase(title) || driver.getTitle().contains(title))
                    return;
            });
        }

        @Override
        public void closeParticularTabOrWindow(String title) {

            AtomicBoolean windowFound = new AtomicBoolean(false);

            driver.getWindowHandles().forEach(window -> {
                PathUtils.applySleep(1);
                driver.switchTo().window(window);

                if(driver.getTitle().equalsIgnoreCase(title) || driver.getTitle().contains(title))
                {
                    driver.close();
                    windowFound.set(true);
                }

                if(windowFound.get())
                    return;
            });
        }
    }
}
