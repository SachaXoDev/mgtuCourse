package academy.acceptance;

import static org.junit.jupiter.api.Assertions.*;

import academy.Application;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LogFileParsingTest {

    @Test
    @DisplayName("На вход передан валидный локальный log-файл")
    void localFileProcessingTest(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("access.log");
        Files.write(
                testFile,
                """
            93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /downloads/product_1 HTTP/1.1" 304 0 "-" "Debian APT-HTTP/1.3"
            192.168.1.1 - - [18/May/2015:10:15:45 +0000] "GET /index.html HTTP/2.0" 200 2048 "-" "Mozilla/5.0"
            """
                        .getBytes());

        String[] args = {"--path", testFile.toString(), "--format", "json"};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode, "Должен успешно обработать валидный файл");
    }

    @Test
    @DisplayName("На вход передан локальный log-файл, часть строк в котором не подходит под формат")
    void damagedLocalFileProcessingTest(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("damaged.log");
        Files.write(
                testFile,
                """
            valid line
            93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /test HTTP/1.1" 200 100 "-" "test"
            invalid line format
            another invalid
            192.168.1.1 - - [18/May/2015:10:15:45 +0000] "GET /index.html HTTP/2.0" 200 2048 "-" "Mozilla/5.0"
            """
                        .getBytes());

        String[] args = {"--path", testFile.toString(), "--format", "json"};

        int exitCode = new Application().callInternal(args);

        assertEquals(0, exitCode, "Должен успешно обработать файл с поврежденными строками");
    }
}
