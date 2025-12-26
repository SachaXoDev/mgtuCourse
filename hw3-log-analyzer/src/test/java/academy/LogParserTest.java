package academy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    @DisplayName("Парсинг валидной строки лога")
    void parseLine_validLine_returnsRecord() throws LogParseException {
        String validLine =
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

        NginxLogRecord record = parser.parseLine(validLine);

        assertNotNull(record);
        assertEquals("93.180.71.3", record.getRemoteAddr());
        assertEquals("-", record.getRemoteUser());
        assertEquals(LocalDateTime.of(2015, 5, 17, 8, 5, 32), record.getTimeLocal());
        assertEquals("GET", record.getRequestMethod());
        assertEquals("/downloads/product_1", record.getResource());
        assertEquals("HTTP/1.1", record.getProtocol());
        assertEquals(304, record.getStatus());
        assertEquals(0, record.getBodyBytesSent());
        assertEquals("-", record.getHttpReferer());
        assertEquals("Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)", record.getHttpUserAgent());
    }

    @Test
    @DisplayName("Парсинг строки с HTTP 200 и размером ответа")
    void parseLine_200Response_returnsRecord() throws LogParseException {
        String line =
                "192.168.1.1 - - [18/May/2015:10:15:45 +0000] \"GET /index.html HTTP/2.0\" 200 2048 \"-\" \"Mozilla/5.0\"";

        NginxLogRecord record = parser.parseLine(line);

        assertEquals(200, record.getStatus());
        assertEquals(2048, record.getBodyBytesSent());
        assertEquals("HTTP/2.0", record.getProtocol());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "invalid line",
                "93.180.71.3 - - [invalid] \"GET /test HTTP/1.1\" 200 100 \"-\" \"test\"",
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /test\" 200 100 \"-\" \"test\"",
                ""
            })
    @DisplayName("Парсинг невалидных строк выбрасывает исключение")
    void parseLine_invalidLine_throwsException(String invalidLine) {
        assertThrows(LogParseException.class, () -> parser.parseLine(invalidLine));
    }

    @Test
    @DisplayName("Парсинг null строки выбрасывает исключение")
    void parseLine_nullLine_throwsException() {
        assertThrows(LogParseException.class, () -> parser.parseLine(null));
    }
}
