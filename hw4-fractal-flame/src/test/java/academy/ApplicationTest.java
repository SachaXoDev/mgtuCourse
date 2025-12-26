package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.model.AffineParameters;
import academy.model.Function;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void testParseFunctions() throws Exception {
        Application app = new Application();
        Method parseFunctions = Application.class.getDeclaredMethod("parseFunctions", String.class);
        parseFunctions.setAccessible(true);

        String input = "swirl:1.0,horseshoe:0.8,polar:0.5";
        List<Function> result = (List<Function>) parseFunctions.invoke(app, input);

        assertEquals(3, result.size());
        assertEquals("swirl", result.get(0).name());
        assertEquals(1.0, result.get(0).weight());
        assertEquals("horseshoe", result.get(1).name());
        assertEquals(0.8, result.get(1).weight());
        assertEquals("polar", result.get(2).name());
        assertEquals(0.5, result.get(2).weight());
    }

    @Test
    void testParseAffineParams() throws Exception {
        Application app = new Application();
        Method parseAffineParams = Application.class.getDeclaredMethod("parseAffineParams", String.class);
        parseAffineParams.setAccessible(true);

        String input = "0.1,0.2,0.3,0.4,0.5,0.6/0.7,0.8,0.9,1.0,1.1,1.2";
        List<AffineParameters> result = (List<AffineParameters>) parseAffineParams.invoke(app, input);

        assertEquals(2, result.size());
        assertEquals(0.1, result.get(0).a());
        assertEquals(0.6, result.get(0).f());
        assertEquals(0.7, result.get(1).a());
        assertEquals(1.2, result.get(1).f());
    }

    @Test
    void testParseFunctions_invalidFormat() throws Exception {
        Application app = new Application();
        Method parseFunctions = Application.class.getDeclaredMethod("parseFunctions", String.class);
        parseFunctions.setAccessible(true);

        String input = "swirl:1.0,horseshoe";
        assertThrows(Exception.class, () -> {
            try {
                parseFunctions.invoke(app, input);
            } catch (Exception e) {
                throw e.getCause();
            }
        });
    }
}
