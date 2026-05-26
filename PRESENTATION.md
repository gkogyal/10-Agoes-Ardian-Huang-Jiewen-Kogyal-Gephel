# SHA-256 by Jiewen Huang

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
