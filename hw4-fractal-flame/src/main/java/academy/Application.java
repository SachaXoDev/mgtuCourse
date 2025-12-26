package academy;

import academy.model.AffineParameters;
import academy.model.Function;
import academy.model.Size;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "fractal-flame", version = "1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final ObjectReader JSON_READER =
            new ObjectMapper().findAndRegisterModules().reader();
    private static final Random RANDOM = new Random();

    @Option(
            names = {"-w", "--width"},
            description = "Width of the final image")
    private Integer width;

    @Option(
            names = {"-h", "--height"},
            description = "Height of the final image")
    private Integer height;

    @Option(names = "--seed", description = "Random generator seed")
    private Long seed;

    @Option(
            names = {"-i", "--iteration-count"},
            description = "Number of generation iterations")
    private Integer iterationCount;

    @Option(
            names = {"-o", "--output-path"},
            description = "Path to save the image")
    private String outputPath;

    @Option(
            names = {"-t", "--threads"},
            description = "Number of threads")
    private Integer threads;

    @Option(
            names = {"-ap", "--affine-params"},
            description = "Configuration of affine transformations")
    private String affineParams;

    @Option(
            names = {"-f", "--functions"},
            description = "Configuration of applied transformation methods")
    private String functions;

    @Option(
            names = {"-g", "--gamma-correction"},
            description = "Enable gamma correction")
    private boolean gammaCorrection;

    @Option(names = "--gamma", description = "Gamma value for correction")
    private Double gamma;

    @Option(
            names = {"-s", "--symmetry-level"},
            description = "Symmetry level")
    private Integer symmetryLevel;

    @Option(names = "--config", description = "Path to JSON config file")
    private File configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        AppConfig config = loadConfig();
        LOGGER.atInfo().addKeyValue("config", config).log("Config content");

        FractalFlameGenerator generator = new FractalFlameGenerator();
        BufferedImage image = generator.generate(config);

        if (image != null) {
            try {
                File outputFile = Path.of(config.outputPath()).toFile();
                ImageIO.write(image, "png", outputFile);
                LOGGER.info("Image saved to {}", outputFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Failed to save image", e);
            }
        }
    }

    private AppConfig loadConfig() {
        AppConfig fileConfig = null;
        if (configPath != null) {
            try {
                fileConfig = JSON_READER.readValue(configPath, AppConfig.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        int finalWidth = (width != null)
                ? width
                : (fileConfig != null && fileConfig.size() != null)
                        ? fileConfig.size().width()
                        : 1920;
        int finalHeight = (height != null)
                ? height
                : (fileConfig != null && fileConfig.size() != null)
                        ? fileConfig.size().height()
                        : 1080;
        long finalSeed = (seed != null) ? seed : (fileConfig != null) ? fileConfig.seed() : 5;
        int finalIterationCount =
                (iterationCount != null) ? iterationCount : (fileConfig != null) ? fileConfig.iterationCount() : 2500;
        String finalOutputPath =
                (outputPath != null) ? outputPath : (fileConfig != null) ? fileConfig.outputPath() : "result.png";
        int finalThreads = (threads != null) ? threads : (fileConfig != null) ? fileConfig.threads() : 1;
        List<Function> finalFunctions = (functions != null)
                ? parseFunctions(functions)
                : (fileConfig != null) ? fileConfig.functions() : Collections.emptyList();
        List<AffineParameters> finalAffineParams = (affineParams != null)
                ? parseAffineParams(affineParams)
                : (fileConfig != null
                                && fileConfig.affineParams() != null
                                && !fileConfig.affineParams().isEmpty())
                        ? fileConfig.affineParams()
                        : defaultAffineParams();
        boolean finalGammaCorrection = gammaCorrection || (fileConfig != null && fileConfig.gammaCorrection());
        double finalGamma = (gamma != null) ? gamma : (fileConfig != null) ? fileConfig.gamma() : 2.2;
        int finalSymmetryLevel =
                (symmetryLevel != null) ? symmetryLevel : (fileConfig != null) ? fileConfig.symmetryLevel() : 1;

        return new AppConfig(
                new Size(finalWidth, finalHeight),
                finalIterationCount,
                finalOutputPath,
                finalThreads,
                finalSeed,
                finalFunctions,
                finalAffineParams,
                finalGammaCorrection,
                finalGamma,
                finalSymmetryLevel);
    }

    private List<AffineParameters> defaultAffineParams() {
        List<AffineParameters> paramsList = new ArrayList<>();
        paramsList.add(
                new AffineParameters(0.195, -0.488, 0, 0.446, 0.434, 0.344, new Color(RANDOM.nextInt(0x1000000))));
        paramsList.add(
                new AffineParameters(0.462, 0.414, 0, -0.252, 0.361, 0.57, new Color(RANDOM.nextInt(0x1000000))));
        paramsList.add(new AffineParameters(-0.637, 0, 0, 0, 0.501, 0.829, new Color(RANDOM.nextInt(0x1000000))));
        return paramsList;
    }

    private List<Function> parseFunctions(String functionsStr) {
        if (functionsStr == null || functionsStr.isEmpty()) {
            return Collections.emptyList();
        }
        List<Function> functionList = new ArrayList<>();
        String[] pairs = functionsStr.split(",");
        for (String pair : pairs) {
            String[] parts = pair.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid function format: " + pair);
            }
            functionList.add(new Function(parts[0], Double.parseDouble(parts[1])));
        }
        return functionList;
    }

    private List<AffineParameters> parseAffineParams(String affineParamsStr) {
        if (affineParamsStr == null || affineParamsStr.isEmpty()) {
            return Collections.emptyList();
        }
        List<AffineParameters> paramsList = new ArrayList<>();
        String[] sets = affineParamsStr.split("/");
        for (String set : sets) {
            String[] values = set.split(",");
            if (values.length != 6) {
                throw new IllegalArgumentException("Invalid affine parameters format: " + set);
            }
            paramsList.add(new AffineParameters(
                    Double.parseDouble(values[0]),
                    Double.parseDouble(values[1]),
                    Double.parseDouble(values[2]),
                    Double.parseDouble(values[3]),
                    Double.parseDouble(values[4]),
                    Double.parseDouble(values[5]),
                    new Color(RANDOM.nextInt(0x1000000))));
        }
        return paramsList;
    }
}
