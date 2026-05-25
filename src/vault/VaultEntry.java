package vault;

public class VaultEntry {

	public final int id;
	public final String site;
	public final String username;
	public final String ciphertext;
	public final String ciphertype;
	public final String birth; // creation time
    
    public VaultEntry (int id, String site, String username, String ciphertext, String ciphertype, String birth) {
    	this.id = id;
		this.site = site;
		this.username = username;
		this.ciphertext = ciphertext;
		this.ciphertype = ciphertype;
		this.birth = birth;
	}

	public VaultEntry(String site, String username, String ciphertext, String cipher) {
		this(-1, site, username, ciphertext, cipher, "");
	}

	public String toString() {
		return String.format("VaultEntry{id=%d, site='%s', username='%s', cipher=%s, createdAt='%s'}", id, site, username, ciphertype, (birth.equals("") ? "EMPTY" : birth));
	}

}
