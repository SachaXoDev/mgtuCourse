package academy.model;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Locale;
import java.util.ArrayList;

public class WordDictionary {
    private final Map<String, List<String>> words;
    private final Map<String, String> hints;
    private final Random random = new Random();

    public WordDictionary(Map<String, List<String>> words, Map<String, String> hints) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Словарь слов пуст или не удалось загрузить.");
        }
        this.words = words;
        this.hints = (hints != null) ? hints : Map.of();
    }

    public String getRandomWord() {
        List<String> categories = new ArrayList<>(words.keySet());
        String randomCategory = categories.get(random.nextInt(categories.size()));

        List<String> categoryWords = words.get(randomCategory);
        return categoryWords.get(random.nextInt(categoryWords.size()));
    }

    public String getRandomWord(String category) {
        List<String> categoryWords = words.get(category.toLowerCase(Locale.ROOT));
        if (categoryWords != null && !categoryWords.isEmpty()) {
            return categoryWords.get(random.nextInt(categoryWords.size()));
        }
        return getRandomWord();
    }

    public List<String> getAllCategories() {
        return new ArrayList<>(words.keySet());
    }

    public String getCategoriesByIndex (int index) {
        List<String> categories = getAllCategories();
        if (index >= 0 && index < categories.size()) {
            return categories.get(index);
        }
        return null;
    }

    public String getHint (String word) {
        return hints.get(word.toLowerCase(Locale.ROOT));
    }

    public Boolean isValidCategory(String category) {
        return words.containsKey(category.toLowerCase(Locale.ROOT));
    }
}
