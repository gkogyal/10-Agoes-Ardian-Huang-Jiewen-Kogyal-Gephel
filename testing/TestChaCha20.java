package testing;

import chacha.ChaCha20;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestChaCha20 {

	private static int passed = 0;
	private static int failed = 0;
	private static final SecureRandom RNG = new SecureRandom();

	public static void main(String[] args) {

		section("ChaCha20 Test Suite");
		//section(line(30),"     ChaCha20 Test Suite",line(30) + "\n");

		String[] staticCases = staticCases();
		String[] randomCases = randomCases();
		String[] customCases = customCases();

		section("STATIC TESTS (" + staticCases.length + " cases)");
		// for (String str: staticCases) runTest(str);
		for (int i  = 0; i<staticCases.length; i++) {
			runTest(staticCases[i],2*i);
		}

		System.out.print("\n");

		section("RANDOM TESTS (" + randomCases.length + " cases)");
		//for (String str: randomCases) runTest(str);
		for (int i  = 0; i<randomCases.length; i++) {
			runTest(randomCases[i],2*i);
		}
		
		System.out.print("\n");
		
		section("CUSTOM TESTS (" + customCases.length + " cases)");
		//for (String str: customCases) runTest(str);
		for (int i  = 0; i<customCases.length; i++) {
			runTest(customCases[i],2*i);
		}

		System.out.print("\n");
		
		section("Passed: " + passed  + "   Failed " + failed);
    }


	private static String[] staticCases() {
		return new String[]{
			"Hello, ChaCha20!",
			"The quick brown fox jumps over the lazy dog",
			"",									// empty char
			"A",								// 1 char
			"A".repeat(64),						// 1 blocks
			"A".repeat(65),						// 1 block + 1 byte
			"A".repeat(200),					// multiple blocks
			"!@#$%^&*()_+-=[]{}|;':\",./<>?",	// speciual characters
			"1234567890".repeat(10)				// 100 digits
		};
	}

	private static String[] randomCases() {

		Scanner sc = new Scanner(System.in);
		System.out.print("How many random test cases?: ");

		int count = 10;
		try {count = Integer.parseInt(sc.nextLine().trim());}
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
		Scanner scn =  new Scanner(System.in);
		
		System.out.println("\nEnter inputs below (QUIT to escape):");

		int i = 0;
		while(true) {

			System.out.print("  " + (i+1) + ": ");
			String line = scn.nextLine();

			if (line.equals("QUIT")) break;
			customCases.add(line); i++;
		}

		System.out.print("\n");
		
		return customCases.toArray(new String[0]);
	}

	private static void runTest(String plaintext, int N) {
		byte[] key = new byte[32];
		byte[] nonce = new byte[12];
		RNG.nextBytes(key);
		RNG.nextBytes(nonce);

		byte[] plaintextBytes = plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8);

		String label = preview(plaintext);

		// javax.crypto used for chacha20
		byte[] jaCha = javaEncrypt(plaintextBytes, key, nonce);
		if (jaCha == null) {
			System.out.printf("[SKIP] %s — javax.crypto unavailable%n", label);
			return;
		}

		// my chacha20
		byte[] myCha = ChaCha20.xcrypt(plaintextBytes, key, nonce, 1);

		// printing results
		printResult(label + " [ciphertext vs javax.crypto]", Arrays.equals(jaCha,myCha), ChaCha20.toHex(jaCha), ChaCha20.toHex(myCha), N);

		// decrypting
		byte[] recovered = ChaCha20.xcrypt(myCha, key, nonce, 1);

		printResult(label + " [decrypting]", Arrays.equals(plaintextBytes, recovered), plaintext, new String(recovered, java.nio.charset.StandardCharsets.UTF_8), N+1);
	}

	private static byte[] javaEncrypt(byte[] plaintext, byte[] key, byte[] nonce) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");
			ChaCha20ParameterSpec params = new ChaCha20ParameterSpec(nonce, 1);
			Cipher cipher = Cipher.getInstance("ChaCha20");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, params);
			return cipher.doFinal(plaintext);
		}
		catch (Exception e) {
			System.out.println("[ERRORR] javax.crypto: " + e.getMessage());
			return null;
		}
	}

	private static void printResult(String label, boolean ok, String exp, String got, int N) {
		String nu = (N/2) + (N%2==0 ? "a" : "b");
		if (ok) {
			System.out.printf(nu + ": [PASS] %s%n", label);
			passed++;
		}
		else {
			System.out.printf(nu + ": [FAIL] %s%n", label);
			System.out.printf("	  exp: %s%n", truncate(exp, 80));
			System.out.printf("	  got: %s%n", truncate(got, 80));
			failed++;
		}
	}

	private static String line(int N) {
		return "=".repeat(N);
	}

	private static void section(String a, String b, String c) {
		System.out.print(a + b + c);
	}

	private static void section(String str, int N) {
		N = Math.max(N,str.length()+10);

		String div = line(N) + "\n";
		String mid = " ".repeat((N - str.length())/2);

		System.out.print(div + mid + str + "\n" + div + "\n");
	}

	private static void section(String str) {
		section(str,Math.max(30,str.length()+10));
	}

	private static String preview(String s) {
		if (s.isEmpty()) return "<empty>";

		String clean = s.replaceAll("[\\r\\n\\t]", " ");
		return clean.length() <= 30 ? "\"" + clean + "\"" : "\"" + clean.substring(0, 27) + "...\"";
	}

	private static String truncate(String s, int max) {
		return s.length() <= max ? s : s.substring(0, max) + "…" + " ( " + (max-s.length()) + "more)";
	}

}
