package simbirsoft.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import simbirsoft.pages.ManagerPage;
import simbirsoft.pages.CustomersPage;

/**
 * Базовый класс для тестов.
 * Настраивает WebDriver и предоставляет общие методы.
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ManagerPage managerPage;
    protected CustomersPage customersPage;
    protected static String BASE_URL;

    @BeforeAll
    static void setUp() {
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(BaseTest.class.getClassLoader().getResourceAsStream("application.properties"));
            BASE_URL = props.getProperty("base.url");
        } catch (Exception e) {
            BASE_URL = "https://www.globalsqa.com/angularJs-protractor/BankingProject/";
        }
    }

    @BeforeEach
    void setUpEach() {
        System.out.println("✅ Тест запущен в потоке: " + Thread.currentThread().getName() +
                ", PID: " + ProcessHandle.current().pid());

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        managerPage = new ManagerPage(driver);
        customersPage = new CustomersPage(driver);

        driver.get(BASE_URL);
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState")
                .equals("complete"));

        By managerLoginLocator = By.xpath("//button[contains(., 'Bank Manager Login')]");
        WebElement managerLoginBtn = wait.until(ExpectedConditions.elementToBeClickable(managerLoginLocator));
        managerLoginBtn.click();

        wait.until(d -> d.getCurrentUrl().contains("/manager"));
        System.out.println("✅ Кликнули 'Bank Manager Login', перешли на /manager");
    }

    @AfterEach
    void tearDownEach() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("✅ Браузер закрыт");
    }
}