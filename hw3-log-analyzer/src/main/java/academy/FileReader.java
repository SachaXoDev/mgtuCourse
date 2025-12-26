package academy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    public List<NginxLogRecord> readFiles(List<String> paths) throws IOException {
        List<NginxLogRecord> allRecords = new ArrayList<>();

        for (String path : paths) {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                allRecords.addAll(readFromUrl(path));
            } else {
                allRecords.addAll(readFromLocalFile(path));
            }
        }

        return allRecords;
    }

    private List<NginxLogRecord> readFromLocalFile(String filePath) throws IOException {
        List<NginxLogRecord> records = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            System.err.println("⚠️ Файл не найден: " + filePath);
            return records;
        }

        LogParser parser = new LogParser();
        try (var lines = Files.lines(path)) {
            lines.forEach(line -> {
                try {
                    NginxLogRecord record = parser.parseLine(line);
                    records.add(record);
                } catch (LogParseException e) {
                    System.err.println("⚠️ Пропущена некорректная строка: " + e.getMessage());
                }
            });
        }

        return records;
    }

    private List<NginxLogRecord> readFromUrl(String url) throws IOException {
        System.err.println("⚠️ Чтение из URL пока не реализовано: " + url);
        return new ArrayList<>();
    }
}
