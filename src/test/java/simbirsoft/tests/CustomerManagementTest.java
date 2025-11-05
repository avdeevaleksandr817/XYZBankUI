package simbirsoft.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import simbirsoft.utils.DataGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для управления клиентами.
 */
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

        // Chain of invocation
        managerPage.openAddCustomer()
                .fillCustomerForm(generatedFirstName, LAST_NAME, generatedPostCode)
                .submitCustomer();

        // Удаление и проверка
        managerPage.goToCustomers();
        customersPage.clearSearchField()
                .deleteCustomerByName(generatedFirstName)
                .verifyCustomerDeleted(generatedFirstName);

        System.out.println("✅ Тестовый клиент добавлен и удалён: " + generatedFirstName);
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

        String randomName = allNames.get(new java.util.Random().nextInt(allNames.size()));
        System.out.println("Выбрано имя для поиска: " + randomName);

        // Вынесено в метод
        customersPage.searchCustomerByTyping(randomName);

        List<String> filteredNames = customersPage.getCustomerNames();
        System.out.println("Найдено: " + filteredNames);

        assertTrue(filteredNames.stream().allMatch(name -> name.equals(randomName)),
                "Все клиенты должны быть '" + randomName + "', но найдено: " + filteredNames);
    }

    @Test
    @DisplayName("03 - Удаление клиента с именем, ближайшим к средней длине")
    @Story("Удаление клиента")
    @Description("TC-03: Удаление клиента по близости длины имени к среднему арифметическому")
    void shouldDeleteCustomerClosestToAverageLength() {
        managerPage.goToCustomers();
        customersPage.clearSearchField();

        // Логика вынесена в CustomersPage
        String nameToDelete = customersPage.findCustomerClosestToAverageLength();
        System.out.println("Удаляем клиента: " + nameToDelete + " (длина: " + nameToDelete.length() + ")");

        customersPage.deleteCustomerByName(nameToDelete)
                .verifyCustomerDeleted(nameToDelete);
    }
}