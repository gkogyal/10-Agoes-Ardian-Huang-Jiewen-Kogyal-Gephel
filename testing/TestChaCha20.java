package testing;

<<<<<<< HEAD
<<<<<<< HEAD
import chacha.ChaCha20;
=======
<<<<<<< HEAD
//import chacha20.ChaCha20;
//import chacha20.Constants;
//import chacha20.QuarterRound;
=======
import chacha.ChaCha20;
>>>>>>> 7cdd7068b1d379e04e0aeefff26834c23fdbb48e
>>>>>>> a7bb13c89c03b356d712736b51aa63ccd500d292
=======
import chacha.ChaCha20;
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

<<<<<<< HEAD
<<<<<<< HEAD
=======
<<<<<<< HEAD
//import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

=======
>>>>>>> 7cdd7068b1d379e04e0aeefff26834c23fdbb48e
>>>>>>> a7bb13c89c03b356d712736b51aa63ccd500d292
=======
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestChaCha20 {

	private static int passed = 0;
	private static int failed = 0;
	private static final SecureRandom RNG = new SecureRandom();

	public static void main(String[] args) {


		section("ChaCha20 Test Suite");

		String[] staticCases = staticCases();
		String[] randomCases = randomCases();
		String[] customCases = customCases();

		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (String str: staticCases) runTest(str);


		section("RANDOM TESTS (" + randomCases.length + " cases)");
		for (String str: randomCases) runTest(str);
<<<<<<< HEAD

=======
		
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
		section("CUSTOM TESTS (" + customCases.length + " cases)");
		for (String str: customCases) runTest(str);

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
<<<<<<< HEAD

=======
	
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
		Scanner sc = new Scanner(System.in);
		System.out.println("\nHow many random test cases? ");

		int count = 10;
		try {count = Integer.parseInt(sc.nextLine().trim());}
		catch (Exception e) {System.out.println("Invalid number, defaulting to 10.");}
<<<<<<< HEAD

<<<<<<< HEAD

=======
<<<<<<< HEAD
		for(int i = 0; i<10; i++ ) {
			//int length = random.nextInt(21) + 10;
			//String randomString = RandomStringUtils.random(length);
			//randomStrings[i] = randomString;
=======
>>>>>>> a7bb13c89c03b356d712736b51aa63ccd500d292
=======
		

>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
		String[] randomStrings = new String[count];

		for(int i = 0; i<count; i++ ) {
			int length = RNG.nextInt(291) + 10; // 10-300 chars

			byte[] bytes = new byte[length];
			for (int j = 0; j < length; j++) {
				bytes[j] = (byte)(32 + RNG.nextInt(95));
			}

<<<<<<< HEAD
<<<<<<< HEAD
			randomStrings[i] = new String(bytes, java.nio.charset.StandardCharsets.US_ASCII);
=======
			randomStrings[i] = new String(bytes, java.nio.charset.StandardCharsets.US_ASCII);	
>>>>>>> 7cdd7068b1d379e04e0aeefff26834c23fdbb48e
>>>>>>> a7bb13c89c03b356d712736b51aa63ccd500d292
		}

=======
			randomStrings[i] = new String(bytes, java.nio.charset.StandardCharsets.US_ASCII);	
		}
		
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
		return randomStrings;
	}


	private static String[] customCases() {
		// should quit asking for more only if given just "QUIT"

		ArrayList<String> customCases = new ArrayList<String>();
		Scanner scn =  new Scanner(System.in);

		while(true) {

			System.out.println("=".repeat(30));
			System.out.print("Enter your input (QUIT to escape): ");
<<<<<<< HEAD
<<<<<<< HEAD
			String line = scn.nextLine();
=======
<<<<<<< HEAD
			customCases.add(scn.nextLine());
=======
			String line = scn.nextLine();
>>>>>>> 7cdd7068b1d379e04e0aeefff26834c23fdbb48e
>>>>>>> a7bb13c89c03b356d712736b51aa63ccd500d292
=======
			String line = scn.nextLine();
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
			System.out.println("=".repeat(30) + "\n");

			if (line.equals("QUIT")) break;
			customCases.add(line);
		}

		return customCases.toArray(new String[0]);
	}


<<<<<<< HEAD

=======
	
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
	private static void runTest(String plaintext) {
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
		printResult(label + " [ciphertext vs javax.crypto]", Arrays.equals(jaCha,myCha), ChaCha20.toHex(jaCha), ChaCha20.toHex(myCha));

		// decrypting
		byte[] recovered = ChaCha20.xcrypt(myCha, key, nonce, 1);

		printResult(label + " [decrypting]", Arrays.equals(plaintextBytes, recovered), plaintext, new String(recovered, java.nio.charset.StandardCharsets.UTF_8));
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

	private static void printResult(String label, boolean ok, String exp, String got) {
		if (ok) {
			System.out.printf("[PASS] %s%n", label);
			passed++;
		}
		else {
			System.out.printf("[FAIL] %s%n", label);
			System.out.printf("	  exp: %s%n", truncate(exp, 80));
			System.out.printf("	  got: %s%n", truncate(got, 80));
			failed++;
		}
	}

	private static void section(String str, int N) {
		N = Math.max(N,str.length()+10);

		String div = "=".repeat(N);
		String mid = " ".repeat((N - str.length())/2);

		System.out.print(div + "\n" + str + "\n" + div);
	}

	private static void section(String str) {
		section(str,Math.max(30,str.length()+10));
	}

	private static String preview(String s) {
		if (s.isEmpty()) return "<empty>";

		String clean = s.replaceAll("[\\r\\n\\t]", " ");
		return clean.length() <= 30 ? "\"" + clean + "\"" : "\"" + clean.substring(0, 27) + "...\"";
	}
<<<<<<< HEAD

	private static String truncate(String s, int max) {
		return s.length() <= max ? s : s.substring(0, max) + "…";
	}

}
=======
	
	private static String truncate(String s, int max) {
		return s.length() <= max ? s : s.substring(0, max) + "…";
	}
    
}
>>>>>>> 4bc4d1471c27eb0ef7c7a7118bfdb9a4dacd60ee
