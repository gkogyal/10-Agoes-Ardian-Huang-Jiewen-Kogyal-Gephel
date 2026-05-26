package aes;

import java.io.ByteArrayOutputStream;


public class AESCipher {
    private final AES256 aesCipher;

    public AESCipher() {
        this.aesCipher = new AES256();
    }

    /*
     Encrypts the input plaintext using AES-256 encryption. 
     It first pads the plaintext to ensure its length is a multiple of 16 bytes, then processes it in 16-byte blocks through AES encryption. The resulting ciphertext is collected in a ByteArrayOutputStream and returned as a byte array.
    */

    public byte[] encrypt(byte[] plaintext, int[] eKey) {
        byte[] paddedPlaintext = Padding.pad(plaintext);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < paddedPlaintext.length; i += 16) {
            byte[] block = new byte[16];
            System.arraycopy(paddedPlaintext, i, block, 0, 16);
            byte[] encryptedBlock = this.aesCipher.encrypt(block, eKey);
            outputStream.write(encryptedBlock, 0, 16);
        }
        return outputStream.toByteArray();
    }

    /*
     Decrypts the input ciphertext using AES-256 decryption.
     It processes the ciphertext in 16-byte blocks through AES decryption. The resulting plaintext is collected in a ByteArrayOutputStream and returned as a byte array.
    */
    public byte[] decrypt(byte[] ciphertext, int[] eKey) {
        if (ciphertext.length % 16 != 0 || ciphertext.length == 0) {
            throw new IllegalArgumentException("Ciphertext length must be a multiple of 16 bytes");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < ciphertext.length; i += 16) {
            byte[] block = new byte[16];
            System.arraycopy(ciphertext, i, block, 0, 16);
            byte[] decryptedBlock = this.aesCipher.decrypt(block, eKey);
            outputStream.write(decryptedBlock, 0, 16);
        }
        byte paddedPlaintext[] = outputStream.toByteArray();
        return Padding.unpad(paddedPlaintext);
    }
}