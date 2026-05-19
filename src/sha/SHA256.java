package sha;

public class SHA256 {
    public static boolean verbose = false;
    static int rotr(int x, int n) { //rotate right
        return (x >>> n) | (x << (32 - n)); // shifts bits over and wraps the ones that fall off back to the front
    }

    static int ch(int x, int y, int z) {
        return (x & y) ^ (~x & z); // if x bit is 1 grab y's bit, else grab z
    }

    static int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z); // majority; returns 1 if at least two of the input bits are 1
    }

    static int bigSigma0(int x) {
        return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22); // big sigmas used to mix up the working state variables a-h during the rounds
    }

    static int bigSigma1(int x) {
        return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25); 
    }

    static int smallSigma0(int x) {
        return rotr(x, 7) ^ rotr(x, 18) ^ (x >>> 3); // small sigmas used in the msg schedule to expand the initial 16 words into 64
    }

    static int smallSigma1(int x) {
        return rotr(x, 17) ^ rotr(x, 19) ^ (x >>> 10);
    }

    static byte[] pad(byte[] message) { // padding is required b/c total lenght has to be a multiple of 64 bytes
        int messageLen = message.length;
        long bitLen = (long) messageLen * 8;
        // calc total bytes needed including the 1 bit, zero padding, and 8 byte length
        int totalLen = ((messageLen + 9 + 63) / 64) * 64;
        byte[] padded = new byte[totalLen];
        System.arraycopy(message, 0, padded, 0, messageLen);
        // append the 1 bit right after the message (0x80 in hex)
        padded[messageLen] = (byte) 0x80;
        for (int i = 0; i < 8; i++) {
            padded[totalLen - 1 - i] = (byte) (bitLen >>> (8 * i)); //add back original bit length
        }
        return padded;
    }

    static int[] messageSchedule(byte[] block, int offset) { // takes a 64 byte block and builds the 64 word array for the compression loop
        int[] W = new int[64];
        for (int i = 0; i < 16; i++) { // first 16 words are just the raw block parsed from bytes into ints
            W[i] = bytesToWord(block, offset + i * 4);
        }
        for (int i = 16; i < 64; i++) { // gen the other 48 words using previous words and the small sigmas
            W[i] = smallSigma1(W[i - 2]) + W[i - 7] + smallSigma0(W[i - 15]) + W[i - 16];
        }
        return W;
    }

    static int bytesToWord(byte[] arr, int offset) {
        return (arr[offset] & 0xff) << 24 | (arr[offset+1] & 0xff) << 16 | (arr[offset+2] & 0xff) << 8 | (arr[offset + 3] & 0xff);
    }
    static void wordToBytes(int word, byte[] arr, int offset) {
        arr[offset] = (byte) (word >>> 24);
        arr[offset+1] = (byte) (word >>> 16);
        arr[offset+2] = (byte) (word >>> 8);
        arr[offset+3] = (byte) word;
    }

    static void compress(int[] state, int[] W) {
        int a = state[0], b = state[1], c = state[2], d = state[3];
        int e = state[4], f = state[5], g = state[6], h = state[7];
        if (verbose) {
            System.out.println("-- block compression starts here --");
            System.out.println("init: a=" + Integer.toHexString(a) + " b=" + Integer.toHexString(b) + " c=" + Integer.toHexString(c) + " d=" + Integer.toHexString(d) + " e=" + Integer.toHexString(e) + " f=" + Integer.toHexString(f) + " g=" + Integer.toHexString(g) + " h=" + Integer.toHexString(h));
        }
        for (int i = 0; i < 64; i++) {
            int nextA = h + bigSigma1(e) + ch(e, f, g) + Constants.K[i] + W[i] + bigSigma0(a) + maj(a, b, c);
            int nextE = d + h + bigSigma1(e) + ch(e, f, g) + Constants.K[i] + W[i];
            h = g;
            g = f;
            f = e;
            e = nextE;
            d = c;
            c = b;
            b = a;
            a = nextA;
            if (verbose) {
                System.out.println("round " + i + ": a=" + Integer.toHexString(a) + " b=" + Integer.toHexString(b) + " c=" + Integer.toHexString(c) + " d=" + Integer.toHexString(d) + " e=" + Integer.toHexString(e) + " f=" + Integer.toHexString(f) + " g=" + Integer.toHexString(g) + " h=" + Integer.toHexString(h));
            }
        }
        state[0] = state[0] + a;
        state[1] = state[1] + b;
        state[2] = state[2] + c;
        state[3] = state[3] + d;
        state[4] = state[4] + e;
        state[5] = state[5] + f;
        state[6] = state[6] + g;
        state[7] = state[7] + h;
    }
    public static byte[] hash(byte[] input) {
        byte[] padded = pad(input);
        int[] state = new int[8];
        System.arraycopy(Constants.H, 0, state, 0, 8);
        for (int i = 0; i < padded.length; i += 64) {
            int[] W = messageSchedule(padded, i);
            compress(state, W); //makes state stay in place
        }
        byte[] output = new byte[32];
        for (int i = 0; i < 8; i++) { //bring 8 ints down to 32 bytes
            wordToBytes(state[i], output, i * 4);
        }
        return output;
    }
}
