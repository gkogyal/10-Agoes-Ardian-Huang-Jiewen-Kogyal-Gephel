package testing;

import sha.SHA256;

import java.util.HexFormat;
import java.util.Scanner;

public class VerboseSHA256 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TestUtils.section("SHA-256 Verbose Trace");

        System.out.print("  Enter plaintext: ");
        String plaintext = sc.nextLine();

    }
}
