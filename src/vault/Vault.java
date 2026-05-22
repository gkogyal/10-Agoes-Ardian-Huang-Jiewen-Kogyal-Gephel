package vault;
import sha.SHA256;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HexFormat;

public class Vault {
    private static String masterPasswordHash;
    private static ArrayList<VaultEntry> database = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("-- CIPHER VAULT --");
        System.out.print("Set master password: ");
        String master = scanner.nextLine();
        masterPasswordHash = sha256Hex(master); // not coded yet but should be straightforward to do using the SHA256 class
        System.out.print("Confirm master password: ");
        String confirm = scanner.nextLine();
        if (!masterPasswordHash.equals(sha256Hex(confirm))) {
            System.out.println("Passwords don't match. Exiting.");
            return;
        }
        System.out.println("Vault unlocked. Type 'help' for commands.");
        loop();
    }

    private static void loop() {
        while (true) {
            System.out.print("\n> ");
            String cmd = scanner.nextLine().trim();
            if (cmd.equals("quit") || cmd.equals("q")) {
                System.out.println("bye");
                return;
            } else if (cmd.equals("add")) {
                addEntry();
            } else if (cmd.equals("list")) {
                listEntries();
            } else if (cmd.equals("get")) {
                getEntry(); // still gotta implement these functions
            } else if (cmd.equals("del")) {
                delEntry();
            } else if (cmd.equals("help")) {
                printHelp();
            } else if (!cmd.isEmpty()) {
                System.out.println("unknown command. type 'help' for commands.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("commands:");
        System.out.println("add - store a new credential");
        System.out.println("list - list all stored sites");
        System.out.println("get - retrieve a credential by site");
        System.out.println("del - delete a credential by site");
        System.out.println("quit - exit");
    }

    private static void addEntry() {
        System.out.print("site: ");
        String site = scanner.nextLine();
        System.out.print("username: ");
        String username = scanner.nextLine();
        System.out.print("password: ");
        String password = scanner.nextLine();
        // TODO: encrypt password with AES-256 or ChaCha20 using masterPasswordHash as the key
        database.add(new VaultEntry(site, username, password));
        System.out.println("stored.");
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

    //UNFINISHED still gotta add some more functionality here 
}