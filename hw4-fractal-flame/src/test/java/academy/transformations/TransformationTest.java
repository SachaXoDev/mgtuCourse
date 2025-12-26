package academy.transformations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TransformationTest {

    private static final double DELTA = 1e-9;

    @Test
    void testLinearTransformation() {
        Point p = new Point(0.5, -0.5);
        Point result = FractalVariations.linear(p);
        assertEquals(0.5, result.x(), DELTA);
        assertEquals(-0.5, result.y(), DELTA);
    }

    @Test
    void testSinusoidalTransformation() {
        Point p = new Point(0.5, -0.5);
        Point result = FractalVariations.sinusoidal(p);
        assertEquals(Math.sin(0.5), result.x(), DELTA);
        assertEquals(Math.sin(-0.5), result.y(), DELTA);
    }

    @Test
    void testSphericalTransformation() {
        Point p = new Point(0.5, -0.5);
        Point result = FractalVariations.spherical(p);
        double r2 = 0.5 * 0.5 + (-0.5) * (-0.5);
        assertEquals(0.5 / r2, result.x(), DELTA);
        assertEquals(-0.5 / r2, result.y(), DELTA);
    }

    @Test
    void testSwirlTransformation() {
        Point p = new Point(0.5, -0.5);
        Point result = FractalVariations.swirl(p);
        double r2 = 0.5 * 0.5 + (-0.5) * (-0.5);
        assertEquals(0.5 * Math.sin(r2) - (-0.5) * Math.cos(r2), result.x(), DELTA);
        assertEquals(0.5 * Math.cos(r2) + (-0.5) * Math.sin(r2), result.y(), DELTA);
    }

    @Test
    void testHorseshoeTransformation() {
        Point p = new Point(0.5, -0.5);
        Point result = FractalVariations.horseshoe(p);
        double r = Math.sqrt(0.5 * 0.5 + (-0.5) * (-0.5));
        double expectedX = (0.5 * 0.5 - (-0.5) * (-0.5)) / r;
        double expectedY = (2 * 0.5 * -0.5) / r;
        assertEquals(expectedX, result.x(), DELTA);
        assertEquals(expectedY, result.y(), DELTA);
    }

    @Test
    void testPolarTransformation() {
        Point p = new Point(0.5, -0.5);
        Point result = FractalVariations.polar(p);
        double r = Math.sqrt(0.5 * 0.5 + (-0.5) * (-0.5));
        double theta = Math.atan2(-0.5, 0.5);
        assertEquals(theta / Math.PI, result.x(), DELTA);
        assertEquals(r - 1, result.y(), DELTA);
    }
}
