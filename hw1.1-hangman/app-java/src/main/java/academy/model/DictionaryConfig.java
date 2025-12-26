package academy.model;

import java.util.List;
import java.util.Map;

public class DictionaryConfig {

    private final Map<String, List<String>> words;
    private final Map<String, String> hints;

    public DictionaryConfig(Map<String, List<String>> words, Map<String, String> hints) {
        this.words = words;
        this.hints = hints;
    }

    public Map<String, List<String>> getWords() {
        return words;
    }

    public Map<String, String> getHints() {
        return hints;
    }
}
