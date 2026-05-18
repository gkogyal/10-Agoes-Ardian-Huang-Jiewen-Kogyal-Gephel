package testing;

import chacha20.ChaCha20;
import chacha20.Constants;
import chacha20.QuarterRound;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestChaCha20 {

	private static int passed = 0;
	private static int failed = 0;
	private static final SecureRandom RNG = new SecureRandom();

	public static void main(String[] args) {


		section("ChaCha20 Test Suite");

		// wip: add ability to use multiple flags to pick which cases: (s,r,c) + (sr,sc,rc) + (src)
		String[] staticCases = staticCases();
		String[] randomCases = randomCases();
		String[] customCases = customCases();

		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (String str: staticCases) runTest(str);


		section("RANDOM TESTS (" + randomCases.length + " cases)");
		for (String str: randomCases) runTest(str);
		
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

		String[] randomStrings = new String[10];

		for(int i = 0; i<10; i++ ) {
			int length = random.nextInt(21) + 10;
			String randomString = RandomStringUtils.random(length);
			randomStrings[i] = randomString;
		}
		
		return randomStrings;
	}


	private static String[] customCases() {
		// should quit asking for more only if given just "QUIT"

		ArrayList<String> customCases = 

		while(true) {

			// prompt with section()

			// prompt with
			
		}

		return customCases.toArray(new String[0]);
	}



	private static void runTest(String plaintext) {
		
		
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
    
}
