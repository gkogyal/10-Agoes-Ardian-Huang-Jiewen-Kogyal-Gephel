package sha;

public class SHA256 {
    static int rotr(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }

    static int ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    static int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    static int bigSigma0(int x) {
        return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22);
    }

    static int bigSigma1(int x) {
        return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25);
    }

    static int smallSigma0(int x) {
        return rotr(x, 7) ^ rotr(x, 18) ^ (x >>> 3);
    }

    static int smallSigma1(int x) {
        return rotr(x, 17) ^ rotr(x, 19) ^ (x >>> 10);
    }

    static byte[] pad(byte[] message) {
        int messageLen = message.length;
        long bitLen = (long) messageLen * 8;
        int totalLen = ((messageLen + 9 + 63) / 64) * 64;
        byte[] padded = new byte[totalLen];
        System.arraycopy(message, 0, padded, 0, messageLen);
        padded[messageLen] = (byte) 0x80;
        for (int i = 0; i < 8; i++) {
            padded[totalLen - 1 - i] = (byte) (bitLen >>> (8 * i));
        }
        return padded;
    }
}
