package chacha;

public class QuarterRound {

	/*======== void apply() ==========
      Inputs:  int[] state    full 16-word ChaCha20 state array (modified in-place)
               int ai      	  index of word "a" in the state
               int bi		  index of word "b" in the state
               int ci      	  index of word "c" in the state
               int di      	  index of word "d" in the state
 
      Process:
               * Load the four target words from the state at indices ai, bi, ci, di
               * Execute the 6-step ARX sequence:
                   a += b;  d ^= a;  d <<<= 16
                   c += d;  b ^= c;  b <<<= 12
                   a += b;  d ^= a;  d <<<= 8
                   c += d;  b ^= c;  b <<<= 7
               * Write the four modified words back into the state array
 
      Returns: void >> state[ai], state[bi], state[ci], state[di] updated in-place
      ====================*/
    public static void apply(int[] state, int ai, int bi, int ci, int di) {

		int a = state[ai];
		int b = state[bi];
		int c = state[ci];
		int d = state[di];

		// round 1
		a += b;  d ^= a;  d = rotl(d, Constants.ROT_16);

		// round 2
		c += d;  b ^= c;  b = rotl(b, Constants.ROT_12);

		// round 3
		a += b;  d ^= a;  d = rotl(d, Constants.ROT_8);

		// round 4
		c += d;  b ^= c;  b = rotl(b, Constants.ROT_7);

		state[ai] = a;
		state[bi] = b;
		state[ci] = c;
		state[di] = d;
    }

    /*======== int rotl() ==========
      Inputs:  int x       32-bit word to rotate
               int shift   number of bit positions to rotate left (1-31)
 
      Process:
               * Leftshift shift bits deletes first shift bits
               * Return above OR'd with first shift bits
 
      Returns: int result >> x rotated left by shift positions
      ====================*/
    static int rotl(int x, int shift) {
        return (x << shift) | (x >>> (32 - shift));
    }

    /*======== int[] applyDirect() ==========
      Inputs:  int a    word a
               int b    word b
               int c    word c
               int d    word d
 
      Process:
               * Pack the four words into a temporary int[4] array
               * Call apply() on indices 0, 1, 2, 3
               * For unit tests that inspect individual quarter-round outputs without a full 16-word state)
 
      Returns: int[] res >> {'a, 'b, 'c, 'd} where ' is arx'd
      ====================*/
    public static int[] applyDirect(int a, int b, int c, int d) {
        int[] res = {a, b, c, d};
        apply(res, 0, 1, 2, 3);
        return res;
    }


}
