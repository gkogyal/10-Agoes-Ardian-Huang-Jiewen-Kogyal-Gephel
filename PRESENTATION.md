# AES-256 by Ardian

## 1. Overview 

AES-256 (Advanced Encryption Standard) is a **symmetric block cipher**. 

- Symmetric means that the same 32-byte or 256-bit key is used to lock and unlock the data
- Block ciphers don't encrypt the data by character, and instead chops the data into 16-byte blocks and encrypts each block one at a time.
- If a block is less than 16 bytes, we use `PKCS#7` padding to fill the gaps.

### Where does the encryption key come from?

For a password vault, the user creates a master password. The master password is run through the SHA-256 hashing algorithm, which will always output a 32-byte hash of what looks like random data. Typing the correct master password will generate the exact same 32-byte AES key to unlock the vault.

## 2. Preparing the Data: Padding and ECB Mode

### PKCS#7 Padding

To make every piece of data a multiple of 16, we use a standard called PKCS#7 padding.

- If a password is 20 bytes long, it would need 12 more bytes to be a multiple of 16 (32).
- The algorithm looks at the number of missing bytes and appends the byte value of the number (0x0c) 12 times to the end of the string.
- During decryption, the algorithm looks at the last byte, which would be 12, and cuts off the last 12 bytes to reveal the original password.

### Electronic Codebook Mode

After padding to a multiple of 16, you have to split it up into 16-byte blocks before feeding it into the AES engine with something called ECB mode.

- The `AESCipher` wrapper slices the 32-byte array into two separate 16-byte blocks.
- Each block is turned into ciphertext separately and put back together.
- ECB can have limitations as if two separate blocks contain the exact same data, they would make the exact same ciphertext blocks, which makes an obvious pattern.

## 3. The Process and Math

Once the 16-byte block is put into the AES engine, it is converted into a 4x4 byte grid called the State Matrix where all the math occurs.
Aes encrypts data by substituting bytes for different values to create something called **Confusion** and then shuffles the positions of the bytes to ensure that there is no correlation between the plaintext and ciphertext.

AES-256 requires 14 rounds of encryption, and an initial round of just setting up the encryption. This means that we need 15 different keys.

In `AESKeySchedule.java`, the 32-byte master key is turned into a 60 word array, which makes 240 bytes as each word is four bytes. Each round key is 16 bytes, so this process called the `Rijndael Key Schedule` generates 15 keys. To prevent patterns or symmetries, the algorithm heavily mutates the master key data by:

1. `rotWord()` shifts the bytes of the key to different positions.
2. `subWord()` passes those bytes through a constant array called `SBOX` which substitutes the original 4 bytes of the key by mapping the number of each byte to the correlating index on the S-Box table, which has 256 values.
3. Using the `RCON` constant array, the output of the substitution's leftmost byte is XOR'd with the correlating constant from RCON.
4. It takes the result of this and XORs it with the word from 8 positions ago (`words[i-8]`) to get the final result.

This occurs at every multiple of 8 (32/4 = 8) to match the length of the master key.

### The Encryption Loop (`AES256.java`)

The algorithm starts with XORing the grid with the raw master key, which instantly hides the plaintext before the heavy scrambling happens

Next, the algorithm runs a 14 round loop. The first 13 loops uses four transformations in the following order:

#### 1. SubBytes

- Using the numeric value of each byte in the 4x4 grid, it matches that value as an index to look up the replacement in `SBOX`, and swaps the old byte for a new one.
```
state[i][j] = Constants.SBOX[state[i][j]];
```
- This method simply loops through the 16 bytes of the matrix. If the numeric value of a byte is 84, it would use whatever value is in index 84 of the `SBOX` array to substitute the original value.
- The values in the table are non-linear, meaning that inputs next to each other would map to completely different and unrelated outputs, which gets rid of any predictability. 

#### 2. ShiftRows

