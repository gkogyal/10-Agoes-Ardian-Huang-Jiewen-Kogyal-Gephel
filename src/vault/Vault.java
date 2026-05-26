package vault;

import sha.SHA256;
import aes.AESCipher;
import aes.AESKeySchedule;
import chacha.ChaCha20;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.Set;

import java.sql.*;


public class Vault {

	private static final String DB_FILE = "src/vault/vault.db";

	private static byte[] derivedKey;
	private static int[] aesKey;
	private static Connection db;

	private static final Scanner scanner = new Scanner(System.in);
	private static final Set<String> CMDS = Set.of("add", "list", "get", "del", "help", "quit");

	/***********************************************/
	/******************** MAIN *********************/
	/***********************************************/

	public static void main(String[] args) {
		// Force stdout autoflush so output reaches the terminal even when piped.
		System.setOut(new PrintStream(System.out, true));

		try {

			Class.forName("org.sqlite.JDBC");
			
			db = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
			initializeVault();

			System.out.println("=".repeat(30));
			System.out.println("       Cipher Vault");
			System.out.println("=".repeat(30));


			if (!masterHashExists()) {
				setupNewVault();
			} else {
				authenticateExisting();
			}

			System.out.printf("%nVault unlocked -- %d entry/entries.%n", countEntries());
			System.out.println("Type 'help' for commands.\n");
			loop();

		} catch (Exception e) {
			System.err.println("Fatal: " + e.getMessage());
		} finally {
			try { if (db != null) db.close(); } catch (SQLException ignored) {}
		}
	}

	private static void setupNewVault() throws SQLException {
		System.out.println("\n[New vault - choose a master password]");
		String master = prompt(
			"  Set master password (>=8 chars): ",
			s -> s.length() >= 8,
			"  Password must be at least 8 characters."
		);
		String confirm = prompt("  Confirm password: ", s -> true, "");
		if (!master.equals(confirm)) {
			System.out.println("Passwords don't match. Exiting.");
			System.exit(1);
		}
		String hash = sha256Hex(master);
		saveMasterHash(hash);
		unlockKey(hash);
	}

