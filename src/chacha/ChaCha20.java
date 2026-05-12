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
	/******************* Key-Nonce ********************/
	/**************************************************/

	/*======== byte[] generateNonce() ==========

	  Inputs:  (none)

	  Process:
	  		* Allocate 12-byte array
	  		* Fill with cryptographically random bytes with SecureRandom

	  Returns: byte[] >> random 12 byte nonce
	  ====================*/
	private static byte[] generateNonce() {
		byte[] nonce = new byte[Constants.NONCE_BYTES];
		(new SecureRandom()).nextBytes(nonce); // (new SecureRandom()) is a temporary SecureRandom obj used to fill nonce
		return nonce;
	}

	/*======== void validateNonce() ==========

	  Inputs:  byte[] nonce		nonce to validate

	  Process:
	  		* Check if nonce is not null and exactly 12 bytes
	  		* Throw exception if fails

	  Returns: void >> (throws if invalid)
	  ====================*/
	private static void validateNonce(byte[] nonce) {
		if (nonce == null || nonce.length != Constants.NONCE_BYTES) {
			throw new IllegalArgumentException("ChaCha20 requires 12-byte nonce, got: " + (nonce == null ? "null" : nonce.length) );
		}
	}

	/*======== void validateKey() ==========

	  Inputs:  byte[] key	key to validate

	  Process:
	  		* Check if key is not null and exactly 32 bytes
	  		* Throw exception if fails

	  Returns: void >> (throws if invalid)
	  ====================*/
	private static void validateKey(byte[] key) {
		if (key == null || key.length != Constants.KEY_BYTES) {
			throw new IllegalArgumentException("ChaCha20 requires 32-byte key, got: " + (key == null ? "null" : key.length) );
		}
	}
	
	/*****************************************************/
	/*************** BYTE/WORD CONVERSION ****************/
	/*****************************************************/

	/*======== byte[] littleEndianWord() ==========

	  Inputs:  byte[] src	source byte array
	  		   int offset	byte index that start reading from

	  Process:
	  		* Read 4 bytes starting at the offset in little-endian format
	  		* byte[offset + k] = bits from 8k to 8k + 7 where k = {0,1,2,3}
	  		* Combine to form a 32-bit integer

	  Returns: int word >> 32-bit little-endian word
	  ====================*/
	public static int littleEndianWord(byte[] src, int offset) {
		int word = 0;
		for(int i = 0; i<4; i++) {
			word |= (src[offset + i] & 0xFF) << (8*i);
		} return word;
	}

	/*======== byte[] wordsToBytes() ==========

	  Inputs:  int[] words	16-word ChaCha20 state after mixing

	  Process:
	  		* For each of the 16 words, write 4 bytes in little-endian order
	  		* word[i] byte k -> block[4*i + k] where k = {0,1,2,3}

	  Returns: byte[] block >> 64-byte keystream block in little-endian byte order
	  ====================*/
	private static byte[] wordsToBytes(int[] words) {
		byte[] block = new byte[Constants.BLOCK_BYTES];
		for (int i = 0; i < Constants.STATE_WORDS; i++) {
			block[i*4 + 0] = (byte)(words[i] >>> 0);
			block[i*4 + 1] = (byte)(words[i] >>> 8);
			block[i*4 + 2] = (byte)(words[i] >>> 16);
			block[i*4 + 3] = (byte)(words[i] >>> 24);
		}
		return block;
	}

	/*****************************************************/
	/********************* UTILITIES *********************/
	/*****************************************************/

	/*======== byte[] toHex() ==========

	  Inputs:  byte[] bytes		raw bytes to encode

	  Process:
	  		* Iterate over each byte
	  		* Format each as a 2 character lowercase hex string

	  Returns: String hex >> lowercase hex string (2 chars per byte)
	  ====================*/
	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xFF));
		}
		return sb.toString();
	}

	/*======== byte[] fromHex() ==========

	  Inputs:  String hex	even-length lowercase hex string

	  Process:
	  		* Allocate output array at half string length
	  		* Parse each 2 character substring as an unsigned hex byte

	  Returns: byte[] bytes >> decoded raw bytes
	  ====================*/
	public static byte[] fromHex(String hex) {
		byte[] bytes = new byte[hex.length()/2];
		for (int i = 0; i < bytes.length; i++) {
			
		}
		return bytes;
	}
	
}
