

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebHandler {

    static WebDriver driver;
    static String emphasis;
    static String headline;
    static Date date;
    static Time time;
    static String author;
    static String place;
    static int readTime;
    static int pages;
    static String text;
    static boolean comments;
    static String source;
    static List<String> themes;
    static List<String> ressorts;
    static String link;

    public static void runPgm() {

        // Webdriver
        System.setProperty("webdriver.chrome.driver",  "res/chromedriver.exe");
        ChromeOptions co = new ChromeOptions();
        co.setHeadless(true);
        driver = new ChromeDriver(co);

        // Navigating
        driver.get("https://www.faz.net/faz-live");
        clickPopupFrame();
        WebElement ticker = driver.findElement(By.id("FAZContentLeftInner"));
        link = ticker.findElement(By.tagName("a")).getAttribute("href");

        driver.get(link);
        clickPopupFrame();
        System.out.println("Looking at " + link);

        ressorts = new ArrayList<>();
        if (link.indexOf("agenturmeldungen") != -1) {
            ressorts.add("agenturmeldungen");
        } else {
            String linkParts = link.substring(28, link.lastIndexOf("/"));
            String[] linkSplitts = linkParts.split("/");
            for (String ls : linkSplitts) {
                ressorts.add(ls);
            }
        }

        // Header
        emphasis = "";
        if (driver.findElements(By.className("atc-HeadlineEmphasisText")).size() != 0) {
            emphasis = driver.findElement(By.className("atc-HeadlineEmphasisText")).getText();
        }

        if (driver.findElements(By.className("atc-HeadlineText")).size() <= 0) return;
        headline = driver.findElement(By.className("atc-HeadlineText")).getText();

        // Date and Time
        String timestamp = driver.findElement(By.className("atc-MetaTime")).getAttribute("datetime");
        date = Date.valueOf(timestamp.substring(0, 10));
        time = Time.valueOf(timestamp.substring(11, 19));

        // readtime
        String rawReadTime = driver.findElement(By.className("atc-ReadTime_Text")).getText();
        readTime = Integer.parseInt(rawReadTime.substring(0, rawReadTime.length() - 5));

        // Author and Place
        author = "";
        place = "";

        if (driver.findElements(By.className("atc-MetaAuthorLink")).size() != 0) {
            String authorAndPlace = driver.findElement(By.className("atc-MetaAuthorLink")).getText();
            int splitter = authorAndPlace.indexOf(",");
            // Only Author or with plade
            if (splitter > 0) {
                author = authorAndPlace.substring(0, splitter);
                place = authorAndPlace.substring(splitter+1);
            } else {
                author = authorAndPlace;
            }
        }

        // Text
        text = "";
        pages = 1;

        if (driver.findElements(By.className("atc-Intro")).size() != 0) {
            text = driver.findElement(By.className("atc-Intro")).getText();
        }

        // multiple pages
        if (driver.findElements(By.className("nvg-Paginator_Item-page-number")).size() != 0) {
            pages = driver.findElements(By.className("nvg-Paginator_Item-page-number")).size();
        }

        for (int j = 1; j <= pages; j++) {
            // multiple paragraphs
            if (driver.findElements(By.className("atc-TextParagraph")).size() != 0) {
                var paras = driver.findElements(By.className("atc-TextParagraph"));
                int paraSize = paras.size();
                for (int i = 0; i < paras.size(); i++) {
                    text += "\n" + paras.get(i).getText();
                }
            }

            if (j == pages) break;

            if (driver.findElements(By.className("nvg-Paginator_Item-page-number")).size() != 0) {
                driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
                driver.findElements(By.className("nvg-Paginator_Item-page-number")).get(j).click();
            }
        }

        // Source
        if (driver.findElements(By.className("atc-Footer_Quelle")).size() != 0) {
            String rawSource = driver.findElement(By.className("atc-Footer_Quelle")).getText();
            if (rawSource.length() > 8) {
                source = rawSource.substring(8);
            }
        }

        // Comments
        comments = false;
        if (driver.findElements(By.className("atc-ContainerSocialMedia")).size() != 0) {
            WebElement pageFnc = driver.findElement(By.className("atc-ContainerSocialMedia"));
            if (pageFnc.findElements(By.className("ico-Base_Comment")).size() != 0) {
                comments = true;
            }
        }

        // Themes
        themes = new ArrayList<>();
        if (driver.findElements(By.className("lst-LinksTopics_TopicsListItem")).size() != 0) {
            List<WebElement> rawThemes = driver.findElements(By.className("lst-LinksTopics_TopicsListItem"));
            for (int i = 0; i < rawThemes.size()-1; i++) {
                themes.add(rawThemes.get(i).getText());
            }
        }

        driver.quit();
    }

    // clicking popup
    public static void clickPopupFrame() {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        // Check if it even appears
        if (driver.findElements(By.id("sp_message_container_545340")).size() != 0) {
            driver.switchTo().frame("sp_message_iframe_545340");
            WebElement popupBtn = driver.findElement(By.xpath("//button[text()='EINVERSTANDEN']"));
            popupBtn.click();
            driver.switchTo().defaultContent();
        }
    }

}
