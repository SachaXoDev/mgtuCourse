package academy.model;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

public record AffineParameters(double a, double b, double c, double d, double e, double f, Color color) {
    public AffineParameters(double a, double b, double c, double d, double e, double f) {
        this(a, b, c, d, e, f, new Color(ThreadLocalRandom.current().nextInt(0x1000000)));
    }
}
