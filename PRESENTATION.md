# SHA-256 by Jiewen Huang

## 1. What is SHA-256?

SHA-256 is a **cryptographic hash function**. It takes an input of any length and produces a 256-bit or 32 byte output called a **digest**.

Three properties that make it useful:

- Computing the hash from input is easy but computing the input from the hash is impossible (although there are exceptions when the password itself is very easy and people can just use a [dictionary attack](https://en.wikipedia.org/wiki/Dictionary_attack) hashes, basically taking all the easy words and hashing the words to hopefully get a resultant hash that's the same as the password)
- The same input always produces the same output
- Changing one bit of the input completely changes the output. ex. `SHA-256("abc")` and `SHA-256("abd")` are entirely diff. This is referred to as the **avalanche effect**.
