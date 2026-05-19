package testing;
import sha.SHA256;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.security.SecureRandom;

public class TESTSHA256 {
    private static int passed = 0;
	private static int failed = 0;
	private static final SecureRandom RNG = new SecureRandom();
	public static void main(String[] args) {
		section("SHA-256 Test Suite");
		String[] staticCases = staticCases();
		String[] randomCases = randomCases();
		String[] customCases = customCases();
		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (String str: staticCases) runTest(str);
		section("RANDOM TESTS (" + randomCases.length + " cases)");
		for (String str: randomCases) runTest(str);
		section("CUSTOM TESTS (" + customCases.length + " cases)");
		for (String str: customCases) runTest(str);
		section("Passed: " + passed  + "   Failed " + failed);
    }

}
