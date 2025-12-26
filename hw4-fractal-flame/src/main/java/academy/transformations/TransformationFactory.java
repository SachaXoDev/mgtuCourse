package academy.transformations;

import java.util.HashMap;
import java.util.Map;

public class TransformationFactory {
    private static final Map<String, Transformation> transformations = new HashMap<>();

    static {
        transformations.put("linear", FractalVariations::linear);
        transformations.put("sinusoidal", FractalVariations::sinusoidal);
        transformations.put("spherical", FractalVariations::spherical);
        transformations.put("swirl", FractalVariations::swirl);
        transformations.put("horseshoe", FractalVariations::horseshoe);
        transformations.put("polar", FractalVariations::polar);
    }

    public static Transformation getTransformation(String name) {
        return transformations.get(name.toLowerCase());
    }
}
