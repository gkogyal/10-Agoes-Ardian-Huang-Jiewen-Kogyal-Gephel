# Project Proposal -- Secure Credential Vault

**Cipher Vault** will involve a from-scratch implementation of SHA-256, AES-256, and ChaCha20 in a working credential vault with command line interface that demonstrates how cryptographic algorithms protect sensitive data.

Each algorithm is built from basic mathematical/bitwise operators and has a live debug mode with verbose option to visualize the process.

---

## Work Breakdown

- Ardian — AES-256
- Jiewen — SHA-256 
- Gephel - ChaCha20

| Component | Primary | Secondary |
|---|---|---|
| SHA-256 | Jiewen | Gephel (review) |
| AES-256 | Ardian | Jiewen (review) |
| ChaCha20 | Gephel | Ardian (review) |
| Vault CLI | Gephel | Ardian |
| Test Suite | Jiewen | Gephel |
| MD Files | All | — |

---

## Specific Deliverables

### 1. SHA-256 Implementation (`src/sha/`)

A from-scratch implementation of SHA-256, a hash function. It takes an arbitrary input and produces a fixed 32-byte digest which is quick to calculate but hard to reverse-engineer.

### 2. AES-256 Implementation (`src/aes/`)
A from-scratch implementation of AES-256, a block cipher. It encrypts/decrypts 16-byte blocks using a 32-byte key and 14 rounds of transformations.
The key schedule (AESKeySchedule.java) expands the key into 'round keys', and AESCipher.java handles padding and chaining for arbitrary-length input.

### 3. ChaCha20 Implementation (`src/chacha/`)
A from-scratch implementation of ChaCha20, a stream cipher. It generates a keystream from a 32-byte key and 12-byte nonce and then ZXORs it with the input.
QuarterRound.java implements the core 'ARX operation'.

### 4. Vault Application (`src/vault/`)
A terminal credential vault (or simply password manager) designed to tie all three algorithms together.
The master password is hashed with SHA-256 which becomes the encryption key. 
Stored passwords are encrypted with either AES-256 or ChaCha20 (user's choice) and stored in plaintext within a local SQLite database. 

### 5. Test Suite (`testing/Test*`)
Validates each algorithm's encryption/decryption is tested against Java's standard library (javax.crypto) across static, random, and custom inputs.

### 6. Verbose (`testing/Verbose*`)
Interactive trace tools exist within the source code but can be visualized with the Verbose*.java files.
