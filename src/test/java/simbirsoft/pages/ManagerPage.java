package simbirsoft.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class ManagerPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ManagerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }


    @Step("Открытие формы добавления клиента")
    public void openAddCustomer() {
        By addCustomerLocator = By.xpath("//button[contains(., 'Add Customer')]");
        WebElement addCustomerBtn = wait.until(
                ExpectedConditions.elementToBeClickable(addCustomerLocator)
        );
        addCustomerBtn.click();
    }

    @Step("Заполнение формы: {firstName}, {lastName}, {postCode}")
    public void fillCustomerForm(String firstName, String lastName, String postCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@ng-model='fName']")))
                .sendKeys(firstName);
        driver.findElement(By.xpath("//input[@ng-model='lName']")).sendKeys(lastName);
        driver.findElement(By.xpath("//input[@ng-model='postCd']")).sendKeys(postCode);
    }

    @Step("Подтверждение добавления клиента")
    public void submitCustomer() {
        By submitBtn = By.xpath("//button[@type='submit' and contains(text(), 'Add Customer')]");
        wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    @Step("Переход к списку клиентов")
    public void goToCustomers() {
        // Кликаем "Customers"
        By customersBtn = By.xpath("//button[contains(., 'Customers')]");
        wait.until(ExpectedConditions.elementToBeClickable(customersBtn)).click();

        // Ждём, что таблица загрузилась (хотя бы одна строка)
        By tableRow = By.xpath("//table//tbody/tr");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(tableRow, 0));
    }
}