package testing;
import sha.SHA256;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Scanner;
public class TestSHA256 {
    private static int passed = 0;
    private static int failed = 0;
    private static final SecureRandom RNG = new SecureRandom();
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        section("SHA-256 Test Suite");
        String[] staticCases = staticCases();
        String[] randomCases = randomCases();
        String[] customCases = customCases();
		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (int i  = 0; i<staticCases.length; i++) {
			runTest(staticCases[i],2*i);
		}
		System.out.print("\n");
		section("RANDOM TESTS (" + randomCases.length + " cases)");
		for (int i  = 0; i<randomCases.length; i++) {
			runTest(randomCases[i],2*i);
		}
		System.out.print("\n");
		section("CUSTOM TESTS (" + customCases.length + " cases)");
		for (int i  = 0; i<customCases.length; i++) {
			runTest(customCases[i],2*i);
		}
		System.out.print("\n");
		section("Passed: " + passed  + "   Failed " + failed);
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

	private static String[] randomCases() {

		System.out.print("How many random test cases?: ");

		int count = 10;
		try {count = Integer.parseInt(SCANNER.nextLine().trim());}
		catch (Exception e) {System.out.println("Invalid number, defaulting to 10.");}


		String[] randomStrings = new String[count];

		for(int i = 0; i<count; i++ ) {
			int length = RNG.nextInt(291) + 10; // 10-300 chars

			byte[] bytes = new byte[length];
			for (int j = 0; j < length; j++) {
				bytes[j] = (byte)(32 + RNG.nextInt(95));
			}

			randomStrings[i] = new String(bytes, java.nio.charset.StandardCharsets.US_ASCII);
		}


		return randomStrings;
	}


	private static String[] customCases() {
		// should quit asking for more only if given just "QUIT"

		ArrayList<String> customCases = new ArrayList<String>();

		System.out.println("\nEnter inputs below (QUIT to escape):");

		int i = 0;
		while(true) {

			System.out.print("  " + (i+1) + ": ");
			String line = SCANNER.nextLine();

			if (line.equals("QUIT")) break;
			customCases.add(line); i++;
		}

		System.out.print("\n");

		return customCases.toArray(new String[0]);
	}

	private static void runTest(String input, int N) {
		byte[] inputBytes = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);

		String label = preview(input);

		// MessageDigest used for sha-256 reference
		byte[] javaSha = javaHash(inputBytes);
		if (javaSha == null) {
			System.out.printf("[SKIP] %s — MessageDigest unavailable%n", label);
			return;
		}

		// my sha-256
		byte[] mySha = SHA256.hash(inputBytes);

		// printing results
		printResult(label + " [hash vs MessageDigest]", Arrays.equals(javaSha,mySha), HexFormat.of().formatHex(javaSha), HexFormat.of().formatHex(mySha), N);
	}
