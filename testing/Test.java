package testing;

import java.util.Scanner;

public class Test {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		TestUtils.section("Cryptography Test Runner");

		System.out.println("Select an algorithm to test (QUIT to escape):");
			
		System.out.println("  1. SHA-256");
		System.out.println("  2. AES-256");
		System.out.println("  3. ChaCha20\n");

		while (true) {
			TestSuite suite = null;
			String suiteName = "";

			System.out.print("Enter choice (1-3; QUIT): ");

			String choice = sc.nextLine().trim();
	
			switch (choice) {

				case "1":
					System.out.println();
					suite = new TestSHA256();
					suiteName = "SHA-256";
					break;

				case "2":
					System.out.println();
					suite = new TestAES256();
					suiteName = "AES-256";
					break;

				case "3":
					System.out.println();
					suite = new TestChaCha20();
					suiteName = "ChaCha20";
					break;
				case "QUIT":
					System.out.println("[QUIT] Exiting test runner.\n");
					return;
				default:
					System.out.println("[ERROR] Invalid choice. Retry.\n");
					break;
				
			}

			if(suite!=null) {
				suite.executeSuite(suiteName);
			}

			
		}
	}


}
