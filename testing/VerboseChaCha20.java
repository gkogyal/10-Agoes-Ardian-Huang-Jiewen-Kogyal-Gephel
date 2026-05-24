package testing;
 
import chacha.ChaCha20;
 
import java.security.SecureRandom;
import java.util.Scanner;

public class VerboseChaCha20 {

	public static void main(String[] args) {

		// 1. get input string and setup
		
		Scanner sc = new Scanner(System.in);

		section("ChaCha20 Verbose Testing");

		System.out.print("  Enter plaintext: ");
		String plaintext = sc.nextLine();

		

		// 2. generate random key and nonce
		SecureRandom rng = new SecureRandom();
		byte[] key = new byte[32];
		byte[] nonce = new byte[12];

		rng.nextBytes(key);
		rng.nextBytes(nonce);

		byte[] plaintextBytes = plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8);


		// 3. print the setup


		section("Value Preview Summary");

		
		System.out.println("   Plaintext    | \"" + plaintext + "\"");
		System.out.println("    Length      | " + plaintextBytes.length + " bytes");
		System.out.println("    Key (hex)   | " + ChaCha20.toHex(key)   + " [32 random bytes]");
		System.out.println("   Nonce (hex)  | " + ChaCha20.toHex(nonce) + " [12 random bytes]");
		System.out.println("  Init Counter  | 1  (IETF RFC 8439 standard)");
		
		System.out.println();

		// 4. encrypt trace

		section("Encryption Trace");

		ChaCha20.verbose = false;

		byte[] ciphertext = ChaCha20.xcrypt(plaintextBytes, key, nonce, 1);

		ChaCha20.verbose = false;

		System.out.println();

		section("Encryption Result");

		System.out.println("  Ciphertext (hex): " + ChaCha20.toHex(ciphertext));


		// 5. decrypt trace

		section("Decryption Trace");

		ChaCha20.verbose = true;

		byte[] recovered = ChaCha20.xcrypt(ciphertext, key, nonce, 1);

		ChaCha20.verbose = false;

		// 6. final result


		section("Final Result");

		String recoveredStr = new String(recovered, java.nio.charset.StandardCharsets.UTF_8);
		boolean recoveredQ = plaintext.equals(recoveredStr);

		System.out.println("  Original  : \"" + plaintext    + "\"");
		System.out.println("  Recovered : \"" + recoveredStr + "\"");
		System.out.println();

		if (match) {
			System.out.println(" [PASS] Successful recovery.");
		}
		 else {
		 	System.out.println(" [FAIL] Failed recovery.");
		 }


		 System.out.println();
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


}
