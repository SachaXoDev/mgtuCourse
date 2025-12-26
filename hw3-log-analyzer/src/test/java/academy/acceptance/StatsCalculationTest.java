package academy.acceptance;

import static org.junit.jupiter.api.Assertions.*;

import academy.Application;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StatsCalculationTest {

    @Test
    @DisplayName("Расчет статистики на основании локального log-файла")
    void happyPathTest(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("access.log");
        Files.write(
                testFile,
                """
            93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /downloads/product_1 HTTP/1.1" 304 0 "-" "test"
            93.180.71.3 - - [17/May/2015:08:05:33 +0000] "GET /downloads/product_1 HTTP/1.1" 304 0 "-" "test"
            192.168.1.1 - - [18/May/2015:10:15:45 +0000] "GET /index.html HTTP/2.0" 200 2048 "-" "test"
            10.0.0.1 - - [18/May/2015:10:16:10 +0000] "GET /about.html HTTP/1.1" 404 0 "-" "test"
            """
                        .getBytes());

        Path outputFile = tempDir.resolve("report.json");
        String[] args = {"--path", testFile.toString(), "--format", "json", "--output", outputFile.toString()};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode, "Должен успешно выполнить анализ");
        assertTrue(Files.exists(outputFile), "Должен создать файл с результатами");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"totalRequestsCount\" : 4"), "Должен содержать правильное количество запросов");
        assertTrue(content.contains("/downloads/product_1"), "Должен содержать топ ресурсы");
    }
}
