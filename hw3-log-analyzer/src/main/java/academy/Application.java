package academy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "log-analyzer",
        version = "Log Analyzer 1.0",
        mixinStandardHelpOptions = true,
        description = "Анализатор логов NGINX")
public class Application implements Callable<Integer> {

    @Option(
            names = {"--path", "-p"},
            description = "Путь к одному или нескольким NGINX лог-файлам",
            required = true)
    private List<String> paths;

    @Option(
            names = {"--format", "-f"},
            description = "Формат вывода: json, markdown, adoc",
            defaultValue = "json")
    private String format;

    @Option(
            names = {"--output", "-o"},
            description = "Путь до файла для сохранения результата")
    private String output;

    @Option(
            names = {"--from"},
            description = "Начальная дата в формате ISO8601 (например, 2025-03-01)")
    private String fromDate;

    @Option(
            names = {"--to"},
            description = "Конечная дата в формате ISO8601 (например, 2025-03-31)")
    private String toDate;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            validateParameters();
            if (fromDate != null || toDate != null) {
                DateValidator.validateDates(fromDate, toDate);
            }

            System.out.println("=== Лог-анализатор NGINX ===");
            System.out.println("Пути к файлам: " + paths);
            System.out.println("Формат вывода: " + format);
            System.out.println("Файл результата: " + output);

            for (String path : paths) {
                if (!path.startsWith("http") && !Files.exists(Path.of(path))) {
                    throw new IllegalArgumentException("Файл не найден: " + path);
                }
            }

            if (output != null && Files.exists(Path.of(output))) {
                throw new IllegalArgumentException("Файл уже существует: " + output);
            }

            LogAnalyzer analyzer = new LogAnalyzer();
            AnalysisResult result = analyzer.analyzeFiles(paths);

            printConsoleResults(result);

            if (output != null) {
                saveToFile(result, format, output);
            }

            System.out.println("✅ Анализ завершен успешно!");
            return 0;
        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
            return 2;
        }
    }

    private void validateParameters() {
        if (paths == null || paths.isEmpty()) {
            throw new IllegalArgumentException("Не указан обязательный параметр --path");
        }

        if (format != null) {
            switch (format) {
                case "json":
                case "markdown":
                case "adoc":
                    break;
                default:
                    throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
            }

            if (output != null) {
                validateFileExtension(format, output);
            }
        }
    }

    private void validateFileExtension(String format, String outputPath) {
        String fileName = outputPath.toLowerCase();
        switch (format) {
            case "json":
                if (!fileName.endsWith(".json")) {
                    throw new IllegalArgumentException("Для формата json требуется расширение .json");
                }
                break;
            case "markdown":
                if (!fileName.endsWith(".md")) {
                    throw new IllegalArgumentException("Для формата markdown требуется расширение .md");
                }
                break;
            case "adoc":
                if (!fileName.endsWith(".adoc") && !fileName.endsWith(".ad")) {
                    throw new IllegalArgumentException("Для формата adoc требуется расширение .adoc или .ad");
                }
                break;
        }
    }

    private void printConsoleResults(AnalysisResult result) {
        System.out.println("\n=== РЕЗУЛЬТАТЫ АНАЛИЗА ===");
        System.out.println("📊 Общее количество запросов: " + result.getTotalRequestsCount());

        System.out.println("💾 Размеры ответов:");
        System.out.println("   Средний: " + result.getResponseSizeInBytes().getAverage() + " байт");
        System.out.println("   Максимальный: " + result.getResponseSizeInBytes().getMax() + " байт");
        System.out.println(
                "   95% перцентиль: " + result.getResponseSizeInBytes().getP95() + " байт");

        System.out.println("🌐 Топ-5 ресурсов:");
        result.getResources().stream()
                .limit(5)
                .forEach(resource -> System.out.println(
                        "   " + resource.getResource() + " - " + resource.getTotalRequestsCount() + " запросов"));

        System.out.println("🔢 Коды ответов:");
        result.getResponseCodes()
                .forEach(code ->
                        System.out.println("   " + code.getCode() + " - " + code.getTotalResponsesCount() + " раз"));

        System.out.println("📡 Уникальные протоколы: " + String.join(", ", result.getUniqueProtocols()));
    }

    private void saveToFile(AnalysisResult result, String format, String outputPath) throws Exception {
        ResultWriter writer;
        switch (format) {
            case "json":
                writer = new JsonResultWriter();
                break;
            case "markdown":
                writer = new MarkdownResultWriter();
                break;
            case "adoc":
                writer = new AdocResultWriter();
                break;
            default:
                throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        }
        writer.writeResult(result, outputPath);
    }

    /**
     * Метод для тестирования, который принимает аргументы как массив строк Вместо выхода из системы, возвращает код
     * выхода
     */
    public int callInternal(String[] args) {
        try {
            CommandLine cmd = new CommandLine(this);
            return cmd.execute(args);
        } catch (Exception e) {
            return 2;
        }
    }
}
