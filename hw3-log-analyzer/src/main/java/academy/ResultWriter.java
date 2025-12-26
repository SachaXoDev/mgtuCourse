package academy;

import java.io.IOException;

public interface ResultWriter {
    void writeResult(AnalysisResult result, String outputPath) throws IOException;
}
