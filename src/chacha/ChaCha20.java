package chacha;

import java.security.SecureRandom;

public class ChaCha20 {

	/***********************************************/
	/********************* API *********************/
	/***********************************************/

	/*======== byte[] encrypt() ==========

	  Inputs:  byte[] plaintext		arbitray-length plaintext bytes
	  		   byte[] key			32-byte ChaCha20 key

	  Process:
	  		* Encrypts plaintext using ChaCha20.
			* Generate random nonce

	  Returns: byte[] output >> 12 byte nonce + ciphertext
	  ====================*/
	public static byte[] encrypt(byte[] plaintext, byte[] key) {
		
	}

	/*======== byte[] decrypt() ==========

	  Inputs:  byte[] ciphertext
	  		   byte[] key

	  Process:
	  		* X

	  Returns: byte[] X >> ANS
	  ====================*/	
	public static byte[] decrypt(byte[] ciphertext, byte[] key) {
		
	}

	/*======== byte[] xcrypt() ==========

	  Inputs:  byte[] input
	  		   byte[] key
	  		   byte[] nonce
	  		   int counter

	  Process:
	  		* X

	  Returns: byte[] X >> ANS
	  ====================*/	
	public static byte[] xcrypt(byte[] input, byte[] key, byte[] nonce, int counter) {
		
	}

	/***********************************************/
	/*************** CORE COMPONENTS ***************/
	/***********************************************/

	/*======== byte[] generateBlock() ==========

	  Inputs:  int[] initialState

	  Process:
	  		* X

	  Returns: byte[] X = ANS
	  ====================*/
	public static byte[] generateBlock(int[] initialState) {
		
	}

	/*======== int[] buildInitialState() ==========

	  Inputs:  byte[] key
	  		   byte[] nonce
	  		   int counter

	  Process:
	  		* X

	  Returns: X = ANS
	  ====================*/
	public static int[] buildInitialState(byte[] key, byte[] nonce, int counter) {
		
	}

	/**************************************************/
	/********************* INPUTS *********************/
	/**************************************************/

	/*======== byte[] generateNonce() ==========

	  Inputs:  N/A

	  Process:
	  		* X

	  Returns: byte[] = random 12 byte nonce
	  ====================*/
	private static byte[] generateNonce() {
		
	}

	/*======== void validateNonce() ==========

	  Inputs:  byte[] nonce

	  Process:
	  		* X

	  Returns: X = ANS
	  ====================*/
	private static void validateNonce(byte[] nonce) {
		
	}

	/*======== void validateKey() ==========

	  Inputs:  byte[] key

	  Process:
	  		* X

	  Returns: X = ANS
	  ====================*/
	private static void validateKey(byte[] key) {
		
	}
	
	/*****************************************************/
	/********************* UTILITIES *********************/
	/*****************************************************/

	/*======== byte[] littleEndianWord() ==========

	  Inputs:  byte[] src
	  		   int offset

	  Process:
	  		* X

	  Returns: X = ANS
	  ====================*/
	public static int littleEndianWord(byte[] src, int offset) {
		
	}

	/*======== byte[] wordsToBytes() ==========

	  Inputs:  int[] words

	  Process:
	  		* X

	  Returns: byte[] X = ANS
	  ====================*/
	public static byte[] wordsToBytes(int[] words) {
		
	}

	/*======== byte[] toHex() ==========

	  Inputs:  byte[] bytes

	  Process:
	  		* X

	  Returns: String X = ANS
	  ====================*/
	public static String toHex(byte[] bytes) {
		
	}

	/*======== byte[] fromHex() ==========

	  Inputs:  X

	  Process:
	  		* X

	  Returns: byte[] X = ANS
	  ====================*/
	public static byte[] fromHex(String hex) {
		
	}
	
}
