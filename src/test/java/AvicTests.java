import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.swing.*;
import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {

    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test
    public void checkThatURLContainsSearchWord() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iPhone 13");
        driver.findElement(xpath("//button[contains(@class, 'reset search-btn')]")).click();

        assertTrue(driver.getCurrentUrl().contains("query=iPhone"));
    }

    @Test
    public void checkThatNothingFound() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("xcxsdfsxczx");
        driver.findElement(xpath("//button[contains(@class, 'reset search-btn')]")).click();

        assertTrue(driver.findElement(xpath("//p[@class='col-xs-12']")).isDisplayed());
    }

    @Test
    public void checkRemoveAddedItemsFromCart() {
        driver.findElement(xpath("//li[@class='parent js_sidebar-item']//a[contains(@href,'elektronika')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'gotovyie-pk')]")).click();
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
        driver.findElements(xpath("//a[@class='prod-cart__buy']")).get(0).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60    ));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//div[@class='btns-cart-holder']/a[contains(@class,'btn--orange')]")).click();
        String actualItemsCountInCart = driver.findElement(
                xpath("//div[contains(@class, 'header-bottom__cart')]//div[contains(@class,'js_cart_count')]")).getText();
        assertEquals(actualItemsCountInCart, "1");
        driver.findElement(xpath("//div[contains(@class,'header-bottom__cart')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//div[@class='item']/i[@class='icon icon-close js-btn-close']")).click();
        driver.navigate().refresh();
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                webDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
        actualItemsCountInCart = driver.findElement(
                xpath("//div[contains(@class, 'header-bottom__cart')]//div[contains(@class,'js_cart_count')]")).getText();
        assertEquals(actualItemsCountInCart, "0");
    }

    @Test
    public void checkProductsFiltering() {
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(xpath("//li[@class='parent js_sidebar-item']/a[contains(@href, 'apple-store')]")))
                .pause(Duration.ofSeconds(1))
                .moveToElement(driver.findElement(xpath("//li[@class='parent js_sidebar-item']/a[contains(@href,'iphone')]")))
                .pause(Duration.ofSeconds(1))
                .moveToElement(driver.findElement(xpath("//li[@class='single-hover-block']/a[contains(@href,'promax')]")))
                .click().build().perform();


        WebElement showMoreBtn = driver.findElement(xpath("//a[@class='btn-see-more js_show_more']"));

        if(showMoreBtn.isDisplayed()) {
            actions.moveToElement(showMoreBtn).click().pause(Duration.ofSeconds(3)).build().perform();
        }

        List<WebElement> productsList = driver.findElements(xpath("//div[@class='item-prod col-lg-3']"));

        boolean isAllTextContainsKeyWord = true;

        for (WebElement e: productsList) {
            isAllTextContainsKeyWord = e.getText().contains("Pro Max");
        }

        assertTrue(isAllTextContainsKeyWord);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
