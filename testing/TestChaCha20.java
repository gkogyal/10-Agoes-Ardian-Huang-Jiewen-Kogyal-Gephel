package testing;

import chacha20.ChaCha20;
import chacha20.Constants;
import chacha20.QuarterRound;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestChaCha20 {

	private static int passed = 0;
	private static int failed = 0;
	private static final SecureRandom RNG = new SecureRandom();

	public static void main(String[] args) {

		System.out.println("================================================");
		System.out.println("|>>>>          ChaCha20 Test Suite         <<<<|");
		System.out.println("================================================");


		String[] staticCases = staticCases();
		String[] randomCases = randomCases();
		String[] customCases = customCases();

		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (String str: staticCases) runTest(str);


		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (String str: randomCases) runTest(str);
		
		section("STATIC TESTS (" + staticCases.length + " cases)");
		for (String str: staticCases) runTest(str);

		System.out.println("================================================");
		System.out.printf( "             Passed: %d Failed %d", passed, failed);
		System.out.println("================================================");
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


		
	}



	private static void runTest(String plaintext) {


	}

	
    
}
