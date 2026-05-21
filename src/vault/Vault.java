package vault;
import sha.SHA256;
import java.util.Scanner;
import java.util.ArrayList;

public class Vault {
    private static String masterPasswordHash;
    private static ArrayList<VaultEntry> database = new ArrayList<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("-- CIPHER VAULT --");
        String master = scanner.nextLine();
        masterPasswordHash = SHA256.hashHex(master);
    }
}