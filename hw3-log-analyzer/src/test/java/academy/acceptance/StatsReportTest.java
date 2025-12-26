package academy.acceptance;

import static org.junit.jupiter.api.Assertions.*;

import academy.Application;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StatsReportTest {

    @Test
    @DisplayName("Сохранение статистики в формате JSON")
    void jsonTest(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test.log");
        Files.write(
                testFile,
                """
            93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /test HTTP/1.1" 200 100 "-" "test"
            """
                        .getBytes());

        Path outputFile = tempDir.resolve("report.json");
        String[] args = {"--path", testFile.toString(), "--format", "json", "--output", outputFile.toString()};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode);
        assertTrue(Files.exists(outputFile));

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"totalRequestsCount\""));
        assertTrue(content.contains("\"resources\""));
        assertTrue(content.contains("\"responseCodes\""));
    }

    @Test
    @DisplayName("Сохранение статистики в формате MARKDOWN")
    void markdownTest(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test.log");
        Files.write(
                testFile,
                """
            93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /test HTTP/1.1" 200 100 "-" "test"
            """
                        .getBytes());

        Path outputFile = tempDir.resolve("report.md");
        String[] args = {"--path", testFile.toString(), "--format", "markdown", "--output", outputFile.toString()};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode);
        assertTrue(Files.exists(outputFile));

        String content = Files.readString(outputFile);
        assertTrue(content.contains("# Анализ логов NGINX"));
        assertTrue(content.contains("Общая информация"));
        assertTrue(content.contains("Запрашиваемые ресурсы"));
    }

    @Test
    @DisplayName("Сохранение статистики в формате ADOC")
    void adocTest(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test.log");
        Files.write(
                testFile,
                """
        93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /test HTTP/1.1" 200 100 "-" "test"
        """
                        .getBytes());

        Path outputFile = tempDir.resolve("report.adoc");
        String[] args = {"--path", testFile.toString(), "--format", "adoc", "--output", outputFile.toString()};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode, "Должен успешно создать ADOC файл");
        assertTrue(Files.exists(outputFile), "Должен создать ADOC файл");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("= Анализ логов NGINX"), "Должен содержать заголовок");
        assertTrue(content.contains("== Общая информация"), "Должен содержать секцию общей информации");
    }
}
