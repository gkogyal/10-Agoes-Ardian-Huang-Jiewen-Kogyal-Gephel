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
