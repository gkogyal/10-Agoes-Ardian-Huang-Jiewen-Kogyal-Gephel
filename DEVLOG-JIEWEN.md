# Devlog - Jiewen Huang

## 2026-05-10

* Added `src/sha/Constants.java` with the SHA-256 initial hash values (`H`) and the 64 round constants (`K`) from FIPS 180-4. These are the fixed inputs the compression function will need next.
* Matched the package/formatting style of Ardian's `src/aes/Constants.java` to make sure everything is consistent
* Verified the file compiles with `javac`

## 2026-05-11

* Updated `PROPOSAL.md` with updated  proposal and added markdown styling elements to proposal

* Filled in `src/sha/SHA256.java` with the seven SHA-256 helper functions per FIPS 180-4: `rotr`, `ch`, `maj`, `bigSigma0`, `bigSigma1`, `smallSigma0`, `smallSigma1`. These are the bitwise primitives the compression loop will call once that's written
* Compiled cleanly with `javac`
* Added SHA-256 references (Wikipedia + NIST FIPS 180-4) to `README.md`.

## 2026-05-12

* Added `pad()` to `src/sha/SHA256.java`. this implements FIPS 180-4 $5.1.1 padding: appends a 0x80 byte then writes the original message bit length as a 64-bit big endian integer. Output length is always a multiple of 64 bytes so the compression loop can process it block by block

## 2026-05-13 + 2026-05-14 morning

* Out sick; didn't get any commits in

## 2026-05-14 nighttime

* Added `messageSchedule(byte[] block, int offset)` to `src/sha/SHA256.java`. Reads the 16 32bit words out of a 64-byte block, then extends to 64 words using the recurrence `W[i] = smallSigma1(W[i-2]) + W[i-7] + smallSigma0(W[i-15]) + W[i-16]` (FIPS 180-4 6.2.2). This is the input the 64 compression rounds will iterate over

## 2026-05-15

* Back in after the sick days.
* Added comments to code in `SHA256.java` in an effort to make code more perspicuous.

## 2026-05-15

* Added `bytesToWord(byte[] arr, int offset)`; pulled the 4-byte big-endian unpacking out of `messageSchedule` so the loop reads cleanly.
> Did also hit a parenthesis bug where `& 0xff` was inside the index (`arr[offset & 0xff]`) instead of masking the byte value (`(arr[offset] & 0xff)`); fixed

## 5-17

* Changed `messageSchedule` a little bit, the first loop is now a one-liner with the new bytesToWords fxn
* Added `wordToBytes(int word, byte[] arr, int offset)`, the inverse of the other fxn. Writes a 32-bit int into 4 consecutive bytes (big-endian). Not called yet but needed later when the 8-word final hash state has to be serialized into a `byte[32]` in `hash()` 
* Added `compress(int[] state, int[] W)` to `src/sha/SHA256.java` which is the the SHA-256 compression function (FIPS 180-4 6.2.2). Takes the running 8-word state and the 64-word message schedule from one block, runs 64 rounds mixing in `K[i]` and `W[i]`, and adds the result back into the state in place
* After the loop, the new working vars are *added* (not assigned) into the state. This is why the function is called "compress" and not "encrypt"
> Also worth noting that I hit a bug in the variable shift where i originally wrote it top-down (`a = nextA; b = a; c = b`), but `b = a` overwrites a before c can read it, so a-d all collapsed to `nextA`. Fixed by reversing the order: `h = g; g = f; f = e; e = nextE; d = c; c = b; b = a; a = nextA;` so every read now happens before its overwrite.

## 2026-05-18

* Created `hash(byte[] input)` function, which is intended to create the final cryptographic hash by calling upon the other functions and rip the final 8 integers to 32 bytes.
* Added verbose option to visualize the process for the SHA-256 algorithm.

## 2026-05-19

* Began coding `TestSHA256.java` file in `testing` directory. This will serve as a main visual indicator for how our code functions and test case checking. 
* Filled in the static portion of `TestSHA256.java`: `section`
* `runTest` compares my `SHA256.hash(...)` output against Java's built-in `MessageDigest.getInstance("SHA-256")`. Using a library in tests for verification is fine I think since the proposal's "no crypto libraries" rule is about the implementation, not how we check it. Gephel did the same with `javax.crypto.Cipher` in `TestChaCha20`

## 2026-05-20

* Imported HexFormat which allows me to handle byte array to hex string conversion; previously, I had planned on using a function but this is more convenient
* Began randomCases() function on testing file
* Restructured `TestSHA256.java` to mirror Gephel's `TestChaCha20.java` layout although I kept my own SHA-specific static cases (with the 55/56/64-byte padding boundary tests) and used `HexFormat.of().formatHex(...)` instead of his `ChaCha20.toHex(...)`
* A few differences: `runTest` does only **one** sub-test per case (`hash vs MessageDigest`) instead of his two (ciphertext + decrypt roundtrip) because hashes have no decrypt step. That's why all my case numbers come out as `0a, 1a, 2a...` and never `Nb`
* Tested: 15/15 PASS (10 static + 5 random) when matched against `MessageDigest.getInstance("SHA-256")` as ref