package testing;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HexFormat;
import java.security.SecureRandom;

public class TestUtils {

	// TEST SUITES

	public static final SecureRandom RNG = new SecureRandom();
	private static final Scanner sc = new Scanner(System.in);

	public static String[] staticCases() {
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

	public static String[] randomCases() {

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


	public static String[] customCases() {
		// should quit asking for more only if given just "QUIT"

		ArrayList<String> customCases = new ArrayList<String>();
		Scanner sc = new Scanner(System.in);
		
		System.out.println("\nEnter inputs below (QUIT to escape):");

		int i = 0;
		while(true) {

			System.out.print("  " + (i+1) + ": ");
			String line = sc.nextLine();

			if (line.equals("QUIT")) break;
			customCases.add(line); i++;
		}

		System.out.print("\n");
		
		return customCases.toArray(new String[0]);
	}


	// PRINTING

	public static String line(int N) {
		return "=".repeat(N);
	}

	public static void section(String a, String b, String c) {
		System.out.print(a + b + c);
	}

	public static void section(String str, int N) {
		N = Math.max(N,str.length()+10);

		String div = line(N) + "\n";
		String mid = " ".repeat((N - str.length())/2);

		System.out.print(div + mid + str + "\n" + div + "\n");
	}

	public static void section(String str) {
		section(str,Math.max(30,str.length()+10));
	}

	public static String preview(String s) {
		if (s.isEmpty()) return "<empty>";

		String clean = s.replaceAll("[\\r\\n\\t]", " ");
		return clean.length() <= 30 ? "\"" + clean + "\"" : "\"" + clean.substring(0, 27) + "...\"";
	}

	public static String truncate(String s, int max) {
		return s.length() <= max ? s : s.substring(0, max) + "…" + " ( " + (max-s.length()) + "more)";
	}

	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) sb.append(String.format("%02x", b & 0xFF));
		return sb.toString();
	}
}
