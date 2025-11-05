# 🏦 XYZ Bank UI — Автоматизированные Тесты

![Java](https://img.shields.io/badge/Java-17-orange)
![Selenium](https://img.shields.io/badge/Selenium-WebDriver-brightgreen)
![JUnit 5](https://img.shields.io/badge/JUnit-5-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.14-brown)
![Allure](https://img.shields.io/badge/Allure-Report-violet)
![Chrome](https://img.shields.io/badge/Browser-Chrome-4285F4)

UI-автоматизация тестов для веб-приложения [XYZ Bank](https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login) с использованием **Selenium WebDriver**, **JUnit 5**, **Page Object Model** и **параллельного запуска тестов**.

Проект полностью **production-ready**: стабильные тесты, логирование, изоляция данных, красивые отчёты в Allure и поддержка расширения.

---

## ✅ Возможности

- ✅ **Добавление клиента**
    - Генерация `Post Code` (10 цифр) через `DataGenerator`
    - `First Name` из первых 5 букв, основанных на парах цифр
    - Автоматическая очистка после теста
- ✅ **Фильтрация клиентов по вводу**
    - Поиск клиента по частичному совпадению букв
    - Имитация посимвольного ввода с задержкой
    - Проверка динамического обновления списка
- ✅ **Удаление клиента**
    - Расчёт средней длины имён
    - Поиск имени, ближайшего к средней длине
    - Использование `Stream API` для чистоты и читаемости
- ✅ **Параллельный запуск**
    - Настроено через `maxParallelForks` и `junit.jupiter.execution.parallel`
    - Поддержка масштабирования при добавлении новых тестовых классов
- ✅ **Изоляция тестов**
    - `@AfterEach` удаляет только добавленного клиента
    - Нет зависимости между тестами
    - Каждый тест работает с уникальными данными

---

## 🧪 Реализованные тест-кейсы

| ID  | Описание | Метод |
|-----|--------|-------|
| TC-01 | Добавление клиента с сгенерированными данными | `shouldAddCustomerWithGeneratedData()` |
| TC-02 | Фильтрация клиентов по буквам (поисковой строке) | `shouldFilterCustomersByNameWithTyping()` |
| TC-03 | Удаление клиента, ближайшего к средней длине имён | `shouldRemoveCustomerClosestToAverageLength()` |

---

## 🛠 Технологии

| Технология | Версия | Назначение |
|----------|--------|-----------|
| Java | 17 | Язык программирования |
| Selenium WebDriver | 4.27.0 | Управление браузером |
| JUnit 5 | 5.10.3 | Запуск тестов, ассерты, параллельность |
| Gradle | 8.14 | Сборка, зависимости, управление задачами |
| Allure | 2.27.0 | Генерация красивых отчётов с шагами |
| WebDriverManager | 5.6.2 | Автоматическая загрузка драйверов |
| AspectJ Weaver | 1.9.20.1 | Поддержка `@Step` в Allure |
| Page Object Model | — | Архитектура: разделение страниц и тестов |

---

## ⚙️ Запуск проекта

### 1. Клонирование
bash git clone https://github.com/avdeevaleksandr817/XYZBankUI.git cd XYZBankUI
### 2. Запуск тестов
bash ./gradlew clean test
> ✅ Тесты запустятся в Chrome (в `headless`-режиме, если настроено)  
> ✅ Поддержка параллельного выполнения через `junit.jupiter.execution.parallel`

### 3. Просмотр отчёта Allure
bash ./gradlew allureServe
> 🚀 Откроется отчёт в браузере:  
> [http://localhost:63440](http://localhost:63440)

---

## 📊 Пример отчёта Allure

![Allure Report](https://i.imgur.com/9T5Lk2e.png)

Отчёт включает:
- Эпики, фичи, сценарии (через `@Epic`, `@Feature`)
- Аннотированные шаги (`@Step`)
- Логи выполнения
- Скриншоты (можно добавить при необходимости)

---

## 📁 Структура проекта
XYZBankUI/ ├── README.md ├── build.gradle ├── gradle.properties ├── .gitignore ├── src/ │ └── test/ │ ├── java/ │ │ └── simbirsoft/ │ │ ├── pages/ │ │ │ ├── BasePage.java │ │ │ ├── CustomersPage.java │ │ │ └── ManagerPage.java │ │ ├── tests/ │ │ │ └── CustomerManagementTest.java │ │ └── utils/ │ │ └── DataGenerator.java │ └── resources/ │ ├── junit-platform.properties │ └── application.properties └── gradle/ └── wrapper/ ├── gradle-wrapper.jar └── gradle-wrapper.properties


---

## 🧹 `.gitignore` — что игнорируется
gitignore
Build
build/ out/ target/ .gradle/ !.gradle/wrapper/gradle-wrapper.jar
IDE
.idea/ *.iws *.iml *.ipr .classpath .project .settings/ .vscode/
Logs
*.log logs/
Reports
allure-results/ allure-report/
OS
.DS_Store
Dependencies
chromedriver* chromedriver-*.zip

> 🔒 В репозиторий не попадают временные и бинарные файлы

---

## 🔧 Особенности реализации

### ✅ `DataGenerator.java`
java src/test/java/simbirsoft/utils/DataGenerator.java package simbirsoft.utils;
import java.util.Random;
/**


Утилита для генерации тестовых данных. */ public class DataGenerator { private static final Random RANDOM = new Random();
/**

Генерирует 10-значный Post Code.
@return строка из 10 цифр */ public static String generatePostCode() { StringBuilder sb = new StringBuilder(); for (int i = 0; i < 10; i++) { sb.append(RANDOM.nextInt(10)); } return sb.toString(); }
/**

Генерирует имя из 5 букв на основе Post Code.
@param postCode 10 цифр
@return 5-буквенное имя */ public static String generateFirstNameFromPostCode(String postCode) { StringBuilder name = new StringBuilder(); for (int i = 0; i < 10; i += 2) { String pair = postCode.substring(i, i + 2); int num = Integer.parseInt(pair); char letter = (char) ('a' + (num % 26)); name.append(letter); } return name.toString(); } }

### ✅ Фильтрация по вводу с имитацией пользователя
java for (char c : searchName.toCharArray()) { firstNameInput.sendKeys(String.valueOf(c)); Thread.sleep(100); // Имитация живого ввода }

### ✅ Удаление по средней длине
java double averageLength = names.stream().mapToInt(String::length).average().orElse(0); return names.stream() .min(Comparator.comparingDouble(s -> Math.abs(s.length() - averageLength))) .orElse(null);

### ✅ Параллельный запуск
gradle test { maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1 systemProperty 'junit.jupiter.execution.parallel.enabled', 'true' systemProperty 'junit.jupiter.execution.parallel.mode.default', 'concurrent' jvmArgs '-Dfile.encoding=UTF-8' }

### ✅ Стабильность
- `WebDriverWait` + `ExpectedConditions`
- Обработка `StaleElementReferenceException`
- JS-клик как резерв
- 
## 🤝 Автор

👤 **Александр Авдеев**  
📧 avdeev.alexandr.817@gmail.com  
🔗 [GitHub Profile](https://github.com/avdeevaleksandr817)


---









