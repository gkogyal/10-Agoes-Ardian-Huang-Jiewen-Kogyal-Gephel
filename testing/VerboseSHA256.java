package testing;

import java.util.Scanner;
import sha.SHA256;

public class VerboseSHA256 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		TestUtils.section("SHA-256 Verbose Trace");
		System.out.print("  Enter plaintext: ");
		String plaintext = sc.nextLine();
		SHA256.verbose = true;
		System.out.println("STARTING SHA-256 HASHING");
		byte[] digest = SHA256.hash(plaintext.getBytes());
		System.out.println("  Digest (hex): " + TestUtils.toHex(digest));
	}

}
