package academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Path;

public class JsonResultWriter implements ResultWriter {
    private final ObjectMapper objectMapper;

    public JsonResultWriter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void writeResult(AnalysisResult result, String outputPath) throws IOException {
        Path path = Path.of(outputPath);
        objectMapper.writeValue(path.toFile(), result);
        System.out.println("✅ Результат сохранен в JSON: " + outputPath);
    }
}
