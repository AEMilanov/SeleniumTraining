# Amazon Selenium and API Tests

This repository contains code for two separate tasks: web scraping Amazon for product URLs and titles, and testing an API endpoint for retrieving blog posts.

## Table of Contents

- [Amazon Crawler]
- [Amazon Add to cart checker] 
- [API Testing]
- [Usage]
- [Dependencies]



## Amazon Crawler

The `AmazonCrawlerDone` Java class is responsible for scraping product URLs and titles from Amazon's website. It navigates through different categories, extracts product information, and exports the results to a text file.

### Features
- Scrapes product URLs and titles from various categories on Amazon.
- Checks if the extracted URLs are valid and exports the results to a text file.

## Usage
To use the amazon scraping functionality, run the `AmazonCrawlerDone` class. Ensure that you have the Chrome WebDriver installed and set the `webdriver.chrome.driver` system property to the path of the WebDriver executable.



## Amazon Add to cart checker
The `AmazonCartDone` Java class is responsible for scraping laptops that are not discounted and ship to our location and adds them to the cart. After added it checks if everything made it in.

### Features
- Adds products from the first page of the Amazon product search.
- Checks if the added products are the same in the cart.

## Usage
To use the first page auto add and compare functionality, run the `AmazonCartDone` class. Ensure that you have the Chrome WebDriver installed and set the `webdriver.chrome.driver` system property to the path of the WebDriver executable.


## API Testing

The `UserPostsTests` Java class performs testing on a mock API endpoint (`https://jsonplaceholder.typicode.com/posts`) to verify the count of blog posts for different users and the uniqueness of post IDs.

### Features
- Tests the API endpoint to ensure each user has the correct number of blog posts.
- Validates that each blog post has a unique ID.

## Usage
To execute the API testing, run the `UserPostsTests` class. The tests will automatically connect to the API endpoint and verify the expected results.

## Dependencies and minimum versions

- Java v.21.0.2
- Selenium v.4.17.0
- Selenium WebDriverManager v.5.6.3
- Apache Maven v3.9.6
- JSON.org (for parsing JSON responses)
- LogBack v1.5.3
- JUnit Jupiter v5.8.0
- RestAssured v5.4.0

