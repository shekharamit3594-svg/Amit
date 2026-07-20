package july17th2026;

import framework.*;
import framework.constants.BrowserNames;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;

import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseClass {

    WebDriver driver;
    Reports reports;
    ElementUtils elementUtils;
    SeleniumUtils seleniumUtils;
    PropertiesUtil propertiesUtil;

    @BeforeClass
    public void setupOfFrameworkObjects(){

        propertiesUtil = new PropertiesUtil();
        BrowserNames browserNames = Arrays.stream(BrowserNames.values()).filter(s-> s.getName().equalsIgnoreCase(propertiesUtil.getBrowser()))
                .findFirst().orElseThrow(() -> new GenericExceptions(propertiesUtil.getBrowser()+" not found"));

        driver=BrowserUtils.fetchDriver(browserNames);
        reports=new Reports(driver);
        elementUtils=new ElementUtils(driver);
        seleniumUtils=new SeleniumUtils(driver,elementUtils,reports);
    }
}
