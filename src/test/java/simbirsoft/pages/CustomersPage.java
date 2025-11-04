package simbirsoft.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)

public class CustomersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @Step("Очистка поля поиска клиентов")
    public void clearSearchField() {
        By searchInput = By.xpath("//input[@placeholder='Search Customer']");
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
            if (!input.getAttribute("value").isEmpty()) {
                input.clear();
                System.out.println("✅ Поле поиска очищено");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Не удалось очистить поле поиска: " + e.getMessage());
        }
    }

    public CustomersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Step("Получение списка имён")
    public List<String> getCustomerNames() {
        By nameCells = By.xpath("//table[@class='table table-bordered table-striped']//tbody/tr/td[1]");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(nameCells));
        return driver.findElements(nameCells).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }


@Step("Получение списка имён после фильтрации")
public List<String> getFilteredCustomerNames() {
    By nameCells = By.xpath("//table[@class='table table-bordered table-striped']//tbody/tr/td[1]");
    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(nameCells));
    return driver.findElements(nameCells).stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
}

    @Step("Поиск клиента по имени: {name}")
    public CustomersPage searchCustomerByName(String name) {
        By searchInput = By.xpath("//input[@placeholder='Search Customer']");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(name);
        return this;
    }
    @Step("Удаление клиента по имени: {firstName}")
    public void deleteCustomerByName(String firstName) {
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody/tr"));
        for (WebElement row : rows) {
            WebElement nameCell = row.findElement(By.xpath(".//td[1]"));
            if (nameCell.getText().equals(firstName)) {
                row.findElement(By.xpath(".//button[text()='Delete']")).click();
                break;
            }
        }
    }

    @Step("Проверка отсутствия клиента '{firstName}'")
    public void verifyCustomerDeleted(String firstName) {
        List<String> names = getCustomerNames();
        assertFalse(names.contains(firstName), "Клиент должен быть удалён");
    }
}