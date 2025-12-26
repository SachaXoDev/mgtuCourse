package academy;

import academy.model.WordDictionary;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

public class WordDictionaryTest {

    private static final Map<String, List<String>> TEST_WORDS = Map.of(
        "фрукты", List.of("ЯБЛОКО", "МАНДАРИН", "БАНАН", "КИВИ", "ВИНОГРАД"),
        "цвета", List.of("КРАСНЫЙ", "СИНИЙ", "ЗЕЛЕНЫЙ")
    );

    private static final Map<String, String> TEST_HINTS = Map.of(
        "яблоко", "Круглый фрукт.",
        "красный", "Цвет спелых ягод."
    );

    private final WordDictionary dictionary = new WordDictionary(TEST_WORDS, TEST_HINTS);

    @Test
    void getRandomWord_returnsNonEmptyWord() {
        String randomWord = dictionary.getRandomWord();
        assertNotNull(randomWord, "Случайное слово не должно быть null.");
        assertFalse(randomWord.isEmpty(), "Случайное слово не должно быть пустым.");
    }

    @Test
    void getRandomWord_fromSpecificCategory_returnsCorrectWord() {
        String category = "фрукты";
        String word = dictionary.getRandomWord(category);

        List<String> expectedWords = List.of("яблоко", "мандарин", "банан", "киви", "виноград");

        assertNotNull(word, "Слово не должно быть null для существующей категории.");
        assertFalse(word.isEmpty(), "Слово не должно быть пустым для существующей категории.");
        assertTrue(expectedWords.contains(word.toLowerCase()),
            "Слово '" + word + "' должно быть из категории '" + category + "' после приведения к нижнему регистру.");
    }

    @Test
    void getRandomWord_returnsRandomSelection() {
        String category = "фрукты";
        int runs = 50;
        java.util.Set<String> uniqueWords = new java.util.HashSet<>();

        for (int i = 0; i < runs; i++) {
            uniqueWords.add(dictionary.getRandomWord(category).toLowerCase());
        }

        int minimumExpectedUnique = 3;
        assertTrue(uniqueWords.size() >= minimumExpectedUnique,
            "При многократном вызове ожидалось получить минимум " + minimumExpectedUnique +
                " уникальных слов, но получено только " + uniqueWords.size() + ". Проверьте логику случайного выбора.");
    }
}
