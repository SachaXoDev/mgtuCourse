package academy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private static final Pattern NGINX_LOG_PATTERN = Pattern.compile(
            "^(\\S+) - (\\S+) \\[(.+?)\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"$");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
            .toFormatter(Locale.ENGLISH);

    public NginxLogRecord parseLine(String line) throws LogParseException {
        if (line == null || line.trim().isEmpty()) {
            throw new LogParseException("Line is empty or null");
        }

        Matcher matcher = NGINX_LOG_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new LogParseException("Line does not match NGINX log format: " + line);
        }

        try {
            NginxLogRecord record = new NginxLogRecord();
            record.setRemoteAddr(matcher.group(1));
            record.setRemoteUser(matcher.group(2));

            String dateTimeString = matcher.group(3);
            record.setTimeLocal(parseDateTime(dateTimeString));

            record.setRequestMethod(matcher.group(4));
            record.setResource(matcher.group(5));
            record.setProtocol(matcher.group(6));
            record.setStatus(Integer.parseInt(matcher.group(7)));
            record.setBodyBytesSent(Long.parseLong(matcher.group(8)));
            record.setHttpReferer(matcher.group(9));
            record.setHttpUserAgent(matcher.group(10));

            return record;
        } catch (Exception e) {
            throw new LogParseException("Failed to parse log line: " + line, e);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            System.out.println("⚠️  Стандартный парсер не сработал, используем ручной парсинг для: " + dateTimeString);
            return parseDateTimeManual(dateTimeString);
        }
    }

    private LocalDateTime parseDateTimeManual(String dateTimeString) {
        try {
            String[] parts = dateTimeString.split("[/ :\\+]");

            int day = Integer.parseInt(parts[0]);
            String monthStr = parts[1].toLowerCase();
            int year = Integer.parseInt(parts[2]);
            int hour = Integer.parseInt(parts[3]);
            int minute = Integer.parseInt(parts[4]);
            int second = Integer.parseInt(parts[5]);

            int month =
                    switch (monthStr) {
                        case "jan" -> 1;
                        case "feb" -> 2;
                        case "mar" -> 3;
                        case "apr" -> 4;
                        case "may" -> 5;
                        case "jun" -> 6;
                        case "jul" -> 7;
                        case "aug" -> 8;
                        case "sep" -> 9;
                        case "oct" -> 10;
                        case "nov" -> 11;
                        case "dec" -> 12;
                        default -> throw new IllegalArgumentException("Unknown month: " + monthStr);
                    };

            return LocalDateTime.of(year, month, day, hour, minute, second);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Failed to parse date manually: " + dateTimeString, e);
        }
    }
}
