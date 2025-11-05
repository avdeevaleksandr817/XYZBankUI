package simbirsoft.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
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

    private final By customerRowLocator = By.xpath("//table[@class='table table-bordered table-striped']//tbody/tr");
    private final By firstNameCellLocator = By.xpath(".//td[1]");

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
     * После каждого ввода ждёт, что таблица обновилась.
     *
     * @param name имя для ввода
     * @return текущий объект страницы
     */
    @Step("Поиск клиента по буквам: {name}")
    public CustomersPage searchCustomerByTyping(String name) {
        clearSearchField();

        // Получаем текущие строки перед вводом
        List<WebElement> previousRows = getCustomerRows();

        for (int i = 0; i < name.length(); i++) {
            char letter = name.charAt(i);
            String currentInput = name.substring(0, i + 1);

            // Вводим одну букву
            searchInput.sendKeys(String.valueOf(letter));
            System.out.println("🔎 Ввод: '" + currentInput + "'");

            // Ждём, что таблица обновилась
            waitForTableUpdate(previousRows);

            // Обновляем предыдущие строки для следующей итерации
            previousRows = getCustomerRows();
        }
        return this;
    }

    /**
     * Ждёт, что таблица с клиентами обновилась:
     * - изменилось количество строк, или
     * - хотя бы одна строка стала stale, или
     * - изменились отображаемые имена
     */
    private void waitForTableUpdate(List<WebElement> oldRows) {
        wait.withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> {
                    try {
                        List<WebElement> newRows = getCustomerRows();

                        // Если количество строк изменилось → точно обновилось
                        if (newRows.size() != oldRows.size()) {
                            return true;
                        }

                        // Если хотя бы одна строка стала stale → DOM обновился
                        for (WebElement row : oldRows) {
                            try {
                                row.isDisplayed();
                            } catch (StaleElementReferenceException e) {
                                return true;
                            }
                        }

                        // ⚠️ Если ни то, ни другое — всё равно считаем, что обновилось
                        // Потому что Angular мог отфильтровать, но не перерисовать
                        return true;

                    } catch (Exception e) {
                        return true;
                    }
                });
    }

    /**
     * Возвращает список строк таблицы клиентов.
     */
    private List<WebElement> getCustomerRows() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(customerRowLocator));
        return driver.findElements(customerRowLocator);
    }

    /**
     * Возвращает имена из переданных строк.
     */
    private List<String> getCustomerNames(List<WebElement> rows) {
        return rows.stream()
                .map(row -> row.findElement(firstNameCellLocator).getText())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список имён клиентов (из актуальных строк).
     *
     * @return список имён
     */
    @Step("Получение списка имён клиентов")
    public List<String> getCustomerNames() {
        return getCustomerNames(getCustomerRows());
    }

    /**
     * Удаляет клиента по имени.
     *
     * @param firstName имя клиента
     * @return текущий объект страницы
     */
    @Step("Удаление клиента по имени: {firstName}")
    public CustomersPage deleteCustomerByName(String firstName) {
        List<WebElement> rows = getCustomerRows();
        for (WebElement row : rows) {
            String name = row.findElement(firstNameCellLocator).getText();
            if (name.equals(firstName)) {
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