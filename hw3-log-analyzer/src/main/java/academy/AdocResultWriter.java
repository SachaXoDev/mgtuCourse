package academy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AdocResultWriter implements ResultWriter {

    @Override
    public void writeResult(AnalysisResult result, String outputPath) throws IOException {
        StringBuilder adoc = new StringBuilder();

        adoc.append("= Анализ логов NGINX\n");
        adoc.append(":toc:\n");
        adoc.append(":numbered:\n\n");

        adoc.append("== Общая информация\n\n");
        adoc.append("[cols=\"1,1\"]\n");
        adoc.append("|===\n");
        adoc.append("| Метрика | Значение\n\n");
        adoc.append("| Файл(-ы) | `")
                .append(String.join(", ", result.getFiles()))
                .append("`\n");
        adoc.append("| Количество запросов | ")
                .append(formatNumber(result.getTotalRequestsCount()))
                .append("\n");
        adoc.append("| Средний размер ответа | ")
                .append(formatNumber(result.getResponseSizeInBytes().getAverage()))
                .append(" байт\n");
        adoc.append("| Максимальный размер ответа | ")
                .append(formatNumber(result.getResponseSizeInBytes().getMax()))
                .append(" байт\n");
        adoc.append("| 95% перцентиль размера ответа | ")
                .append(formatNumber(result.getResponseSizeInBytes().getP95()))
                .append(" байт\n");
        adoc.append("|===\n\n");

        adoc.append("== Запрашиваемые ресурсы\n\n");
        adoc.append("[cols=\"2,1\"]\n");
        adoc.append("|===\n");
        adoc.append("| Ресурс | Количество\n\n");
        List<AnalysisResult.ResourceStat> topResources = result.getResources();
        if (topResources.size() > 10) {
            topResources = topResources.subList(0, 10);
        }
        for (AnalysisResult.ResourceStat resource : topResources) {
            adoc.append("| `")
                    .append(resource.getResource())
                    .append("` | ")
                    .append(formatNumber(resource.getTotalRequestsCount()))
                    .append("\n");
        }
        adoc.append("|===\n\n");

        adoc.append("== Коды ответа\n\n");
        adoc.append("[cols=\"1,1\"]\n");
        adoc.append("|===\n");
        adoc.append("| Код | Количество\n\n");
        for (AnalysisResult.ResponseCodeStat code : result.getResponseCodes()) {
            adoc.append("| ")
                    .append(code.getCode())
                    .append(" | ")
                    .append(formatNumber(code.getTotalResponsesCount()))
                    .append("\n");
        }
        adoc.append("|===\n\n");

        adoc.append("== Уникальные протоколы\n\n");
        for (String protocol : result.getUniqueProtocols()) {
            adoc.append("* ").append(protocol).append("\n");
        }

        Files.writeString(Path.of(outputPath), adoc.toString());
        System.out.println("✅ Результат сохранен в AsciiDoc: " + outputPath);
    }

    private String formatNumber(Number number) {
        if (number instanceof Double) {
            return String.format("%,.0f", number);
        } else if (number instanceof Long || number instanceof Integer) {
            return String.format("%,d", number);
        }
        return number.toString();
    }
}
