package academy.service;

import academy.model.DictionaryConfig;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

public class StaticDictionaryLoader {

    public static DictionaryConfig loadDefaultDictionary() {
        Map<String, List<String>> words = Map.of(
            "фрукты", List.of("яблоко", "мандарин", "банан", "киви", "виноград"),
            "страны", List.of("россия", "америка", "болгария", "сербия", "турция"),
            "профессии", List.of("доктор", "инженер", "программист", "машинист", "переводчик")
        );
        Map<String, String> hints = Map.ofEntries(
            entry("яблоко", "Круглый фрукт, бывает зеленым или красным"),
            entry("мандарин", "Цитрусовый фрукт, символ Нового года"),
            entry("банан", "Желтый, изогнутый фрукт"),
            entry("киви", "Мохнатый фрукт с зеленой мякотью"),
            entry("виноград", "Используется для производства вина"),

            entry("россия", "Самая большая страна в мире"),
            entry("америка", "Страна, где изобрели джинсы"),
            entry("болгария", "Страна с кириллицей и теплым морем"),
            entry("сербия", "Столица — Белград"),
            entry("турция", "Страна, популярная среди туристов"),

            entry("доктор", "Лечит людей"),
            entry("инженер", "Проектирует машины и конструкции"),
            entry("программист", "Пишет код для программ"),
            entry("машинист", "Управляет поездом"),
            entry("переводчик", "Помогает людям говорить на разных языках")
        );

        return new DictionaryConfig(words, hints);
    }
}
