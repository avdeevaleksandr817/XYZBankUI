package simbirsoft.utils;

import java.util.Random;

/**
 * Утилита для генерации тестовых данных.
 */
public class DataGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Генерирует 10-значный Post Code.
     * @return строка из 10 цифр
     */
    public static String generatePostCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Генерирует имя из 5 букв на основе Post Code.
     * Post Code делится на 5 пар по 2 цифры.
     * Каждая пара → число → % 26 → буква (a=0, b=1, ..., z=25)
     * Пример: 0001252667 → 00,01,25,26,67 → a,b,z,a,p → abzap
     */
    public static String generateFirstNameFromPostCode(String postCode) {
        if (postCode == null || postCode.length() != 10) {
            throw new IllegalArgumentException("Post Code должен быть 10 цифр");
        }

        StringBuilder name = new StringBuilder();
        for (int i = 0; i < 10; i += 2) {
            String pair = postCode.substring(i, i + 2);
            int num = Integer.parseInt(pair);
            char letter = (char) ('a' + (num % 26)); // 0=a, 26=a, 52=a
            name.append(letter);
        }
        return name.toString();
    }
}