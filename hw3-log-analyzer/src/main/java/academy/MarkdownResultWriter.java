package academy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MarkdownResultWriter implements ResultWriter {

    @Override
    public void writeResult(AnalysisResult result, String outputPath) throws IOException {
        StringBuilder markdown = new StringBuilder();

        markdown.append("# Анализ логов NGINX\n\n");

        markdown.append("## Общая информация\n\n");
        markdown.append("| Метрика | Значение |\n");
        markdown.append("|---------|---------:|\n");
        markdown.append(String.format("| Файл(-ы) | `%s` |%n", String.join(", ", result.getFiles())));
        markdown.append(String.format("| Количество запросов | %,d |%n", result.getTotalRequestsCount()));
        markdown.append(String.format(
                "| Средний размер ответа | %,.0f байт |%n",
                result.getResponseSizeInBytes().getAverage()));
        markdown.append(String.format(
                "| Максимальный размер ответа | %,d байт |%n",
                result.getResponseSizeInBytes().getMax()));
        markdown.append(String.format(
                "| 95%% перцентиль размера ответа | %,.0f байт |%n%n",
                result.getResponseSizeInBytes().getP95()));

        markdown.append("## Запрашиваемые ресурсы\n\n");
        markdown.append("| Ресурс | Количество |\n");
        markdown.append("|--------|-----------:|\n");
        List<AnalysisResult.ResourceStat> topResources = result.getResources();
        if (topResources.size() > 10) {
            topResources = topResources.subList(0, 10);
        }
        for (AnalysisResult.ResourceStat resource : topResources) {
            markdown.append(
                    String.format("| `%s` | %,d |%n", resource.getResource(), resource.getTotalRequestsCount()));
        }
        markdown.append("\n");

        markdown.append("## Коды ответа\n\n");
        markdown.append("| Код | Количество |\n");
        markdown.append("|-----|-----------:|\n");
        for (AnalysisResult.ResponseCodeStat code : result.getResponseCodes()) {
            markdown.append(String.format("| %d | %,d |%n", code.getCode(), code.getTotalResponsesCount()));
        }
        markdown.append("\n");

        markdown.append("## Уникальные протоколы\n\n");
        for (String protocol : result.getUniqueProtocols()) {
            markdown.append(String.format("- %s%n", protocol));
        }

        Files.writeString(Path.of(outputPath), markdown.toString());
        System.out.println("✅ Результат сохранен в Markdown: " + outputPath);
    }
}