	private static void authenticateExisting() throws SQLException {
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
			String cmd = prompt("> ", c -> CMDS.contains(c.toLowerCase()), "Unknown command. Type 'help'.");

			switch (cmd.toLowerCase()) {
				case "add"  -> addEntry();
				case "list" -> listEntries();
				case "get"  -> getEntry();
				case "del"  -> delEntry();
				case "help" -> printHelp();
				case "quit" -> { System.out.println("Vault locked. Goodbye."); return; }
			}
		}
	}

	/***********************************************/
	/******************** CMDS *********************/
	/***********************************************/

	private static void printHelp() {
		System.out.println();
		System.out.println("  add  — store a new credential");
		System.out.println("  list — list all stored sites");
		System.out.println("  get  — retrieve a credential by site");
		System.out.println("  del  — delete a credential by site");
		System.out.println("  quit — exit and lock the vault");
		System.out.println();
	}

	private static void addEntry() {
		String site = prompt(" site:  ", s->!s.equals(""), "Site can not be empty.");

		if(findEntry(site) != null) {
			System.out.println(" Entry for ' " +site+ "' already exists. Use 'del' first."); return;
		} else {
			
		}

		String username = prompt(" USERNAME: ", u -> u.length()>=3 && u.matches("[a-zA-Z0-9_]+"), "Username must be at least 3 characters and contain only letters or underscores.");

		String password = prompt(" PASSWORD: ", p -> p.length()>=12, "Password must have at least 12 characters");

		String choice = prompt("  cipher [1 = ChaCha20  2 = AES-256]: ", c -> c.equals("1") || c.equals("2"), "Enter one of the valid choices");

		String ciphertype = choice.equals("2") ? "AES" : "CC20";
		String ciphertext = choice.equals("2") ? encryptAES(password) : encryptChaCha20(password);

		insertEntry(new VaultEntry(site, username, ciphertext, ciphertype));

        System.out.println("Successfully stored with " + ciphertype + ".");
	}

	private static void listEntries() {
		ArrayList<VaultEntry> entries = loadAllEntries();
        if (entries.isEmpty()) {System.out.println("  Vault is empty."); return;}

		System.out.println();
		System.out.printf("  %-4s  %-24s  %-20s  %-6s%n", "#", "SITE", "USERNAME", "CIPHER");
		
		for (VaultEntry e : entries) {
			System.out.printf("  %-4d  %-24s  %-20s  %-6s%n", e.id, e.site, e.username, e.ciphertype);
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
		byte[] ct = ChaCha20.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), derivedKey);
		return ChaCha20.toHex(ct);
	}

	private static String decryptChaCha20(String hex) {
		byte[] ct = ChaCha20.fromHex(hex);
		byte[] pt = ChaCha20.decrypt(ct, derivedKey);
		return new String(pt, StandardCharsets.UTF_8);
	}

	private static String encryptAES(String plaintext) {
		AESCipher aes = new AESCipher();
		byte[] ct = aes.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), aesKey);
		return toHex(ct);
	}

	private static String decryptAES(String hex) {
		AESCipher aes = new AESCipher();
		byte[] ct = fromHex(hex);
		byte[] pt = aes.decrypt(ct, aesKey);
		return new String(pt, StandardCharsets.UTF_8);
	}

	private static String decrypt(VaultEntry e) {
		switch(	e.ciphertype) {
			case "AES": return decryptAES(e.ciphertext);
			case "CC20": return decryptChaCha20(e.ciphertext);
			default: throw new IllegalArgumentException("Unknown cipher type: " + e.ciphertype);
		}
	}

	/***********************************************/
	/********************** DB *********************/
	/***********************************************/

	private static final String COLS = "id, site, username, ciphertext, ciphertype";

	private static void initializeVault() throws SQLException {
		try (Statement st = db.createStatement()) {
			// 'IF NOT EXISTS' makes it  safe to call on every startup
			st.executeUpdate("""
				CREATE TABLE IF NOT EXISTS meta (
					id          INTEGER PRIMARY KEY CHECK (id = 1),
					master_hash TEXT    NOT NULL
				)""");
			st.executeUpdate("""
				CREATE TABLE IF NOT EXISTS entries (
					id          INTEGER PRIMARY KEY AUTOINCREMENT,
					site        TEXT    NOT NULL UNIQUE,
					username    TEXT    NOT NULL,
					ciphertext  TEXT    NOT NULL,
					ciphertype  TEXT    NOT NULL CHECK (ciphertype IN ('AES','CC20'))
				)""");
		}
	}

	// runs a SELECT and returns first col of first row as string
	private static String queryScalar(String sql) throws SQLException {
		try (Statement st = db.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {
			return rs.next() ? rs.getString(1) : null;
		}
	}

	 // Count(*) returns a row always so its fine to do parseInt
	private static boolean masterHashExists() throws SQLException {
		return Integer.parseInt(queryScalar("SELECT COUNT(*) FROM meta")) > 0;
	}

	private static void saveMasterHash(String hash) throws SQLException {
		try (PreparedStatement ps = db.prepareStatement("INSERT INTO meta(id, master_hash) VALUES(1, ?)")) {
			ps.setString(1, hash);
			ps.executeUpdate();
		}
	}

	private static String loadMasterHash() throws SQLException {
		return queryScalar("SELECT master_hash FROM meta WHERE id=1");
	}

	private static int countEntries() throws SQLException {
		return Integer.parseInt(queryScalar("SELECT COUNT(*) FROM entries"));
	}

	// db values -> vaultentry ds
	private static VaultEntry rowToEntry(ResultSet rs) throws SQLException {
		return new VaultEntry(rs.getInt("id"), rs.getString("site"), rs.getString("username"), rs.getString("ciphertext"), rs.getString("ciphertype"));
	}

	private static VaultEntry findEntry(String site) {
		try (PreparedStatement ps = db.prepareStatement("SELECT " + COLS + " FROM entries WHERE site = ?")) {
			ps.setString(1, site);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rowToEntry(rs) : null;
			}
		} catch (SQLException e) {
			return null;
		}
	}

	private static ArrayList<VaultEntry> loadAllEntries() {
		ArrayList<VaultEntry> list = new ArrayList<>();
		try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery("SELECT " + COLS + " FROM entries ORDER BY id")) {
			while (rs.next()) list.add(rowToEntry(rs));
		} catch (SQLException e) {
			System.out.println("DB error");
		}
		return list;
	}

	// oreoares a statement, 'binds' the string params and calls executeUpdate(
	private static void runUpdate(String sql, String... params) { /// ... just means 1+ params treated as string array
		try (PreparedStatement ps = db.prepareStatement(sql)) {
			for (int i = 0; i < params.length; i++)
				ps.setString(i + 1, params[i]); // JDBC params are 1-indexed shrug
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("DB error: " + e.getMessage());
		}
	}

	private static void insertEntry(VaultEntry e) {
		runUpdate("INSERT INTO entries(site, username, ciphertext, ciphertype) VALUES(?, ?, ?, ?)", e.site, e.username, e.ciphertext, e.ciphertype);
	}

	private static void deleteEntry(String site) {
		runUpdate("DELETE FROM entries WHERE site = ?", site);
	}

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
