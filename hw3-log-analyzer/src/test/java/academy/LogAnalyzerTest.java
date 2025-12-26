package academy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LogAnalyzerTest {

    private final LogAnalyzer analyzer = new LogAnalyzer();

    @Test
    @DisplayName("Анализ пустого списка файлов")
    void analyzeFiles_emptyList_returnsEmptyResult() throws IOException {
        AnalysisResult result = analyzer.analyzeFiles(Arrays.asList("nonexistent.log"));

        assertEquals(1, result.getFiles().size());
        assertEquals(0, result.getTotalRequestsCount());
        assertEquals(0, result.getResponseSizeInBytes().getMax());
        assertTrue(result.getResources().isEmpty());
    }

    @Test
    @DisplayName("Анализ файла с валидными записями")
    void analyzeFiles_validFile_returnsStatistics(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test.log");
        Files.write(
                testFile,
                Arrays.asList(
                        "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"test\"",
                        "93.180.71.3 - - [17/May/2015:08:05:33 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"test\"",
                        "93.180.71.3 - - [17/May/2015:08:05:34 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"test\"",
                        "192.168.1.1 - - [18/May/2015:10:15:45 +0000] \"GET /index.html HTTP/2.0\" 200 2048 \"-\" \"test\"",
                        "10.0.0.1 - - [18/May/2015:10:16:10 +0000] \"GET /about.html HTTP/1.1\" 404 0 \"-\" \"test\""));

        AnalysisResult result = analyzer.analyzeFiles(Arrays.asList(testFile.toString()));

        assertEquals(5, result.getTotalRequestsCount());
        assertEquals(2048, result.getResponseSizeInBytes().getMax());
        assertEquals(409.6, result.getResponseSizeInBytes().getAverage(), 0.01);

        assertEquals(3, result.getResources().size());
        assertEquals("/downloads/product_1", result.getResources().get(0).getResource());
        assertEquals(3, result.getResources().get(0).getTotalRequestsCount()); // 3 запроса!

        assertEquals(3, result.getResponseCodes().size());

        assertEquals(2, result.getUniqueProtocols().size());
        assertTrue(result.getUniqueProtocols().contains("HTTP/1.1"));
        assertTrue(result.getUniqueProtocols().contains("HTTP/2.0"));
    }

    @Test
    @DisplayName("Расчет перцентиля")
    void calculatePercentile_variousValues_returnsCorrectPercentile() {
        LogAnalyzer analyzer = new LogAnalyzer();

        List<Long> values = Arrays.asList(10L, 20L, 30L, 40L, 50L);
    }
}
