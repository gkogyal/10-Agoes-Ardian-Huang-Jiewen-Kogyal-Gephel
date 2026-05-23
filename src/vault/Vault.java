package vault;
import sha.SHA256;
import aes.AES256;
import chacha.ChaCha20;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.function.Predicate;
import java.util.Set


public class Vault {

    private static String masterPasswordHash;
	private static byte[] derivedKey;

    private static ArrayList<VaultEntry> database = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    private static final Set<String> COMMANDS = Set.of("add", "list", "get", "del", "help", "quit");

    public static void main(String[] args) {
        System.out.println("-- CIPHER VAULT --");
        
        System.out.print("\nSet master password: ");
        String master = scanner.nextLine();
        
        System.out.print("Confirm master password: ");
		String confirm = scanner.nextLine();
        
        if (!sha256Hex(master).equals(sha256Hex(confirm))) {
            System.out.println("Passwords don't match. Exiting.");
            return;
        }

        masterPasswordHash = sha256Hex(master);

        derivedKey = ChaCha20.fromHex(masterPasswordHash);
        
        System.out.println("\nVault unlocked.\nType 'help' for commands.\n");
        loop();
    }

	private static String prompt(String label, Predicate<String> validator, String errorMsg) {
		while (true) {
			System.out.print(label);
			String input = scanner.nextLine().trim();
			if (validator.test(input)) return input;
			System.out.println(errorMsg);
		}
	}


    private static void loop() {
        while (true) {        
            String cmd = prompt("> ", c -> COMMANDS.contains(c.toLowerCase()),"Unknown command. Type 'help'.")

            
			switch(cmd.toLowerCase()) {

				case "add": addEntry(); break;
				case "list": listEntries(); break;
				case "get": getEntry(); break;
				case "del": delEntry(); break;
				case "help": printHelp(); break;
				case "quit": case "q": 
					System.out.println("Vault locked. Goodbye."); 
					return;
				default:
					if(!cmd.isEmpty()) {
						System.out.println("Unknown command. Type 'help'.");
					}
			}
        }
    }

    private static void printHelp() {
        System.out.println("\n ** List of Commands **");
        System.out.println("[add] - store a new credential");
        System.out.println("[list] - list all stored sites");
        System.out.println("[get] - retrieve a credential by site");
        System.out.println("[del] - delete a credential by site");
        System.out.println("[quit] - exit\n");
    }

    private static void addEntry() {
    
		System.out.print("site: ");
		String site = scanner.nextLine().trim();
        
		String username = prompt(" USERNAME: ", u -> u.length()>=3 && u.matches("[a-ZA-Z_]+"), "Username must be at least 3 characters and contain only letters or underscores.");

		String password = prompt(" PASSWORD: ", p -> p.length()>=12, "Password must have at least 12 characters");

		String choice = prompt("  cipher [1 = ChaCha20  2 = AES-256]: ", c -> c.equals("1") || c.equals("2"), "Enter one of the valid choices");


		String stored = choice.equals("2") ? "AES:" + encryptAES(password) : "CC20:" + encryptChaCha20(password);
		String cipherLabel = choices.equals("2") ? "AES-256" : "ChaCha20";

        database.add(new VaultEntry(site, username, password));
        System.out.println("Stored with " + cipherLabel + ".");
    }

    private static void listEntries() {
        if (database.isEmpty()) {
            System.out.println("vault is empty.");
            return;
        }
        for (int i = 0; i < database.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + database.get(i).site);
        }
    }

	private static void getEntry() {
		if (database.isEmpty()) { System.out.println("  Vault is empty."); return; }
 
		System.out.print("  site: ");
		String site = scanner.nextLine().trim();

		VaultEntry entry = findEntry(site);
		if (entry == null) {
			System.out.println("No entry found for: " + site);
			return;
		}
 
		String decrypted = decrypt(entry.encryptedPasswd);
		System.out.println();
		System.out.println("Site: " + entry.site);
		System.out.println("Username: " + entry.username);
		System.out.println("Password: " + decrypted);
		System.out.println();
	}



    private static String encryptChaCha20(String plaintext) {
        byte[] ciphertext = ChaCha20.encrypt(plaintext.getBytes(), derivedKey);
        return ChaCha20.toHex(ciphertext);
    }
    private static String decryptChaCha20(String hex) {
        byte[] ciphertext = ChaCha20.fromHex(hex);
        byte[] plaintext  = ChaCha20.decrypt(ciphertext, derivedKey);
        return new String(plaintext);
    }

    private static String decrypt(String stored) {
        if (stored.startsWith("AES:"))  return decryptAES(stored.substring(4));
        if (stored.startsWith("CC20:")) return decryptChaCha20(stored.substring(5));
        throw new IllegalArgumentException("Unrecognised cipher prefix in stored entry");
    }


    
}
