import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AmazonCrawlerDone {
    static List<String> getInfo = new ArrayList<>(); // Array to store all the data
    static int i = 5; // Hard coded category start

    public static void main(String[] args) throws InterruptedException {
        // Set the ChromeDriver path
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver.exe");

        // Create a new instance of the ChromeDriver
        WebDriver driver = new ChromeDriver();

        // Open amazon.com
        driver.get("https://www.amazon.com");

        // Maximize window
        driver.manage().window().maximize();

        // Since this is a new instance of the browser, Amazon might require us to fill out a captcha.
        // Even if we can bypass this with automation it goes against the Terms of Service of the website
        // and there's a chance we might be breaking the law. Usually in a test environment the captcha
        // is disabled for full automation, but in this case if the prompt shows, the script will
        // auto-select the input field, wait 7 second for you to type it in and will click the submit button
        // and continue with the automation process.
        if (driver.getPageSource().contains("Continue shopping")) {
            WebElement captchaElement = driver.findElement(By.id("captchacharacters"));
            captchaElement.click();
            Thread.sleep(7000); // Wait 7 seconds
            WebElement continueShoppingButton = driver.findElement(By.className("a-button-inner"));
            continueShoppingButton.click();
        }

        Thread.sleep(500);

        while (i < 27) {
            try {
                driver.get("https://www.amazon.com");
                Thread.sleep(2000);
                // Open the hamburger menu "All" on the left
                WebElement hamburgerMenu = driver.findElement(By.id("nav-hamburger-menu"));
                hamburgerMenu.click();
                Thread.sleep(1000);

                // Click on See All to expand all categories
                WebElement seeAllElement = driver.findElement(By.linkText("See all"));
                seeAllElement.click();
                Thread.sleep(1000);

                // Open the first category
                WebElement electronicsMenu = driver.findElement(By.cssSelector("[data-menu-id='" + i + "']"));
                electronicsMenu.click();
                Thread.sleep(1000);

                // Copy the URL and text from each subcategory
                for (int i1 = 2; ; i1++) {
                    WebElement link = driver.findElement(By.cssSelector("a[href$='_0_2_" + i + "_" + i1 + "']"));
                    String linkText = link.getText();
                    String linkUrl = link.getAttribute("href");
                    String textAndUrl = "URL= " + linkUrl + ", Page Title= " + linkText + ", Status= ";
                    getInfo.add(textAndUrl);
                }
            } catch (NoSuchElementException e) {
                // Break the loop after reaching the last subcategory
                i++; // Changes the category after finishing up its subcategories
               
            }
        }

        // Calls the URL extractor, webpage checker and text exporter
        checkLinksAndExportResults(driver);

        // Closes the browser
        driver.quit();
    }
    
    // Extracts the URL from the array
    public static void checkLinksAndExportResults(WebDriver driver) {
        List<String> checkedResults = new ArrayList<>();
        for (String info : getInfo) {
            int startIndex = info.indexOf("URL=") + 5; 
            int endIndex = info.indexOf(", Page Title");
            if (startIndex < 0 || endIndex < 0) {
                System.out.println("Error: Unable to extract URL from info string: " + info);
                continue; // Skip this info string if the URL extraction fails
            }
            String url = info.substring(startIndex, endIndex); // Extracts the URL from the string
            String result = checkLink(driver, url);
            info += result;
            checkedResults.add(info);
        }
        exportResults(checkedResults);
    }
    
    // Checks if the link opens and adds its status to the array
    public static String checkLink(WebDriver driver, String url) {
        try {
            System.out.println("Opening URL: " + url);
            driver.get(url);
            Thread.sleep(1000);
            System.out.println("Status: OK");
            return "OK";
        } catch (Exception e) {
            System.out.println("Status: Dead link");
            e.printStackTrace();
            return "Dead link";
        }
    }

    
    // Exports the data array as a text document
	public static void exportResults(List<String> results) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String formattedTimestamp = timestamp.toString().replace(" ", "_").replace(":", "-");
        String fileName = formattedTimestamp + "_results.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String result : results) {
                writer.write(result);
                writer.newLine();
            }
            System.out.println("Results exported to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}