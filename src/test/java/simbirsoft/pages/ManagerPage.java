package simbirsoft.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Страница менеджера.
 * Предоставляет методы для добавления клиентов и перехода к списку.
 */
public class ManagerPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ManagerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    /**
     * Открывает форму добавления клиента.
     *
     * @return текущий объект страницы
     */
    @Step("Открытие формы добавления клиента")
    public ManagerPage openAddCustomer() {
        By addCustomerLocator = By.xpath("//button[contains(., 'Add Customer')]");
        WebElement addCustomerBtn = wait.until(
                ExpectedConditions.elementToBeClickable(addCustomerLocator)
        );
        addCustomerBtn.click();
        return this;
    }

    /**
     * Заполняет форму клиента.
     *
     * @param firstName имя
     * @param lastName фамилия
     * @param postCode почтовый индекс
     * @return текущий объект страницы
     */
    @Step("Заполнение формы: {firstName}, {lastName}, {postCode}")
    public ManagerPage fillCustomerForm(String firstName, String lastName, String postCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@ng-model='fName']")))
                .sendKeys(firstName);
        driver.findElement(By.xpath("//input[@ng-model='lName']")).sendKeys(lastName);
        driver.findElement(By.xpath("//input[@ng-model='postCd']")).sendKeys(postCode);
        return this;
    }

    /**
     * Подтверждает добавление клиента.
     *
     * @return текущий объект страницы
     */
    @Step("Подтверждение добавления клиента")
    public ManagerPage submitCustomer() {
        By submitBtn = By.xpath("//button[@type='submit' and contains(text(), 'Add Customer')]");
        wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        return this;
    }

    /**
     * Переходит к списку клиентов.
     *
     * @return текущий объект страницы
     */
    @Step("Переход к списку клиентов")
    public ManagerPage goToCustomers() {
        By customersBtn = By.xpath("//button[contains(., 'Customers')]");
        wait.until(ExpectedConditions.elementToBeClickable(customersBtn)).click();

        By tableRow = By.xpath("//table//tbody/tr");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(tableRow, 0));
        return this;
    }
}