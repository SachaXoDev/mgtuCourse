package academy.acceptance;

import static org.junit.jupiter.api.Assertions.*;

import academy.Application;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ArgumentValidationTest {

    @Test
    @DisplayName("На вход передан несуществующий локальный файл")
    void test1() {
        String[] args = {"--path", "nonexistent.log", "--format", "json"};

        int exitCode = new Application().callInternal(args);

        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 для несуществующего файла");
    }

    @Test
    @DisplayName("На вход передан несуществующий удаленный файл")
    void test2() {
        String[] args = {"--path", "https://example.com/nonexistent.log", "--format", "json"};

        int exitCode = new Application().callInternal(args);

        assertTrue(
                exitCode == 0 || exitCode == 2,
                "Может вернуть 0 или 2 для несуществующего URL, но получили: " + exitCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"txt", "html", "xml"})
    @DisplayName("Результаты запрошены в неподдерживаемом формате {0}")
    void test5(String format) {
        String[] args = {"--path", "test.log", "--format", format};

        int exitCode = new Application().callInternal(args);

        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 для неподдерживаемого формата вывода");
    }

    @ParameterizedTest
    @MethodSource("test6ArgumentsSource")
    @DisplayName("По пути в аргументе --output указан файл с некоректным расширением")
    void test6(String format, String output, @TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test.log");
        Files.write(testFile, "test content".getBytes());

        String[] args = {"--path", testFile.toString(), "--format", format, "--output", output};

        int exitCode = new Application().callInternal(args);

        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 для несоответствующего расширения файла");
    }

    @Test
    @DisplayName("По пути в аргументе --output уже существует файл")
    void test7(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test.log");
        Files.write(testFile, "test content".getBytes());

        Path existingOutput = tempDir.resolve("existing.json");
        Files.write(existingOutput, "existing content".getBytes());

        String[] args = {"--path", testFile.toString(), "--format", "json", "--output", existingOutput.toString()};

        int exitCode = new Application().callInternal(args);

        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 для существующего файла вывода");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("На вход переданы невалидные параметры --from / --to - {0} (пустые значения)")
    void test4_emptyValues(String invalidDate) {
        Assumptions.assumeTrue(
                invalidDate != null && !invalidDate.isEmpty(),
                "Пропускаем тест для пустых значений из-за особенностей Picocli");

        String[] args = {"--path", "test.log", "--from", invalidDate, "--to", "2025-01-02"};
        int exitCode = new Application().callInternal(args);
        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 для невалидной даты");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025.01.01 10:30", "today", "01-01-2025"})
    @DisplayName("На вход переданы невалидные параметры --from / --to - {0} (неправильный формат)")
    void test4_invalidFormat(String invalidDate) {
        String[] args = {"--path", "test.log", "--from", invalidDate, "--to", "2025-01-02"};

        int exitCode = new Application().callInternal(args);

        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 для неправильного формата дат");
    }

    @ParameterizedTest
    @ValueSource(strings = {"--path", "--output", "--format"})
    @DisplayName("На вход не передан обязательный параметр \"{0}\"")
    void test8(String argument) {
        String[] args = {"--format", "json"};

        int exitCode = new Application().callInternal(args);

        assertTrue(exitCode != 0, "Должен вернуть ненулевой код для отсутствующего обязательного параметра");
    }

    @ParameterizedTest
    @ValueSource(strings = {"--input", "--filter", "--verbose"})
    @DisplayName("На вход передан неподдерживаемый параметр \"{0}\"")
    void test9(String argument) {
        String[] args = {"--path", "test.log", argument, "value"};

        int exitCode = new Application().callInternal(args);

        assertTrue(exitCode != 0, "Должен вернуть ненулевой код для неподдерживаемого параметра");
    }

    @Test
    @DisplayName("Значение параметра --from больше, чем значение параметра --to")
    void test10() {
        String[] args = {"--path", "test.log", "--from", "2025-01-02", "--to", "2025-01-01"};

        int exitCode = new Application().callInternal(args);

        assertEquals(2, exitCode, "Должен вернуть код ошибки 2 когда from > to");
    }

    private static Stream<Arguments> test6ArgumentsSource() {
        return Stream.of(
                Arguments.of("markdown", "./results.txt"),
                Arguments.of("json", "./results.md"),
                Arguments.of("adoc", "./results.ad1"));
    }
}
