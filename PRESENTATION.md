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
