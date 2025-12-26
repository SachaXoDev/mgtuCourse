package academy;

import academy.model.AffineParameters;
import academy.model.Function;
import academy.model.Size;
import academy.transformations.Point;
import academy.transformations.Transformation;
import academy.transformations.TransformationFactory;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class FractalFlameGenerator {

    private static class RenderResult {
        final int[] hits;
        final int[] r;
        final int[] g;
        final int[] b;

        RenderResult(int width, int height) {
            int size = width * height;
            this.hits = new int[size];
            this.r = new int[size];
            this.g = new int[size];
            this.b = new int[size];
        }
    }

    public BufferedImage generate(AppConfig config) {
        Size size = config.size();
        int width = size.width();
        int height = size.height();
        int numThreads = config.threads();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<RenderResult>> futures = new ArrayList<>();

        int iterationsPerThread = config.iterationCount() / numThreads;

        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(new RenderTask(config, iterationsPerThread, width, height)));
        }

        int totalPixels = width * height;
        int[] globalHits = new int[totalPixels];
        int[] globalR = new int[totalPixels];
        int[] globalG = new int[totalPixels];
        int[] globalB = new int[totalPixels];

        try {
            for (Future<RenderResult> future : futures) {
                RenderResult result = future.get();
                for (int k = 0; k < totalPixels; k++) {
                    if (result.hits[k] > 0) {
                        globalHits[k] += result.hits[k];
                        globalR[k] += result.r[k];
                        globalG[k] += result.g[k];
                        globalB[k] += result.b[k];
                    }
                }
            }
        } catch (Exception e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to generate fractal", e);
        } finally {
            executor.shutdown();
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        applyGammaAndRender(config, globalHits, globalR, globalG, globalB, image);

        return image;
    }

    private Color mapTo8BitColor(Color color) {
        int r = Math.max(0, Math.min(255, (int) (Math.round(color.getRed() / 51.0) * 51)));
        int g = Math.max(0, Math.min(255, (int) (Math.round(color.getGreen() / 51.0) * 51)));
        int b = Math.max(0, Math.min(255, (int) (Math.round(color.getBlue() / 51.0) * 51)));
        return new Color(r, g, b);
    }

    private void applyGammaAndRender(AppConfig config, int[] hits, int[] r, int[] g, int[] b, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double maxNormal = 0.0;

        double[] normals = new double[hits.length];

        if (config.gammaCorrection()) {
            for (int i = 0; i < hits.length; i++) {
                if (hits[i] != 0) {
                    double normal = Math.log10(hits[i]);
                    normals[i] = normal;
                    if (normal > maxNormal) {
                        maxNormal = normal;
                    }
                }
            }
        }

        double gamma = config.gamma();
        boolean useGamma = config.gammaCorrection() && maxNormal > 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = y * width + x;

                if (hits[i] == 0) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    int red = r[i];
                    int green = g[i];
                    int blue = b[i];
                    int count = hits[i];

                    Color finalColor;
                    if (useGamma) {
                        double normal = normals[i] / maxNormal;

                        float rAvg = (float) red / count / 255.0f;
                        float gAvg = (float) green / count / 255.0f;
                        float bAvg = (float) blue / count / 255.0f;

                        double factor = Math.pow(normal, 1.0 / gamma);
                        float rGamma = (float) (rAvg * factor);
                        float gGamma = (float) (gAvg * factor);
                        float bGamma = (float) (bAvg * factor);

                        finalColor = new Color(
                                Math.max(0.0f, Math.min(1.0f, rGamma)),
                                Math.max(0.0f, Math.min(1.0f, gGamma)),
                                Math.max(0.0f, Math.min(1.0f, bGamma)));
                    } else {
                        finalColor = new Color(
                                Math.max(0, Math.min(255, red / count)),
                                Math.max(0, Math.min(255, green / count)),
                                Math.max(0, Math.min(255, blue / count)));
                    }
                    image.setRGB(x, y, mapTo8BitColor(finalColor).getRGB());
                }
            }
        }
    }

    private static class RenderTask implements Callable<RenderResult> {
        private final AppConfig config;
        private final int iterations;
        private final int width;
        private final int height;

        RenderTask(AppConfig config, int iterations, int width, int height) {
            this.config = config;
            this.iterations = iterations;
            this.width = width;
            this.height = height;
        }

        @Override
        public RenderResult call() {
            RenderResult localResult = new RenderResult(width, height);

            int[] hits = localResult.hits;
            int[] r = localResult.r;
            int[] g = localResult.g;
            int[] b = localResult.b;

            ThreadLocalRandom random = ThreadLocalRandom.current();

            double currentX = random.nextDouble(-1, 1);
            double currentY = random.nextDouble(-1, 1);

            List<AffineParameters> affineTransformations = config.affineParams();
            List<Function> functions = config.functions();
            int symmetry = config.symmetryLevel();
            int affineCount = affineTransformations.size();

            for (int i = -20; i < iterations; i++) {
                if (affineCount == 0) break;

                AffineParameters transform = affineTransformations.get(random.nextInt(affineCount));

                double nextX = transform.a() * currentX + transform.b() * currentY + transform.c();
                double nextY = transform.d() * currentX + transform.e() * currentY + transform.f();

                currentX = nextX;
                currentY = nextY;

                if (!functions.isEmpty()) {
                    double finalX = 0;
                    double finalY = 0;

                    Point pObj = new Point(currentX, currentY);

                    for (Function func : functions) {
                        Transformation nonLinear = TransformationFactory.getTransformation(func.name());
                        if (nonLinear != null) {
                            Point transformedPoint = nonLinear.apply(pObj);
                            double weight = func.weight();
                            finalX += transformedPoint.x() * weight;
                            finalY += transformedPoint.y() * weight;
                        }
                    }
                    currentX = finalX;
                    currentY = finalY;
                }

                if (i >= 0) {
                    double theta = 0.0;
                    for (int s = 0; s < symmetry; s++) {
                        theta += 2 * Math.PI / symmetry;

                        double rotX = currentX * Math.cos(theta) - currentY * Math.sin(theta);
                        double rotY = currentX * Math.sin(theta) + currentY * Math.cos(theta);

                        int scrX = (int) ((rotX + 1.5) * width / 3.0);
                        int scrY = (int) ((rotY + 1.5) * height / 3.0);

                        if (scrX >= 0 && scrX < width && scrY >= 0 && scrY < height) {
                            int index = scrY * width + scrX;

                            Color c = transform.color();
                            r[index] += c.getRed();
                            g[index] += c.getGreen();
                            b[index] += c.getBlue();
                            hits[index]++;
                        }
                    }
                }
            }
            return localResult;
        }
    }
}
