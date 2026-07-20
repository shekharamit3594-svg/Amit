package july20th2026;

import july17th2026.BaseClass;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class ContinuationOfLinkTextAndPartialLinkText extends BaseClass {

    @Test(description = "Link Text And Partial Link Text", priority = 1)
    public void linkTextAndPartialLinkText() {
        seleniumUtils.launchApplications().launchApplication(propertiesUtil.getURL());

        seleniumUtils.clickActions().performClick(By.linkText("MATCHES"));
        seleniumUtils.clickActions().performClick(By.linkText("Teams"));

        seleniumUtils.clickActions().performClick(By.linkText("News"));

        //Whenever the size of the linktext is huge and we cannot use such huge texts, then we come up with the concept
        //of partial link text

        //Syntax of finding the element on the basis of partial link text:
        //driver.findElement(By.partialLinkText(value));

        seleniumUtils.clickActions().performClick(By.partialLinkText("Ruturaj Gaikwad to captain West Zone in"));
        seleniumUtils.pageNavigations().navigateBack();

        seleniumUtils.clickActions().performClick(By.partialLinkText("Ashutosh Sharma to play for Hampshire in One-Day Cup"));
    }
}
