package testing;

import aes.AES256;
import aes.AESKeySchedule;

public class TestAES256 {

    public static void main(String[] args) {
        AES256 aes = new AES256();
        String text = "StuyvesantHS2026";

        byte[] plainText = text.getBytes();

        String key = "supersecretpasswordforvault12345";
        byte[] masterKey = key.getBytes();

        System.out.println("Original: " + text);

        int[] expanded = AESKeySchedule.expandKey(masterKey);

        byte[] cipher = aes.encrypt(plainText, expanded);

        System.out.print("Encrypted: ");
        for (byte b : cipher) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) System.out.print('0');
            System.out.print(hex + " ");
        }
        System.out.println();

        byte[] decrypted = aes.decrypt(cipher, expanded);
        String result = new String(decrypted);
        System.out.println("Decrypted: " + result);

        if (text.equals(result)) {
            System.out.println("Status: works");
        } else {
            System.out.println("Status: broken");
        }
    }
}
