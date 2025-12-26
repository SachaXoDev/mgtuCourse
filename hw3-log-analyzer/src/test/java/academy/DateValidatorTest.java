package academy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DateValidatorTest {

    @Test
    @DisplayName("Валидация корректных дат")
    void validateDates_validDates_noException() {
        assertDoesNotThrow(() -> DateValidator.validateDates("2025-01-01", "2025-01-02"));
    }

    @Test
    @DisplayName("Валидация когда from > to выбрасывает исключение")
    void validateDates_fromAfterTo_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> DateValidator.validateDates("2025-01-02", "2025-01-01"));

        assertTrue(exception.getMessage().contains("должна быть раньше"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025.01.01", "01-01-2025", "invalid", "2025-13-01"})
    @DisplayName("Валидация невалидных форматов дат")
    void validateDates_invalidFormat_throwsException(String invalidDate) {
        assertThrows(IllegalArgumentException.class, () -> DateValidator.validateDates(invalidDate, "2025-01-02"));
    }

    @Test
    @DisplayName("Валидация когда одна из дат null")
    void validateDates_oneDateNull_noException() {
        assertDoesNotThrow(() -> {
            DateValidator.validateDates("2025-01-01", null);
            DateValidator.validateDates(null, "2025-01-01");
            DateValidator.validateDates(null, null);
        });
    }
}
