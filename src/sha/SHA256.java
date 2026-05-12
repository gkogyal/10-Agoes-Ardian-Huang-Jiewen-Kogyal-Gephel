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

}
