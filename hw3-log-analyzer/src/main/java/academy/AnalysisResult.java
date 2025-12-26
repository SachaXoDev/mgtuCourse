package academy;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
    private List<String> files = new ArrayList<>();
    private long totalRequestsCount;
    private ResponseSize responseSizeInBytes = new ResponseSize();
    private List<ResourceStat> resources = new ArrayList<>();
    private List<ResponseCodeStat> responseCodes = new ArrayList<>();
    private List<RequestDateStat> requestsPerDate = new ArrayList<>();
    private List<String> uniqueProtocols = new ArrayList<>();

    public static class ResponseSize {
        private double average;
        private long max;
        private double p95;

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }

        public double getP95() {
            return p95;
        }

        public void setP95(double p95) {
            this.p95 = p95;
        }
    }

    public static class ResourceStat {
        private String resource;
        private long totalRequestsCount;

        public ResourceStat(String resource, long count) {
            this.resource = resource;
            this.totalRequestsCount = count;
        }

        public String getResource() {
            return resource;
        }

        public long getTotalRequestsCount() {
            return totalRequestsCount;
        }
    }

    public static class ResponseCodeStat {
        private int code;
        private long totalResponsesCount;

        public ResponseCodeStat(int code, long count) {
            this.code = code;
            this.totalResponsesCount = count;
        }

        public int getCode() {
            return code;
        }

        public long getTotalResponsesCount() {
            return totalResponsesCount;
        }
    }

    public static class RequestDateStat {
        private String date;
        private String weekday;
        private long totalRequestsCount;
        private double totalRequestsPercentage;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getWeekday() {
            return weekday;
        }

        public void setWeekday(String weekday) {
            this.weekday = weekday;
        }

        public long getTotalRequestsCount() {
            return totalRequestsCount;
        }

        public void setTotalRequestsCount(long totalRequestsCount) {
            this.totalRequestsCount = totalRequestsCount;
        }

        public double getTotalRequestsPercentage() {
            return totalRequestsPercentage;
        }

        public void setTotalRequestsPercentage(double totalRequestsPercentage) {
            this.totalRequestsPercentage = totalRequestsPercentage;
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public long getTotalRequestsCount() {
        return totalRequestsCount;
    }

    public void setTotalRequestsCount(long totalRequestsCount) {
        this.totalRequestsCount = totalRequestsCount;
    }

    public ResponseSize getResponseSizeInBytes() {
        return responseSizeInBytes;
    }

    public List<ResourceStat> getResources() {
        return resources;
    }

    public void setResources(List<ResourceStat> resources) {
        this.resources = resources;
    }

    public List<ResponseCodeStat> getResponseCodes() {
        return responseCodes;
    }

    public void setResponseCodes(List<ResponseCodeStat> responseCodes) {
        this.responseCodes = responseCodes;
    }

    public List<RequestDateStat> getRequestsPerDate() {
        return requestsPerDate;
    }

    public List<String> getUniqueProtocols() {
        return uniqueProtocols;
    }

    public void setUniqueProtocols(List<String> uniqueProtocols) {
        this.uniqueProtocols = uniqueProtocols;
    }
}
