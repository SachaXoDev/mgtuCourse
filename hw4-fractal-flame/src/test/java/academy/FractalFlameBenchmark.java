package academy;

import academy.model.AffineParameters;
import academy.model.Function;
import academy.model.Size;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
@Fork(1)
public class FractalFlameBenchmark {

    private FractalFlameGenerator generator;
    private AppConfig config1Thread;
    private AppConfig config4Threads;
    private AppConfig configPolar;

    @Setup
    public void setup() {
        generator = new FractalFlameGenerator();
        Size size = new Size(1920, 1080);
        List<AffineParameters> affine = Collections.singletonList(new AffineParameters(0.5, 0.1, 0, 0.1, 0.5, 0));
        List<Function> functionsSwirl = Collections.singletonList(new Function("swirl", 1.0));
        List<Function> functionsPolar = Collections.singletonList(new Function("polar", 1.0));

        config1Thread = new AppConfig(size, 50000000, "test.png", 1, 123, functionsSwirl, affine, false, 2.2, 1);
        config4Threads = new AppConfig(size, 50000000, "test.png", 64, 123, functionsSwirl, affine, false, 2.2, 1);
        configPolar = new AppConfig(size, 50000000, "test.png", 4, 123, functionsPolar, affine, false, 2.2, 1);
    }

    @Benchmark
    public void singleThreadBenchmark() {
        generator.generate(config1Thread);
    }

    @Benchmark
    public void multiThreadBenchmark() {
        generator.generate(config4Threads);
    }

    @Benchmark
    public void polarBenchmark() {
        generator.generate(configPolar);
    }
}