```
for (int i = 1; i < 4; i++) {
    for (int j = 0; j < 4; j++) {
        temp[j] = state[i][(j + i) % 4];
    }
    // copies temp back to state
}
```
- This method loops through the rows of the matrix and slides the bytes to the left.
    - Row 0: No shift.
    - Row 1: Shifts left by 1 position.
    - Row 2: Shifts left by 2 positions.
    - Row 3: Shifts left by 3 positions.
- Bytes in the same column gets moved into comptlely different columns

#### 3. MixColumns

```
state[0][i] = xtime(s0) ^ xtime(s1) ^ s1 ^ s2 ^ s3;
```

- Each values of each vertical column of 4 bytes are mixed together using matrix multiplication.
- Multiplying bytes together can't overflow past 255, so the `Galois Field` (GF(2^8)) comes into play again.
- The `xtime()` method shifts all of the bits of a byte one slot to the left. If the leftmost bit is 1, shifting it would cause an overflow.
- To fix this, the byte is XORd with `0x1b` to force the value back into the 0-255 range.
- This step makes it so that just changing a single bit of your original plaintext would completely alter every byte of the ciphertext within a few rounds.

#### 4. AddRoundKey

```
state[i][j] = state[i][j] ^ roundKey[i][j];
```

- This takes the 4x4 matrix, now scrambled, and XORs it against the round key of this round.
- This step ensures that the matrix can only be reversed if you have the correct key.

#### The Final Round

- In the 14th round, the final round of encryption, the algorithm completely skips the `MixColumns` transformation and only runs `SubBytes`, `ShiftRows`, and `AddRoundKey`.
- This makes encryption and decrption symmetric, meaning every step forward in encryption can be a step backward for decryption purposes.

# SHA-256 by Jiewen

## 1. What is SHA-256?

SHA-256 is a **cryptographic hash function**. It takes an input of any length and produces a 256-bit or 32 byte output called a **digest**.

Three properties that make it useful:

