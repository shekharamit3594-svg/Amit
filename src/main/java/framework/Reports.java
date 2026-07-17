package framework;

import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

@AllArgsConstructor
public class Reports {

    WebDriver driver;

    @SneakyThrows
    public Reports captureScreenshot(String... imgName)
    {
        File src=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        File dest=new File(PathUtils.getScreenshotPath(imgName));

        Files.copy(src,dest);
        return this;
    }
}
