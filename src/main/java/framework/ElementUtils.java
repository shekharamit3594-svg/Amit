package framework;

//Whole and sole purpose of this class is to manage the process of finding the web elements efficiently

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
public class ElementUtils {

    WebDriver driver;

    public WebElement findElement(By locator)
    {
        return driver.findElement(locator);
    }
}
