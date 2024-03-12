import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonCartDone {
    public static void main(String[] args) throws InterruptedException {
        // Set the ChromeDriver path
    	System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver.exe");

        // Create a new instance of the ChromeDriver
        WebDriver driver = new ChromeDriver();

         // Navigate to Amazon.com
        driver.get("https://www.amazon.com");
        
        // Maximize window
        driver.manage().window().maximize();
        
        // Since this is a new instance of the browser Amazon might require us to fill out a captcha.
        // Even if we can bypass this with automation it goes against Terms of Service of the website
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
                
        // Creating an empty array to avoid adding the same item over and over in the cart. Note that even if it does
        // the final test will still work.
        Set<String> noCartDuplicates = new HashSet<>();
        
        Thread.sleep(2000);
        
        // Locate the search field, type in "laptop" and click the search button
        WebElement searchField = driver.findElement(By.id("twotabsearchtextbox"));
        searchField.sendKeys("laptop");
        searchField.submit();

        // Fetch all the product links from the first page of the search result
        List<WebElement> productLinks = driver.findElements(By.xpath("//a[contains(@class, 'a-link-normal') and contains(@href, '/dp/')]"));

        // Store unique product URLs from the search in a set
        Set<String> productsUrlsFromSearch = new HashSet<>();
        for (WebElement productLink : productLinks) {
            productsUrlsFromSearch.add(productLink.getAttribute("href"));
        }

        // Avoid duplicate URL's. The script might fetch a different URL for the same product, but that
        // will be filtered out as well later on
        List<String> productsFromSearch = new ArrayList<>();
        for (String productUrl : productsUrlsFromSearch) {
            // Open the product URL
            driver.get(productUrl);

            // Since all of the products that come from the first page of the Amazon search there
            // wont be a need to check for availability, but this method will filter out discounted products
            if (isConditionMet(driver)) {
                // Extract the unique product ID for the final test check
                String productIdentifier = extractStringBetween(productUrl, "/dp/", "/ref");

                // Store the product ID
                productsFromSearch.add(productIdentifier);
                
                // Adding products that are not in the cart to avoid adding them multiple times
                WebElement productTitleElement = driver.findElement(By.xpath("//span[@id='productTitle']"));
                String productTitle = productTitleElement.getText();
                if (!noCartDuplicates.contains(productTitle)) {
                    noCartDuplicates.add(productTitle);
                    driver.findElement(By.id("add-to-cart-button")).click();
                    // Waiting for the cart to update
                    Thread.sleep(2000);
                }
            }
        }

        // Opening the Amazon cart
        driver.get("https://www.amazon.com/gp/cart/view.html");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Extracting the unique product ID's from the cart for the final test while insuring
        // no duplicates
        List<String> productsFromCart = new ArrayList<>();
        List<WebElement> productsInCart = driver.findElements(By.xpath("//a[contains(@class, 'sc-product-link')]"));
        for (WebElement productInCart : productsInCart) {
            String productUrl = productInCart.getAttribute("href");
            String productIdentifier = extractStringBetween(productUrl, "/product/", "/ref");
            productsFromCart.add(productIdentifier);
        }

        // Remove productsFromSearch
        Set<String> uniqueProductsFromSearch = new HashSet<>(productsFromSearch);
        productsFromSearch.clear();
        productsFromSearch.addAll(uniqueProductsFromSearch);
        
        // Remove duplicates from productsFromCart
        Set<String> uniqueProductsFromCart = new HashSet<>(productsFromCart);
        productsFromCart.clear();
        productsFromCart.addAll(uniqueProductsFromCart);

        // Printing the products ID's from the first page of the search that
        // meet the criteria with all the product ID's from the cart to check if the
        // final test actually works
        System.out.println("Products from search:");
        System.out.println(productsFromSearch);
        System.out.println("Products from cart:");
        System.out.println(productsFromCart);

        // Check if the product ID's match and print out the result
        if (productsFromSearch.containsAll(productsFromCart) && productsFromCart.containsAll(productsFromSearch)) {
            System.out.println("Actual test passed");
        } else {
            System.out.println("Actual test failed");
        }
        
        // Changes a random string from productsFromCart to "NEGATIVE00" for the negative test
        int numOfStrings = productsFromCart.size();
        if (numOfStrings > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(numOfStrings);
            productsFromCart.set(randomIndex, "NEGATIVE00");
        } else {
            System.out.println("Array is empty.");
        }
        
        // Check if the product ID's match and print out the result
        if (productsFromSearch.containsAll(productsFromCart) && productsFromCart.containsAll(productsFromSearch)) {
            System.out.println("Negative test failed");
        } else {
            System.out.println("Negative test passed");
        }
        
        Thread.sleep(3000);
        
        // This is a fun outro of the whole operation. It serves no purpose and it exists only because this is a test
        driver.get("https://media.npr.org/assets/img/2023/05/26/honest-work-meme_wide-4029ac991ab09630c53950e7236a45ab07dc9b9d-s1400-c100.jpg");
        
        Thread.sleep(2000);

        // Close the browser
        driver.quit();
    }

 // The method that checks if a product is discounted or doesn't ship to our location
    public static boolean isConditionMet(WebDriver driver) {
        if (driver.getPageSource().contains("Typical price") ||
            driver.getPageSource().contains("This item cannot be shipped to your selected delivery location.")) {
            return false;
        }

        try {
            WebElement listPriceElement = driver.findElement(By.xpath("//span[@class='a-size-small aok-offscreen' and contains(text(), 'List Price')]"));
            if (listPriceElement != null) {
                return false;
            }
        } catch (NoSuchElementException e) {
            
        }

        return true;
    }

    // The method that extracts the product ID
    public static String extractStringBetween(String input, String start, String end) {
        String pattern = start + "(.*?)" + end;
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }
}