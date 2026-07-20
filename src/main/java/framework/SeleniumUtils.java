package framework;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    PerformClickActions clickActions;
    EnterData typeData;
    ElementUtils elementUtils;
    Reports reports;
    PageNavigations pageNavigations;
    public SeleniumUtils(WebDriver driver,ElementUtils elementUtils,Reports reports)
    {
        this.driver = driver;
        this.launchApplications=new CreatingWindowsAndTabs(driver);
        this.switchBetweenTabsOrWindows=new SwitchBetweenWindows(driver);
        this.clickActions=new ClickActions(driver,elementUtils,reports);
        this.typeData=new EnterDataActions(driver,elementUtils,reports);
        this.elementUtils=new ElementUtils(driver);
        this.reports=new Reports(driver);
        this.pageNavigations=new HandlePageNavigations(driver);
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

    public interface PageNavigations
    {
        void navigateTo(String url);
        void refreshPage();
        void navigateBack();
        void navigateForward();
    }

    public interface PerformClickActions
    {
        void performClick(WebElement element);
        void performClick(By locator);
    }

    public interface EnterData
    {
        void enterText(WebElement element, String value);
        void enterText(By locator, String value);
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

            driver.manage().window().maximize(); //Maximize the browser

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

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    static class ClickActions implements PerformClickActions
    {
        WebDriver driver;
        ElementUtils elementUtils;
        Reports reports;

        @Override
        public void performClick(By locator) {
            elementUtils.findElement(locator).click();
            reports.captureScreenshot();
        }

        @Override
        public void performClick(WebElement element) {
            element.click();
            reports.captureScreenshot();
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    static class EnterDataActions implements EnterData
    {
        WebDriver driver;
        ElementUtils elementUtils;
        Reports reports;

        @Override
        public void enterText(By locator, String value) {
            elementUtils.findElement(locator).sendKeys(value);
            reports.captureScreenshot();
        }

        @Override
        public void enterText(WebElement element, String value) {
            element.sendKeys(value);
            reports.captureScreenshot();
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    static class HandlePageNavigations implements PageNavigations
    {
        WebDriver driver;

        //Differences between driver.get() vs driver.navigate().to()
        //There is no difference at all as driver.navigate().to() also uses driver.get() internally

        @Override
        public void navigateTo(String url) {
            driver.navigate().to(url);
        }

        @Override
        public void refreshPage() {
            driver.navigate().refresh();
        }

        @Override
        public void navigateBack() {
            driver.navigate().back();
        }

        @Override
        public void navigateForward() {
            driver.navigate().forward();
        }
    }
}
