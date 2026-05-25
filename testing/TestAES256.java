package testing;

import aes.AES256;
import aes.AESKeySchedule;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class TestAES256 extends TestSuite {

	public static void main(String[] args) {
		new TestAES256().executeSuite("AES-256");
	}

	@Override
	protected void runTest(String plaintext, int N) {

		byte[] key = new byte[32];
		TestUtils.RNG.nextBytes(key);

		byte[] ptBytes = plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		String label = TestUtils.preview(plaintext);

		AES256 aes = new AES256();
		int[] eKey = AESKeySchedule.expandKey(key);

		byte[] myAES = aes.encrypt(ptBytes, eKey);
		byte[] javaAES = javaEncrypt(ptBytes, key);

		if (javaAES != null) {
			printResult(label + " [ciphertext vs javax.crypto]", Arrays.equals(javaAES, myAES), TestUtils.toHex(javaAES), TestUtils.toHex(myAES), N);
		} else {
			System.out.printf(" [SKIP] %s — javax.crypto unavailable%n", label);
		}


		byte[] recovered = aes.decrypt(myAES, eKey);
		printResult(label + " [round-trip decrypt]", Arrays.equals(ptBytes, recovered), plaintext, new String(recovered, java.nio.charset.StandardCharsets.UTF_8), N + 1);
	}


	private static byte[] javaEncrypt(byte[] pt, byte[] key) {
		try {
			SecretKeySpec ks = new SecretKeySpec(key, "AES");
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, ks);
			return c.doFinal(pt);
		} catch (Exception e) {
			System.out.println(" [ERROR] javax.crypto: " + e.getMessage());
			return null;
		}
	}
}
