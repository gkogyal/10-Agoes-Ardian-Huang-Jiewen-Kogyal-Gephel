package testing;
import sha.SHA256;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
public class TestSHA256 {
    private static int passed = 0;
    private static int failed = 0;
    private static final SecureRandom RNG = new SecureRandom();
    public static void main(String[] args) {
        section("SHA-256 Test Suite");
        String[] staticCases = staticCases();
        String[] randomCases = randomCases();
        String[] customCases = customCases();
        section("STATIC TESTS (" + staticCases.length + " cases)");
        for (String str : staticCases) runTest(str);
        section("RANDOM TESTS (" + randomCases.length + " cases)");
        for (String str : randomCases) runTest(str);
        section("CUSTOM TESTS (" + customCases.length + " cases)");
        for (String str : customCases) runTest(str);
        section("Passed: " + passed + "   Failed: " + failed);
    }

    private static void section(String header) {
        System.out.println();
        System.out.println("--- " + header + " ---");
    }

    private static void runTest(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        String ours = toHex(SHA256.hash(inputBytes));
        String expected;
        try {
            expected = toHex(MessageDigest.getInstance("SHA-256").digest(inputBytes));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            failed++;
            return;
        }
        boolean pass = ours.equals(expected);
        String label;
        if (input.length()>40) {
            label = input.substring(0, 37) + "...";
        } else {
            label = input;
        }
        String result;
        if (pass) {
            result = "PASS";
        } else {
            result ="FAIL";
        }
        System.out.println(result + " (" + inputBytes.length + " bytes) \"" + label + "\"");
        if (!pass) {
            System.out.println(" expected: " + expected);
            System.out.println(" ours: " + ours);
        }
        if (pass) {
            passed++;
        } else {
            failed++;
        }
    }
    private static String[] staticCases() {
        return new String[]{
                "",
                "a", //exactly one byte
                "abc", 
                "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
                "The quick brown fox jumps over the lazy dog",
                "!@#$%^&*()_+-=[]{}|;':\",./<>?",//for special chars
                "A".repeat(55), 
                "A".repeat(56),
                "A".repeat(64),//exactly 1 block of input
                "A".repeat(1000)
        };
    }
}

