package vault;

import sha.SHA256;
import aes.AES256;
import chacha.ChaCha20;

import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.function.Predicate;
import java.util.Set;

import java.sql.*;



public class Vault {

	private static final String DB_FILE = "vault.db";
	
	private static byte[] derivedKey;
	private static int[] aesKey;

	private static Connection db;

    private static Scanner scanner = new Scanner(System.in);

    private static final Set<String> CMDS = Set.of("add", "list", "get", "del", "help", "quit");

    public static void main(String[] args) {

		try {

			System.out.println("=".repeat(30));
			System.out.println("       Cipher Vault");
			System.out.println("=".repeat(30));

			if (!masterHashExists()) {
				setupNewVault();
			} else {
				authenticateExisting();
			}

			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    }

	private static void setupNewVault() {
		System.out.println("\n[New vault — choose a master password]");
		String master = prompt("  Set master password (>= 12 chars): ", s -> s.length() >= 12, "  Password must be at least 12 characters.");
		String confirm = prompt("  Confirm password: ", s -> true, "");
		if (!master.equals(confirm)) {
			System.out.println("Passwords don't match. Exiting.");
			System.exit(1);
		}

		String hash = sha256Hex(master);
		saveMasterHash(hash); //wip
		unlockKey(hash);
	}


	private static void authenticateExisting() {
		System.out.print("\nMaster password: ");
		String input = scanner.nextLine();
		String hash = sha256Hex(input);
		if (!hash.equals(loadMasterHash())) {
			System.out.println("Incorrect password.");
			System.exit(1);
		}
		unlockKey(hash);
	}

	// getting encryption key from master pwd hash
	private static void unlockKey(String hash) { 
		derivedKey = ChaCha20.fromHex(hash);
		aesKey = AESKeySchedule.expandKey(derivedKey);
		
	}


    private static void loop() {
        while (true) {        
            String cmd = prompt("> ", c -> CMDS.contains(c.toLowerCase()),"Unknown command. Type 'help'.");
            
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

	/***********************************************/
	/******************** CMDS *********************/
	/***********************************************/

    private static void printHelp() {
        System.out.println("\n ** List of Commands **");
        System.out.println("[add] - store a new credential");
        System.out.println("[list] - list all stored sites");
        System.out.println("[get] - retrieve a credential by site");
        System.out.println("[del] - delete a credential by site");
        System.out.println("[quit] - exit\n");
    }

    private static void addEntry() {
    
		String site = prompt(" site:  ", s->!s.equals(""), "Site can not be empty.");

		if(findEntry(site) != null) {
			System.out.println(" Entry for ' " +site+ "' already exists. Use 'del' first."); return;
		} else {
			
		}

		String username = prompt(" USERNAME: ", u -> u.length()>=3 && u.matches("[a-ZA-Z_]+"), "Username must be at least 3 characters and contain only letters or underscores.");

		String password = prompt(" PASSWORD: ", p -> p.length()>=12, "Password must have at least 12 characters");

		String choice = prompt("  cipher [1 = ChaCha20  2 = AES-256]: ", c -> c.equals("1") || c.equals("2"), "Enter one of the valid choices");


		String stored = choice.equals("2") ? "AES:" + encryptAES(password) : "CC20:" + encryptChaCha20(password);
		String ciphertype = choice.equals("2") ? "AES" : "CC20";
		String ciphertext = choice.equals("2") ? encryptAES(password) : encryptChaCha20(password);


		VaultEntry entry = new VaultEntry(site, username, ciphertext, ciphertype);

        System.out.println("Successfully stored with " + ciphertype + ".");
    }

    private static void listEntries() {
    	ArrayList<VaultEntry> entries = loadAllEntries();
        if (entries.isEmpty()) {System.out.println("  Vault is empty."); return;}

        
		System.out.printf("  %-24s  %-20s  %-6s%n",e.site, e.username, e.ciphertype);
		
        for (VaultEntry e : entries) {
			System.out.printf("  %-4d  %-24s  %-20s  %-6s  %s%n", e.site, e.username, e.ciphertype);
		}
        
        for (int i = 0; i < database.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + database.get(i).site);
        }
    }

	private static void getEntry() {
		ArrayList<VaultEntry> entries = loadAllEntries();
		if (entries.isEmpty()) { System.out.println("  Vault is empty."); return; }

		String site = prompt(" site:  ", s->!s.equals(""), "Site can not be empty.");

		VaultEntry entry = findEntry(site);
		if (entry == null) { System.out.println("  No entry found."); return; }

		String decrypted = decrypt(entry);

		System.out.println();
		System.out.println("  Site     : " + entry.site);
		System.out.println("  Username : " + entry.username);
		System.out.println("  Password : " + decrypted);
		System.out.println("  Cipher   : " + entry.ciphertype);
		System.out.println();

	}

	private static void delEntry() {
		String site = prompt(" site:  ", s->!s.equals(""), "Site can not be empty.");

		VaultEntry entry = findEntry(site);
		if (entry == null) { System.out.println("  No entry found."); return; }

		deleteEntry(site);
		System.out.println(" Deleted entry.\n");
		
	}

	/***********************************************/
	/******************* HELPER ********************/
	/***********************************************/

    private static String encryptChaCha20(String plaintext) {
        byte[] ciphertext = ChaCha20.encrypt(plaintext.getBytes(), derivedKey);
        return ChaCha20.toHex(ciphertext);
    }
    private static String decryptChaCha20(String hex) {
        byte[] ciphertext = ChaCha20.fromHex(hex);
        byte[] plaintext = ChaCha20.decrypt(ciphertext, derivedKey);
        return new String(plaintext);
    }

    private static String encryptAES(String plaintext) {
    	AES256 aes = new AES256();
    	byte[] ct  = aes.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), aesKey);
    	return toHex(ct);
    }

    private static String decryptAES(String hex) {
    	AES256 aes = new AES256();
		byte[] ciphertext = fromHex(hex);
		byte[] plaintext = aes.decrypt(ct, aesKey);
		return new String(plaintext, StandardCharsets.UTF_8);
    }

    private static String decrypt(VaultEntry entry) {
		switch(	entry.ciphertype) {
			case "AES": return decryptAES(entry.ciphertext);
			case "CC20": return decryptChaCha20(entry.ciphertext);
			default: throw new IllegalArgumentException("Unknown cipher type: " + e.ciphertype);
		}
    }

	/***********************************************/
	/********************** DB *********************/
	/***********************************************/

	private static void initializeVault() {}

	private static boolean masterHashExists() {}

	private static void saveMasterHash(String hash) {}

	private static String loadMasterHash() {}

	private static int countEntries() {}

	private static VaultEntry findEntry(String site) {}

	private static ArrayList<VaultEntry> loadAllEntries() {}

	private static void insertEntry(VaultEntry e) {}

   	/***********************************************/
	/****************** UTILITIES ******************/
	/***********************************************/

	private static String sha256Hex(String input) {
		byte[] hash = SHA256.hash(input.getBytes(StandardCharsets.UTF_8));
		return toHex(hash);
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) sb.append(String.format("%02x", b & 0xFF));
		return sb.toString();
	}


	private static byte[] fromHex(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
		return bytes;
	}


	private static String prompt(String label, Predicate<String> validator, String errorMsg) {
		while (true) {
			System.out.print(label);
			String input = scanner.nextLine().trim();
			if (validator.test(input)) return input;
			System.out.println(errorMsg);
		}
	}
}
