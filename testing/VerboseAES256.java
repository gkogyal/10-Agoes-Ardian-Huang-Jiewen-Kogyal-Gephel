package testing;
 
import aes.AES256;
import aes.AESKeySchedule;
 
import java.security.SecureRandom;
import java.util.Scanner;

public class VerboseAES256 {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		TestUtils.section("AES-256 Verbose Trace");

		System.out.print("  Enter plaintext: ");
		String plaintext = sc.nextLine();    
	}

}
