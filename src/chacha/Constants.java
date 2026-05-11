package chacha;

public class Constants {

	// 128-bit constant; ascii values of "expand 32-byte k" then little-endian'd
	public static final int[] SIGMA = {

		0x61707865, // "expa"
		0x3320646e, // "nd 3"
		0x79622d32, // "2-by"
		0x6b206574  // "te k"
		
	};

	// Bit-rotation amounts needed for quarter round
	// 16/8 for speed; 12 for crossing byte boundaries and 7 for adjacent bit diffusion
	public static final int ROT_16 = 16;
	public static final int ROT_12 = 12;
	public static final int ROT_8 = 8;
	public static final int ROT_7 = 7;

	// 4x4 grid of words = 16 words
	public static final int STATE_WORDS = 16;

	// 256 bit key has size of 32 bytes
	public static final int KEY_BYTES = 32;

	// The standard is 12 bytes or 96 bits; IETF (RFC 8439) version
	public static final int NONCE_BYTES = 12;

	// 16 words after all 20 mixing rounds * (4 bytes / word) = 64 bytes
	// note: 32 bits = 1 word
	public static final int BLOCK_BYTES = 64;

	// this is chacha 20 so 20 rounds
	public static final int ROUNDS = 20;
	
}
