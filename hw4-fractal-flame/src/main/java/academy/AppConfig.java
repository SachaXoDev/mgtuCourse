package academy;

import academy.model.AffineParameters;
import academy.model.Function;
import academy.model.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AppConfig(
        Size size,
        @JsonProperty("iteration_count") int iterationCount,
        @JsonProperty("output_path") String outputPath,
        int threads,
        long seed,
        List<Function> functions,
        @JsonProperty("affine_params") List<AffineParameters> affineParams,
        @JsonProperty("gamma_correction") boolean gammaCorrection,
        double gamma,
        @JsonProperty("symmetry_level") int symmetryLevel) {}
