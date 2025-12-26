package academy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateValidator {

    public static void validateDates(String fromDate, String toDate) {
        LocalDate from = null;
        LocalDate to = null;

        if (fromDate != null) {
            from = parseDate(fromDate, "from");
        }
        if (toDate != null) {
            to = parseDate(toDate, "to");
        }
        if (from != null && to != null && !from.isBefore(to)) {
            throw new IllegalArgumentException("Дата 'from' должна быть раньше даты 'to'");
        }
    }

    private static LocalDate parseDate(String date, String paramName) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Неверный формат даты для " + paramName + ": " + date + ". Ожидается формат ISO8601 (YYYY-MM-DD)");
        }
    }
}
