package july17th2026;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class UnderstandingConceptOfLinkTextAndPartialLinkText extends BaseClass {

    @Test(description = "Link Text And Partial Link Text",priority = 1)
    public void linkTextAndPartialLinkText(){
        seleniumUtils.launchApplications().launchApplication("https://www.cricbuzz.com");

        //If a text is surrounded between the anchor tags, then that particular text is referred to as LinkText
        //<a title="Live Cricket Score" href="/cricket-match/live-scores">Live Scores</a>

        //Syntax of finding an element on the basis of linkText:
        //driver.findElement(By.linkText(value));

        //driver.findElement(By.linkText("Live Scores"));

        seleniumUtils.clickActions().performClick(By.linkText("Live Scores"));

        seleniumUtils.clickActions().performClick(By.linkText("Matches By Day"));

    }
}
