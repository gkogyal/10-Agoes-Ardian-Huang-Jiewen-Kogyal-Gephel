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
