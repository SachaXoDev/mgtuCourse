package academy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TestRunner {

    private static final String RED = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String BLUE = "\033[0;34m";
    private static final String NC = "\033[0m"; // No Color

    public static void main(String[] args) {
        System.out.println(BLUE + "Starting tests..." + NC);

        boolean basicTestOk = runBasicFunctionalityTest();
        if (!basicTestOk) {
            System.out.println(RED + "Basic functionality test failed. Aborting further tests." + NC);
            System.exit(1);
        }

        boolean imagePropsTestOk = runImagePropertiesTest();
        if (!imagePropsTestOk) {
            System.out.println(RED + "Image properties test failed. Aborting further tests." + NC);
            System.exit(1);
        }

        boolean performanceTestOk = runMultithreadingPerformanceTest();
        if (!performanceTestOk) {
            System.out.println(RED + "Multithreading performance test failed." + NC);
            System.exit(1);
        }

        System.out.println(GREEN + "All tests passed successfully!" + NC);
    }

    private static boolean runBasicFunctionalityTest() {
        System.out.println(YELLOW + "--- Running Basic Functionality Test ---" + NC);
        try {
            String[] appArgs = {"-w", "800", "-h", "600", "-o", "test_output.png"};
            Application.main(appArgs);

            File imageFile = new File("test_output.png");
            if (imageFile.exists()) {
                System.out.println(GREEN + "Image file 'test_output.png' was created." + NC);
                return true;
            } else {
                System.out.println(RED + "Image file 'test_output.png' was not created." + NC);
                return false;
            }
        } catch (Exception e) {
            System.out.println(RED + "Application failed with an exception: " + e.getMessage() + NC);
            e.printStackTrace();
            return false;
        }
    }

    private static boolean runImagePropertiesTest() {
        System.out.println(YELLOW + "--- Running Image Properties Test ---" + NC);
        File imageFile = new File("test_output.png");

        // Check extension
        if (!imageFile.getName().endsWith(".png")) {
            System.out.println(RED + "Image file does not have .png extension." + NC);
            return false;
        }
        System.out.println(GREEN + "Image file has .png extension." + NC);

        // Check file size
        long fileSize = imageFile.length();
        if (fileSize <= 0) {
            System.out.println(RED + "Image file is empty." + NC);
            return false;
        }
        System.out.println(GREEN + "Image file has content (size: " + fileSize + " bytes)." + NC);

        // Check PNG signature
        byte[] expectedSignature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        try (InputStream is = new FileInputStream(imageFile)) {
            byte[] actualSignature = new byte[8];
            if (is.read(actualSignature) == 8 && Arrays.equals(expectedSignature, actualSignature)) {
                System.out.println(GREEN + "Image file has valid PNG signature." + NC);
            } else {
                System.out.println(RED + "Image file does not have valid PNG signature." + NC);
                return false;
            }
        } catch (IOException e) {
            System.out.println(RED + "Error reading image file: " + e.getMessage() + NC);
            return false;
        }

        return true;
    }

    private static boolean runMultithreadingPerformanceTest() {
        System.out.println(YELLOW + "--- Running Multithreading Performance Test ---" + NC);
        System.out.println("threads,duration_seconds");

        for (int threads : new int[] {1, 2, 4}) {
            try {
                String outputFile = "test_output_" + threads + "_threads.png";
                String[] appArgs = {"-w", "1920", "-h", "1080", "-t", String.valueOf(threads), "-o", outputFile};

                long startTime = System.nanoTime();
                Application.main(appArgs);
                long endTime = System.nanoTime();

                long duration = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
                System.out.println(threads + "," + duration);

            } catch (Exception e) {
                System.out.println(RED + "Performance test failed for " + threads + " threads with an exception: "
                        + e.getMessage() + NC);
                e.printStackTrace();
                return false;
            }
        }
        System.out.println(GREEN + "Multithreading performance test completed." + NC);
        return true;
    }
}
