package academy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationTest {

    @Test
    @DisplayName("Базовая проверка работоспособности программы")
    void happyPathTest(@TempDir Path tempDir) throws IOException {
        // Создаем тестовый файл
        Path testFile = tempDir.resolve("test.log");
        Files.write(
                testFile,
                """
            93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /downloads/product_1 HTTP/1.1" 304 0 "-" "test"
            192.168.1.1 - - [18/May/2015:10:15:45 +0000] "GET /index.html HTTP/2.0" 200 2048 "-" "test"
            """
                        .getBytes());

        Path outputFile = tempDir.resolve("report.json");
        String[] args = {"--path", testFile.toString(), "--format", "json", "--output", outputFile.toString()};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode, "Программа должна завершиться успешно");
        assertTrue(Files.exists(outputFile), "Должен создать файл с результатами");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("totalRequestsCount"), "Должен содержать статистику");
    }
}
