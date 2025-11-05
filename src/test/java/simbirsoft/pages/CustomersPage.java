package simbirsoft.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Страница управления клиентами.
 * Предоставляет методы для поиска, удаления и фильтрации клиентов.
 */
public class CustomersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//input[@placeholder='Search Customer']")
    private WebElement searchInput;

    @FindBy(xpath = "//table[@class='table table-bordered table-striped']//tbody/tr/td[1]")
    private List<WebElement> firstNameCells;

    /**
     * Конструктор страницы.
     *
     * @param driver WebDriver
     */
    public CustomersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    /**
     * Очищает поле поиска клиентов.
     *
     * @return текущий объект страницы
     */
    @Step("Очистка поля поиска клиентов")
    public CustomersPage clearSearchField() {
        try {
            if (!searchInput.getAttribute("value").isEmpty()) {
                searchInput.clear();
                System.out.println("✅ Поле поиска очищено");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Не удалось очистить поле поиска: " + e.getMessage());
        }
        return this;
    }

    /**
     * Вводит имя по буквам для проверки динамической фильтрации.
     *
     * @param name имя для ввода
     * @return текущий объект страницы
     */
    @Step("Поиск клиента по буквам: {name}")
    public CustomersPage searchCustomerByTyping(String name) {
        clearSearchField();

        for (int i = 0; i < name.length(); i++) {
            String partialName = name.substring(0, i + 1);
            char letter = name.charAt(i);

            // Вводим букву
            searchInput.sendKeys(String.valueOf(letter));
            System.out.println("🔎 Ввод: '" + partialName + "'");

            // ЖДЁМ, что фильтрация применилась
            waitForFilteredNamesToStartWith(partialName);
        }
        return this;
    }

    /**
     * Ждёт, что все отображённые имена начинаются с префикса.
     * Автоматически ожидает появление строк.
     */
    private void waitForFilteredNamesToStartWith(String prefix) {
        wait.until(driver -> {
            try {
                List<String> names = getCustomerNames();
                return !names.isEmpty() &&
                        names.stream().allMatch(n ->
                                n.toLowerCase().startsWith(prefix.toLowerCase())
                        );
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Возвращает список имён клиентов.
     *
     * @return список имён
     */
    @Step("Получение списка имён клиентов")
    public List<String> getCustomerNames() {
        By nameLocator = By.xpath("//table[@class='table table-bordered table-striped']//tbody/tr/td[1]");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(nameLocator));
        return firstNameCells.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Удаляет клиента по имени.
     *
     * @param firstName имя клиента
     * @return текущий объект страницы
     */
    @Step("Удаление клиента по имени: {firstName}")
    public CustomersPage deleteCustomerByName(String firstName) {
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody/tr"));
        for (WebElement row : rows) {
            WebElement nameCell = row.findElement(By.xpath(".//td[1]"));
            if (nameCell.getText().equals(firstName)) {
                row.findElement(By.xpath(".//button[text()='Delete']")).click();
                break;
            }
        }
        return this;
    }

    /**
     * Проверяет, что клиент был удалён.
     *
     * @param firstName имя клиента
     * @return текущий объект страницы
     */
    @Step("Проверка отсутствия клиента '{firstName}'")
    public CustomersPage verifyCustomerDeleted(String firstName) {
        List<String> currentNames = getCustomerNames();
        assertFalse(currentNames.contains(firstName), "Клиент '" + firstName + "' должен быть удалён");
        return this;
    }

    /**
     * Находит имя клиента, длина которого ближе всего к средней длине всех имён.
     *
     * @return имя клиента
     */
    @Step("Поиск клиента, ближайшего к средней длине имени")
    public String findCustomerClosestToAverageLength() {
        List<String> names = getCustomerNames();
        if (names.isEmpty()) {
            throw new IllegalStateException("Список клиентов пуст");
        }

        double averageLength = names.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);

        System.out.println("Средняя длина имён: " + String.format("%.2f", averageLength));

        return names.stream()
                .min((a, b) -> {
                    int diffA = Math.abs(a.length() - (int) averageLength);
                    int diffB = Math.abs(b.length() - (int) averageLength);
                    return Integer.compare(diffA, diffB);
                })
                .orElseThrow(() -> new IllegalStateException("Не удалось выбрать клиента для удаления"));
    }
}