package sha;

public class SHA256 {
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
            int j = offset + i * 4;
            W[i] = (block[j] & 0xff) << 24 | (block[j + 1] & 0xff) << 16 | (block[j + 2] & 0xff) << 8 | (block[j + 3] & 0xff);
        }
        for (int i = 16; i < 64; i++) { // gen the other 48 words using previous words and the small sigmas
            W[i] = smallSigma1(W[i - 2]) + W[i - 7] + smallSigma0(W[i - 15]) + W[i - 16];
        }
        return W;
    }
    
    static int bytesToWord(byte[] arr, int offset) {
        return (arr[offset & 0xff] << 24) | (arr[offset+1] & 0xff) << 16 | (arr[offset+2] & 0xff) << 8 | (arr[offset + 3] & 0xff);
    }
    static void wordToBytes(int word, byte[] arr, int offset) {
        arr[offset] = (byte) (word >>> 24);
    }
}
