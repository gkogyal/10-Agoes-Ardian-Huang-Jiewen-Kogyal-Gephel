package aes;

import java.io.ByteArrayOutputStream;


public class AESCipher {
    private final AES256 engine;

    public AESCipher() {
        this.engine = new AES256();
    }

    /*
     Encrypts the input plaintext using AES-256 encryption. 
     It first pads the plaintext to ensure its length is a multiple of 16 bytes, then processes it in 16-byte blocks through the AES encryption engine. The resulting ciphertext is collected in a ByteArrayOutputStream and returned as a byte array.
    */
    public byte[] encrypt(byte[] plaintext, int[] eKey) {
        byte[] paddedPlaintext = Padding.pad(plaintext);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < paddedPlaintext.length; i += 16) {
            byte[] block = new byte[16];
            System.arraycopy(paddedPlaintext, i, block, 0, 16);
            byte[] encryptedBlock = this.engine.encrypt(block, eKey);
            outputStream.write(encryptedBlock, 0, 16);
        }
        return outputStream.toByteArray();
    }
}