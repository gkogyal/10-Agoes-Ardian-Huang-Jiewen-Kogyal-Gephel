package testing;

import sha.SHA256;

import java.security.MessageDigest;
import java.security.SecureRandom;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;

public class TestSHA256  extends TestSuite {

	public static void main(String[] args) {
		new TestSHA256().executeSuite("SHA-256");
	}

	@Override
	protected void runTest(String input, int N) {
		byte[] inputBytes = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);

		String label = TestUtils.preview(input);

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

	private static byte[] javaHash(byte[] input) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(input);
		}
		catch (Exception e) {
			System.out.println("[ERROR] MessageDigest: " + e.getMessage());
			return null;
		}
	}
}
