package testing;

import chacha.ChaCha20;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestChaCha20 extends TestSuite {

	public static void main(String[] args) {
		new TestChaCha20().executeSuite("ChaCha20");
	}

	@Override
	protected void runTest(String plaintext, int N) {
		byte[] key = new byte[32];
		byte[] nonce = new byte[12];
		TestUtils.RNG.nextBytes(key);
		TestUtils.RNG.nextBytes(nonce);

		byte[] plaintextBytes = plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8);

		String label = TestUtils.preview(plaintext);

		// javax.crypto used for chacha20
		byte[] javaCha = javaEncrypt(plaintextBytes, key, nonce);
		if (javaCha == null) {
			System.out.printf("[SKIP] %s — javax.crypto unavailable%n", label);
			return;
		}

		// my chacha20
		byte[] myCha = ChaCha20.xcrypt(plaintextBytes, key, nonce, 1);

		// printing results
		printResult(label + " [ciphertext vs javax.crypto]", Arrays.equals(javaCha,myCha), ChaCha20.toHex(javaCha), ChaCha20.toHex(myCha), N);

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
			System.out.println("[ERROR] javax.crypto: " + e.getMessage());
			return null;
		}
	}

}