- Computing the hash from input is easy but computing the input from the hash is impossible (although there are exceptions when the password itself is very easy and people can just use a [dictionary attack](https://en.wikipedia.org/wiki/Dictionary_attack) hashes, basically taking all the easy words and hashing the words to hopefully get a resultant hash that's the same as the password)
- The same input always produces the same output
- Changing one bit of the input completely changes the output. ex. `SHA-256("abc")` and `SHA-256("abd")` are entirely diff. This is referred to as the **avalanche effect**.

### Why we need it for Cipher Vault

A vault has to verify a user's password without ever storing the password itself
1. When the user sets a master password, we hash it with SHA-256 and store only the hash.
2. When the user logs in, we hash their input and compare hashes.
3. And, in the event that if the database file is stolen, the attacker gets the hash only, and because SHA-256 is one-way, you cannot get the password from the hash

ALSO: the hash is the **encryption key** for AES-256/ChaCha20 to encrypt.

## 2. The Whole Process

1. Take the raw input bytes
2. Pad them out until the total length is a multiple of 64 bytes
3. For each 64-byte block:
    - build a 64-word "message schedule" from the block
    - run 64 rounds of compression on the running state `H`
    - add the result back into `H`
4. Once every block has been processed, take `H` and serialize it into 32 bytes (convert the ints into a byte array, in a big-endian way, aka the most significant byte goes first)
5. Those 32 bytes are the digest

This pattern is called the **[Merkle–Damgård construction](https://en.wikipedia.org/wiki/Merkle%E2%80%93Damg%C3%A5rd_construction)**. Think of `H` like a running total. We start from the original values, then we take the current and the next 64 byte block and mix them together and do this successively until we get to the last block and that's our hash. Essentially, the final hash depends on every other byte. 


## 3 Constants

`src/sha/Constants.java` defines two types of constants. These are just "magic numbers" in SHA-256, and we use these to compute the final hash

### `H[8]` Initial Hash Values

These are the 8 words the running state starts at, before any input has been processed:

```
H[0] = 0x6a09e667
H[1] = 0xbb67ae85
H[2] = 0x3c6ef372
H[3] = 0xa54ff53a
H[4] = 0x510e527f
H[5] = 0x9b05688c
H[6] = 0x1f83d9ab
H[7] = 0x5be0cd19
```

These come from the **fractional parts of the sqrt()s of the first 8 primes** (2, 3, 5, 7, 11, 13, 17, 19), each multiplied by 2^32.

For example
- sqrt(2) is roughly 1.41421356…
- Take just the fractional part: `0.41421356…`
- Multiply by 2^32 -> 1,779,033,703
- In hex: `0x6a09e667` which is our H(0)

### `K[64]` Round Constants

We have 64 values, one per round of the compression function. It is derived the same way as above, but from **cube roots of the first 64 primes**.

### Nothing up my sleeve...

This is called a **["nothing up my sleeve" number](https://en.wikipedia.org/wiki/Nothing-up-my-sleeve_number)**. The designers of SHA-256 (the NSA) needed arbitrary 32-bit constants. If they had chosen random-looking values, people might go and think, "there must be a backdoor, these numbers are suspicious."

By using mathematical constants anyone can verify, the designers prove there was no such thing.

This appears very often in cryptography. MD5 is one such example.
---

## 4. The Helper Functions (FIPS 180-4 4.1.2)

`src/sha/SHA256.java` implements the six logical functions from that standard (above)

From [FIPS 180-4 4.1.2](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf) (pp. 10–11):

> SHA-224 and SHA-256 use six logical functions, where each function operates on 32-bit words, which are represented as x, y, and z. The result of each function is a new 32-bit word.

> Ch(x, y, z) = (x ∧ y) ⊕ (¬x ∧ z)

> Maj(x, y, z) = (x ∧ y) ⊕ (x ∧ z) ⊕ (y ∧ z)

> Σ₀{256}(x) = ROTR^2(x) ⊕ ROTR^13(x) ⊕ ROTR^22(x)

> Σ₁{256}(x) = ROTR^6(x) ⊕ ROTR^11(x) ⊕ ROTR^25(x)

> σ₀{256}(x) = ROTR^7(x) ⊕ ROTR^18(x) ⊕ SHR^3(x)

> σ₁{256}(x) = ROTR^17(x) ⊕ ROTR^19(x) ⊕ SHR^10(x)

Where `ROTR^n` is right-rotate by n bits and `SHR^n` is right-shift by n bits (both defined in 4.1.1 of the same pdf). The `{256}` part is arbitrary but basically it just differentiates it from SHA-512, which is similar but uses different rotation amounts.

Also: 
- `∧` is bitwise AND (Java: `&`)
- `⊕` is bitwise XOR (Java: `^`)
- `¬` is bitwise NOT (Java: `~`)
- `Ch` and `Maj` are the gatekeepers use AND and NOT instead of just XOR, so you literally can't reverse it using algebra so it's what allows it to be a hash.
- The big sigmas (`Σ₀`, `Σ₁`) are take three rotated copies of the same value and XOR them all together, so this allows for one changed byte or character to cause a massive change in the end result hash.
- The small sigmas (`σ₀`, `σ₁`) get used in the message schedule (section 6). The compression function needs 64 different word inputs per block, but a 64-byte block only directly gives us **16 words**. So we use the small sigmas to scramble earlier words together and get the other 48.
- `ROTR` and `SHR` are just sliding bits around, and they're super light on CPUs and were designed to be like that so SHA-256 would be easy to perform

In our Java implementation we get `ch`, `maj`, `bigSigma0`, `bigSigma1`, `smallSigma0`, `smallSigma1` plus our own `rotr(x, n)` helper that the four sigma functions all call (so we don't have to the rotation expression six separate times)

### little Java detail I found

Java has two right-shift operators. 

`>>` is arithmetic, and it fills the gap on the left when it shifts to the right with copies of the leftmost bit. If `x` is positive, zeros come in, but ones come in if it's negative. However, `>>>` fills the gap on the left with zeros no matter what. 

Java's [`>>>` operator](https://docs.oracle.com/javase/specs/jls/se17/html/jls-15.html#jls-15.19) is **unsigned right shift**. We use `>>>` everywhere we need a logical shift, because Java ints are signed. Without `>>>`, any value with the high bit set would break the calculations. This was the most common implementation bug I encountered in Java SHA-256. For instance, numbers in `K` like `0xbb67ae85` for ex. look like negative numbers to Java even though SHA-256 just thinks of them as a pattern that's 32 bits. So we need to use `>>>`.

---

## 5. Padding (FIPS 5.1.1)

The compression function only handles 64-byte things, but since most messages obviously aren't a clean multiple of 64, we have to pad the messages always to some multiple of 64.

1. Append `0x80`.
2. Add zeros until the length is `≡ 56 (mod 64)`.
3. Append the original bit-length as 8 bytes.

The result is always a multiple of 64 bytes.

---

## 6. Message Schedule (FIPS 6.2.2)

For each 64-byte block, we build a `W[]`, which is always 64 × 32-bit words for the compression rounds.

- **W[0..15]**: read directly from the block as 16 big-endian 32-bit integers.
- **W[16..63]** is extended via `W[i] = σ₁(W[i-2]) + W[i-7] + σ₀(W[i-15]) + W[i-16]`.

Essentially, we can turn 16 words into 64 words using the small sigma functions to basically mix in earlier words into later words, which allows us to scramble the letters better such that one letter change will change the entire resultant hash.

---

## 7. The Compression Function

The compression takes the current 8-word state plus one block's `W[]`, runs 64 rounds and adds the result back into the state. Put simply, we use compression to do the actual hashing part. Everything else was just a setup until now, but here we take the 8 word state and the blocks of message schedule and mixes everything together using our various functions and after many rounds (64 of them) it adds the result back into the state.

Copy state into 8 variables `a-h`:
```java
int a = state[0], b = state[1], ..., h = state[7];
```

Each round computes the new `a` and `e` using all the different functions we have:
```java
nextA = h + bigSigma1(e) + ch(e,f,g) + K[i] + W[i] + bigSigma0(a) + maj(a,b,c)
nextE = d + h + bigSigma1(e) + ch(e,f,g) + K[i] + W[i]
```

Then we shift all 8 working variables down by one position:
```java
h = g;  g = f;  f = e;  e = nextE;
d = c;  c = b;  b = a;  a = nextA;
```

After 64 rounds, **add** the working variables back into state:
```java
state[0] += a;  state[1] += b;  ... state[7] += h;
```

The `+=` is important, the function is called "compress" and not something like "encrypt". The message builds up from each block to get our final hash

---

## 8. `hash(byte[]) -> byte[32]`

Our hash function is what runs through everything, all our functions, and returns the final output.

```java
public static byte[] hash(byte[] input) {
    byte[] padded = pad(input);
    int[] state = new int[8];
    System.arraycopy(Constants.H, 0, state, 0, 8);
    for (int i = 0; i < padded.length; i += 64) {
        int[] W = messageSchedule(padded, i);
        compress(state, W);
    }
    byte[] output = new byte[32];
    for (int i = 0; i < 8; i++) wordToBytes(state[i], output, i * 4);
    return output;
}
```

test: `SHA-256("abc")` produces
```
ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
```

This matched what I got using Java's MessageDigest for sha-256.

---

## 10. Testing

`testing/TestSHA256.java` has three categories:
- **Static cases**: known inputs including the `"abc"` test and boundary tests at 55, 56, 64 bytes for edge cases.
- **Random cases**: byte arrays 10–300 bytes that we picked at random using secureRandom
- **Custom cases**: strings you can type in directly into the CLI

Every case gets compared against Java's built-in `MessageDigest` as the reference. 

```bash
make test-sha
```
Most recent run states **15 / 15 PASS**, so our code must be working

---

## 11. Refs

- [NIST FIPS 180-4 - Secure Hash Standard](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf) 4.1.2 (helpers), 4.2.2 (constants), 5.1.1 (padding), 6.2.2 (schedule + compression)
- [Wikipedia - SHA-2](https://en.wikipedia.org/wiki/SHA-2)

# ChaCha20 by Gephel

## 1. What is ChaCha20?

ChaCha20 is a **stream cipher**. Unlike a block cipher (like AES) that encrypts fixed-size chunks, a stream cipher generates a continuous **keystream** (a pseudorandom sequence of bytes) and XORs it with the input byte by byte.

That means encryption and decryption are the exact same operation: XOR the data with the keystream. So if `plaintext XOR keystream = ciphertext`, then `ciphertext XOR keystream = plaintext`.

ChaCha20 was designed by Daniel J. Bernstein in 2008 as a faster, safer alternative to AES in software and subsequently is used in TLS 1.3, WireGuard, and SSH.

Three inputs define a ChaCha20 operation:
- **Key**: 32 bytes (256 bits) of secret
- **Nonce**: 12 bytes, unique per message (never reuse with the same key)
- **Counter**: a block number, starting at 1 per the IETF standard ([RFC 8439](https://datatracker.ietf.org/doc/html/rfc8439))

### Why we need it for Cipher Vault

AES and ChaCha20 solve the same problem but have tradeoffs. ChaCha20 is a good alternative to AES especially on hardware without AES acceleration.
Additionally, having both in the vault gives the user the freedom to choose and emphasizes that the security can be reached through different cryptographic designs.

---

## 2. The Whole Proccess

1. Build a 4×4 matrix of 32-bit words (the **initial state**) from the constants, key, counter, and nonce

2. Copy the state to a **working state** and apply 20 rounds of mixing

3. Add the working state back to the original (mod 2^32) to produce a **64-byte keystream block**

4. XOR up to 64 bytes of input with the keystream block

5. Increment the counter and repeat for the next 64 bytes

6. The output is the same length as the input, meaning no padding needed
The nonce is prepended to the ciphertext on encrypt and stripped back off on decrypt, so the receiver always knows which nonce was used.

---

## 3. The Initial State

Each keystream block starts from a 4×4 matrix of 32-bit words:

```
[ sigma[0]  sigma[1]  sigma[2]  sigma[3] ]   <- constants
[ key[0]    key[1]    key[2]    key[3]   ]   <- key words 0-3
[ key[4]    key[5]    key[6]    key[7]   ]   <- key words 4-7
[ counter   nonce[0]  nonce[1]  nonce[2] ]   <- counter + nonce

```

The **sigma constants** are the ASCII bytes of `"expand 32-byte k"` (equal to 4 32-bit little-endian words). Like SHA-256's H values, these are "nothing up my sleeve" numbers. Everyone can verify them and so there is no backdoor possible.

## 4. The Quarter Round

The quarter round is the core operation of ChaCha20, as everything else is jsut calling it but in different orders. It takes four 32-bit words `a, b, c, d` and mixes them using the **ARX** (Add-Rotate-XOR) design.

```
a += b;  d ^= a;  d <<<= 16
c += d;  b ^= c;  b <<<= 12
a += b;  d ^= a;  d <<<= 8
c += d;  b ^= c;  b <<<= 7
```

`<<<=` signifies a **left rotate** (think of Caesar ciphers), where bits from left are put on the right.

ARX is chosen as it is extremely fast, especially on modern CPUs, as the 3 instructions are all single-cycle and fast. One difference is that there aren't lookup tables (AES has S-box) so ChaCha hhs no [cache-timing vulnerabilities](https://en.wikipedia.org/wiki/Timing_attack) (attackers can infer from time taken to complete tasks).

---

## 5. The 20 Rounds

Each full block applies the quarter round **20 times** in two alternating patterns:

**Column rounds**: apply to the four columns of the state matrix:
```
QR(state, 0, 4, 8, 12)
QR(state, 1, 5, 9, 13)
QR(state, 2, 6, 10, 14)
QR(state, 3, 7, 11, 15)
```
 
**Diagonal rounds**: apply to the four diagonals:
```
QR(state, 0, 5, 10, 15)
QR(state, 1, 6, 11, 12)
QR(state, 2, 7, 8, 13)
QR(state, 3, 4, 9, 14)
```

One column-round + one diagonal-round is called a **double round** which ChaCha20 does 10 of (hence 20 rounds); the alternating pattern ensures that every word influences every other word.

After all 20 rounds, the working state is added back to the initial state (mod 2^32). This last step of final additon allows for rounds to be irreversible and keep keystream unpredictable.

---
 
# Vault CLI by Gephel

## 1. What is the Vault?

The Vault is a **command-line password manager** that ties all three cryptographic primitives together into a simple, but real application. You authenticate with a master password, and the vault stores your site credentials, with each one  being encrypted with either AES-256 or ChaCha20.

---
 
## 2. How to Use It
```bash
$ make vault
```

On first run you'll be prompted to create a master password (subsequent runs will request same master password).

```
==============================
       Cipher Vault
==============================
 
Master password: •••••••••••••••••••••
 
Vault unlocked -- 0 entry/entries.
Type 'help' for commands.
 
> help
 
  add  — store a new credential
  list — list all stored sites
  get  — retrieve a credential by site
  del  — delete a credential by site
  quit — exit and lock the vault
```

**Adding a credential:**
```
> add
 site:  github.com
 username: gkogyal
 password: MyP@ssword!1
  cipher [1 = ChaCha20  2 = AES-256]: 1
Successfully stored with CC20.
```


**Listing and retrieving:**
```
> list

  #     SITE                      USERNAME              CIPHER
  1     github.com                gkogyal               CC20
```

To reset the vault entirely:
```bash
$ make vault-clear
```

To inspect the raw database:
```bash
$ sqlite3 src/vault/vault.db
sqlite> SELECT * FROM entries;
sqlite> SELECT * FROM meta;
```

## 3. How the Algorithms Connect

1. **SHA-256** hashes the master password → 32-byte hex string stored in the `meta` table for login verification

2. That same hash is used directly as the **32-byte encryption key** for both ciphers

3. When you `add` a credential, the password is encrypted and stored as hex in `entries`

4. When you `get` a credential, the ciphertext is decrypted on the fly using the derived key

This means that the database stores no plaintext, only hashes and ciphertext.

## 4. Database

The vault uses **SQLite** (via `sqlite-jdbc`) with two tables:
```sql
CREATE TABLE meta (
    id          INTEGER PRIMARY KEY CHECK (id = 1),
    master_hash TEXT NOT NULL
);
 
CREATE TABLE entries (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    site        TEXT NOT NULL UNIQUE,
    username    TEXT NOT NULL,
    ciphertext  TEXT NOT NULL,
    ciphertype  TEXT NOT NULL CHECK (ciphertype IN ('AES','CC20'))
);
```

---

## 5. Strengths and Weaknesses

### Strengths
- **No plaintext stored anywhere**
- **Two cipher options**
- **Password strength mandate**
- **Open-source primitives**
- **SQL Injection Protection**

### Weaknesses
- **Simple key derivation**: The masterpassword's SHA-256 is the key, but real vaults use a proper **KDF** like [PBKDF2](https://en.wikipedia.org/wiki/PBKDF2)
- **Simple database**: No multiuser support, backups, or syncs
- **Lack of salting**: Allows for effective rainbow lists

---