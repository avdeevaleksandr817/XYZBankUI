package simbirsoft.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import simbirsoft.utils.DataGenerator;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Epic("Управление клиентами")
@Feature("Работа с клиентами")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class CustomerManagementTest extends BaseTest {

    private static String generatedPostCode;
    private static String generatedFirstName;
    private static final String LAST_NAME = "Smith";

    @Test
    @DisplayName("01 - Добавление клиента с сгенерированными данными")
    @Story("Добавление клиента")
    @Description("TC-01: Создание клиента с генерацией Post Code и First Name")
    void shouldAddCustomerWithGeneratedData() {
        generatedPostCode = DataGenerator.generatePostCode();
        generatedFirstName = DataGenerator.generateFirstNameFromPostCode(generatedPostCode);

        System.out.println("Post Code: " + generatedPostCode);
        System.out.println("First Name: " + generatedFirstName);

        managerPage.openAddCustomer();
        managerPage.fillCustomerForm(generatedFirstName, LAST_NAME, generatedPostCode);
        managerPage.submitCustomer();

        // ✅ Переходим к списку и удаляем сразу
        managerPage.goToCustomers();
        customersPage.clearSearchField();
        customersPage.deleteCustomerByName(generatedFirstName);
        customersPage.verifyCustomerDeleted(generatedFirstName);

        System.out.println("✅ Тестовый клиент добавлен и удалён: " + generatedFirstName);

        // ✅ Сбрасываем, чтобы другие тесты не удалили
        generatedFirstName = null;
    }

@Test
@DisplayName("02 - Фильтрация клиентов через поиск по буквам")
@Story("Поиск клиентов")
@Description("TC-02: Ввод имени по буквам в поле Search Customer и проверка фильтрации")
void shouldFilterCustomersByNameWithTyping() {
    managerPage.goToCustomers();
    customersPage.clearSearchField();

    List<String> allNames = customersPage.getCustomerNames();
    System.out.println("Клиенты в списке: " + allNames);

    // Выбираем случайное имя
    String randomName = allNames.get(new Random().nextInt(allNames.size()));
    System.out.println("Выбрано имя для поиска: " + randomName);

    // Вводим по буквам
    for (int i = 1; i <= randomName.length(); i++) {
        String partialName = randomName.substring(0, i);
        customersPage.searchCustomerByName(partialName);
        System.out.println("🔎 Ввод: '" + partialName + "'");

        // Ждём обновления таблицы
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tbody/tr")));
    }

    // Проверяем результат
    List<String> filteredNames = customersPage.getFilteredCustomerNames();
    System.out.println("Найдено: " + filteredNames);

    // Все оставшиеся клиенты должны иметь выбранное имя
    assertTrue(filteredNames.stream().allMatch(name -> name.equals(randomName)),
            "Все клиенты в списке должны быть '" + randomName + "', но найдено: " + filteredNames);
}
    @Test
    @DisplayName("03 - Удаление клиента с именем, ближайшим к средней длине")
    @Story("Удаление клиента")
    @Description("TC-03: Удаление клиента по близости длины имени к среднему арифметическому")
    void shouldDeleteCustomerClosestToAverageLength() {
        managerPage.goToCustomers();
        customersPage.clearSearchField();

        List<String> names = customersPage.getCustomerNames();
        System.out.println("Клиенты в списке: " + names);

        // 1. Найти среднюю длину имён
        double averageLength = names.stream()
                .mapToInt(String::length)
                .average()
                .orElseThrow(() -> new IllegalStateException("Список клиентов пуст"));

        System.out.println("Средняя длина имён: " + String.format("%.2f", averageLength));

        // 2. Найти имя с длиной, ближайшей к средней
        String nameToDelete = names.stream()
                .min((a, b) -> {
                    int diffA = Math.abs(a.length() - (int) averageLength);
                    int diffB = Math.abs(b.length() - (int) averageLength);
                    return Integer.compare(diffA, diffB);
                })
                .orElseThrow(() -> new IllegalStateException("Не удалось выбрать клиента для удаления"));

        System.out.println("Удаляем клиента: " + nameToDelete + " (длина: " + nameToDelete.length() + ")");

        // 3. Удаляем
        customersPage.deleteCustomerByName(nameToDelete);

        // 4. Проверяем
        customersPage.verifyCustomerDeleted(nameToDelete);
    }
}