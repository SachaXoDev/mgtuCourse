package academy.transformations;

public class FractalVariations {

    public static Point linear(Point p) {
        return new Point(p.x(), p.y());
    }

    public static Point sinusoidal(Point p) {
        return new Point(Math.sin(p.x()), Math.sin(p.y()));
    }

    public static Point spherical(Point p) {
        double r2 = p.x() * p.x() + p.y() * p.y();
        if (r2 == 0) return new Point(0, 0);
        return new Point(p.x() / r2, p.y() / r2);
    }

    public static Point swirl(Point p) {
        double r2 = p.x() * p.x() + p.y() * p.y();
        double sinR2 = Math.sin(r2);
        double cosR2 = Math.cos(r2);

        double newX = p.x() * sinR2 - p.y() * cosR2;
        double newY = p.x() * cosR2 + p.y() * sinR2;
        return new Point(newX, newY);
    }

    public static Point horseshoe(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        if (r == 0) return new Point(0, 0);

        double newX = (p.x() * p.x() - p.y() * p.y()) / r;
        double newY = 2 * p.x() * p.y() / r;
        return new Point(newX, newY);
    }

    public static Point polar(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());

        return new Point(theta / Math.PI, r - 1);
    }
}
