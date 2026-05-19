package testing;
import sha.SHA256;

public class TESTSHA256 {
    //helper fxn
    static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length*2];
        char[] hexAlphabet= "0123456789abcdef".toCharArray();
        for (int i = 0; i<bytes.length; i++) {
            int x = bytes[i] & 0xFF;
            hexChars[i*2] = hexAlphabet[v >>> 4];
            hexChars[i*2 + 1] = hexAlphabet[v & 0x0F]; //bottom 4 bits
        }
        return new String(hexChars);
    }
}