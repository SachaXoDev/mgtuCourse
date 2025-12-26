package academy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogAnalyzer {
    private static final Logger logger = LogManager.getLogger(LogAnalyzer.class);

    public AnalysisResult analyzeFiles(List<String> filePaths) throws IOException {
        AnalysisResult result = new AnalysisResult();

        List<String> fileNames = new ArrayList<>();
        for (String path : filePaths) {
            File file = Path.of(path).toFile();
            fileNames.add(file.getName());
        }
        result.setFiles(fileNames);

        FileReader fileReader = new FileReader();
        List<NginxLogRecord> allRecords = fileReader.readFiles(filePaths);

        collectStatistics(allRecords, result);

        return result;
    }

    private void collectStatistics(List<NginxLogRecord> records, AnalysisResult result) {
        if (records.isEmpty()) {
            logger.warn("Нет записей для анализа");
            return;
        }

        result.setTotalRequestsCount(records.size());

        collectSizeStatistics(records, result);

        collectResourceStatistics(records, result);

        collectResponseCodeStatistics(records, result);

        collectProtocolStatistics(records, result);
    }

    private void collectSizeStatistics(List<NginxLogRecord> records, AnalysisResult result) {
        List<Long> sizes =
                records.stream().map(NginxLogRecord::getBodyBytesSent).collect(Collectors.toList());

        long max = sizes.stream().max(Long::compareTo).orElse(0L);

        double average = sizes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        double p95 = calculatePercentile(sizes, 95);

        result.getResponseSizeInBytes().setMax(max);
        result.getResponseSizeInBytes().setAverage(Math.round(average * 100.0) / 100.0);
        result.getResponseSizeInBytes().setP95(p95);
    }

    private void collectResourceStatistics(List<NginxLogRecord> records, AnalysisResult result) {
        Map<String, Long> resourceCounts =
                records.stream().collect(Collectors.groupingBy(NginxLogRecord::getResource, Collectors.counting()));

        List<AnalysisResult.ResourceStat> topResources = resourceCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new AnalysisResult.ResourceStat(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        result.setResources(topResources);
    }

    private void collectResponseCodeStatistics(List<NginxLogRecord> records, AnalysisResult result) {
        Map<Integer, Long> codeCounts =
                records.stream().collect(Collectors.groupingBy(NginxLogRecord::getStatus, Collectors.counting()));

        List<AnalysisResult.ResponseCodeStat> codeStats = codeCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .map(entry -> new AnalysisResult.ResponseCodeStat(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        result.setResponseCodes(codeStats);
    }

    private void collectProtocolStatistics(List<NginxLogRecord> records, AnalysisResult result) {
        List<String> protocols = records.stream()
                .map(NginxLogRecord::getProtocol)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.setUniqueProtocols(protocols);
    }

    private double calculatePercentile(List<Long> values, double percentile) {
        if (values.isEmpty()) return 0.0;

        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        double rank = percentile / 100.0 * (sorted.size() - 1);
        int lower = (int) Math.floor(rank);
        int upper = lower + 1;

        if (upper >= sorted.size()) {
            return sorted.get(lower);
        }

        double lowerValue = sorted.get(lower);
        double upperValue = sorted.get(upper);
        double weight = rank - lower;

        return lowerValue + (upperValue - lowerValue) * weight;
    }
}
