package july17th2026;

import framework.BrowserUtils;
import framework.ElementUtils;
import framework.Reports;
import framework.SeleniumUtils;
import framework.constants.BrowserNames;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseClass {

    WebDriver driver;
    Reports reports;
    ElementUtils elementUtils;
    SeleniumUtils seleniumUtils;

    @BeforeClass
    public void setupOfFrameworkObjects(){
        driver=BrowserUtils.fetchDriver(BrowserNames.CHROME);
        reports=new Reports(driver);
        elementUtils=new ElementUtils(driver);
        seleniumUtils=new SeleniumUtils(driver,elementUtils,reports);
    }
}
