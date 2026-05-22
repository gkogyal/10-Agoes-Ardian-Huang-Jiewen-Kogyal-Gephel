package vault;

public class VaultEntry {
    public String site;
    public String username;
    public String encryptedPasswd;
    public VaultEntry(String site, String username, String encryptedPasswd) {
        this.site = site;
        this.username = username;
        this.encryptedPasswd = encryptedPasswd;
    }
}