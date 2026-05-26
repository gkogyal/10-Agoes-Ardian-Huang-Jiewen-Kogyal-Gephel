package testing;
 
import aes.AES256;
import aes.AESKeySchedule;
import aes.AESCipher;
import java.security.SecureRandom;
import java.util.Scanner;

public class VerboseAES256 {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		TestUtils.section("AES-256 Verbose Trace");

		System.out.print("  Enter plaintext: ");
		String plaintext = sc.nextLine();    

		AES256.verbose = true;

		byte[] key = new byte[32];
		new SecureRandom().nextBytes(key);
		int[] eKey = AESKeySchedule.expandKey(key);

		System.out.println("STARTING AES-256 ENCRYPTION");
		System.out.println("  AES-256 Key (hex): " + TestUtils.toHex(key));

		AESCipher aesCipher = new AESCipher();
		byte[] ciphertext = aesCipher.encrypt(plaintext.getBytes(), eKey);
		System.out.println("  Ciphertext (hex): " + TestUtils.toHex(ciphertext));

		System.out.println("STARTING AES-256 DECRYPTION");
		System.out.println("  AES-256 Key (hex): " + TestUtils.toHex(key));
		byte[] decrypted = aesCipher.decrypt(ciphertext, eKey);
		System.out.println("  Decrypted plaintext: " + new String(decrypted));
	}

}
