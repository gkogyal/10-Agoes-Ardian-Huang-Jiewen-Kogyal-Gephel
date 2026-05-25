package testing;
 
public class TestUtils {

	public static String line(int N) {
		return "=".repeat(N);
	}

	public static void section(String a, String b, String c) {
		System.out.print(a + b + c);
	}

	public static void section(String str, int N) {
		N = Math.max(N,str.length()+10);

		String div = line(N) + "\n";
		String mid = " ".repeat((N - str.length())/2);

		System.out.print(div + mid + str + "\n" + div + "\n");
	}

	public static void section(String str) {
		section(str,Math.max(30,str.length()+10));
	}

	public static String preview(String s) {
		if (s.isEmpty()) return "<empty>";

		String clean = s.replaceAll("[\\r\\n\\t]", " ");
		return clean.length() <= 30 ? "\"" + clean + "\"" : "\"" + clean.substring(0, 27) + "...\"";
	}

	public static String truncate(String s, int max) {
		return s.length() <= max ? s : s.substring(0, max) + "…" + " ( " + (max-s.length()) + "more)";
	}

 
	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) sb.append(String.format("%02x", b & 0xFF));
		return sb.toString();
	}
}
