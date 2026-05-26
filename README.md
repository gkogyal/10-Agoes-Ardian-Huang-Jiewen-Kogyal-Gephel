# Secure Credential Vault

A command-line password manager that implements **SHA-256**, **AES-256**, and **ChaCha20** from scratch in Java without cryptographic libraries.

Built to demonstrate the inner workings of cryptographic primitives (well-established, low-level cryptographic algorithms):
1. SHA-256: contemporary hashing
2. AES-256: symmetric block encryption
3. ChaCha20: modern stream cipher encryption.

> See also: [PRESENTATION.md](PRESENTATION.md) | [PROPOSAL.md](PROPOSAL.md)

---

### Table of Contents

- [Project Description](#project-description)
- [Repository Structure](#repository-structure)
- [How to Build and Run](#how-to-build-and-run)
- [References and Resources](#references-and-resources)

---

## Project Description

The project has four parts:

### 1. **Implementation**: Core cryptography algorithms (AES, SHA-256, ChaCha20)
Fundamental **cryptographic primitives** built from scratch in Java.

 - SHA-256 → hashing (one-way integrity to detect tampering and securely authenticate)
 - AES-256 → block cipher encryption (block-based encryption for hiding confidential info)
 - ChaCha20 → stream cipher encryption (continuous encryption, often faster/safer as alternative to AES)

### 2. **Application**: Vault CLI
Minimal, functional **credential vault** that stores site/username/password entries.

 - password vault command-line interface
 - master password authentication and credential storage+retrieval
 - encryption/decryption integration:
   - SHA for master password
   - stored passwords encrypted with AES-256 or ChaCha20, using a key derived directly from the SHA-256 hash

### 3. **Visualization**: Terminal-based tracing
**Debugging/educational mode** to show internals of AES, SHA-256, and ChaCha20 step-by-step

 - SHA-256 round-by-round state evolution
 - AES state transformations (SubBytes, ShiftRows, MixColumns, etc)
 - ChaCha20 state and quarter-round progression
 - test suites for showing accuracy in a large number of cases
 - optional verbose CLI trace mode for debugging/visualizing

---

## Repository Structure

```bash

.
├── README.md
├── PROPOSAL.md
├── PRESENTATION.md
├── DEVLOG-ARDIAN.md
├── DEVLOG-JIEWEN.md
├── DEVLOG-GEPHEL.md
├── makefile				  
├── lib/
│   └── sqlite-jdbc.jar			  # needed for vault
├── src/
│   │
│   ├── aes/
│   │   ├── AES256.java           # AES-256 implementation
│   │   ├── Padding.java          # AES-256 padding
│   │   ├── AESKeySchedule.java   # 15 Rijndael key expansions
│   │   ├── AESCipher.java        # AES-256 encryption
│   │   └── Constants.java        # S-Box, Inverse S-Box, RCON values
│   │
│   ├── sha/
│   │   ├── SHA256.java           # SHA-256 implementation
│   │   └── Constants.java        # H and K constants; initial hash values and round constants
│   │
│   ├── vault/
│   │   ├── Vault.java            # main program: CLI app managing interaction, storage, operations
│   │   └── VaultEntry.java       # data model/container for a record
│   │
│   └── chacha/
│       ├── ChaCha20.java		  # ChaCha20 implementation
│       ├── QuarterRound.java	  # Helper function for ChaCha20
│       └── Constants.java		  # Sigma constants and utility values
│
└── testing/
    ├── Test.java           	  # All-in-one test suite
    ├── TestSHA256.java           # SHA-256 test suite
    ├── TestAES256.java           # AES-256 test suite
    ├── TestChaCha20.java         # ChaCha20 test suite
    ├── TestSuite.java            # Test suite abstract
    ├── TestUtils.java            # Utilities
    ├── VerboseSHA256.java        # SHA-256 trace
    ├── VerboseAES256.java        # AES-256 trace
    └── VerboseChaCha20.java      # ChaCha20 trace

```

---

## How to Build and Run

Make is used for the ease of users.

### 1. Test Algorithms

Users can view the test suite for an algorithm with the following commands.
```bash
$ make test-aes
$ make test-sha
$ make test-chacha
```

For ease, an all-in-one test suite exists.
```
$ make test
```

### 2. Run the Vault

Users can start the vault and will be given sufficient instructions to navigate.
```bash
$ make vault
```

Users have the ability to reset the vault.
```bash
$ make vault-clear
```

The option is available for users would like to access the SQLite database directly
```bash
$ sqlite3 src/vault/vault.db
sqlite>  
```

Suggested commands:
```bash
sqlite> SELECT * from entries;
sqlite> SELECT * from meta;
```

### 3. Trace Algorithms

Users have the option to trace an algorithm exists with make.
```bash
$ make verbose-aes
$ make verbose-sha
$ make verbose-cha
```

This can be used to find the actual ciphertext of each cryptographic algorithm.

---

## References and Resources

- [Every Algorithm - ChaCha20 Stream Cipher](https://every-algorithm.github.io/2025/06/19/chacha20.html)
- [PKCS#7 Padding for AES](https://node-security.com/posts/cryptography-pkcs-7-padding/)
- [AMD - ChaCha20 Algorithms](https://docs.amd.com/r/en-US/Vitis_Libraries/security/guide_L1/internals/des.html_0)
- [Implementing Advanced Encryption Standard](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.197.pdf)
- [Wikipedia - SHA-2](https://en.wikipedia.org/wiki/SHA-2) pseudocode, helper function defs used in `src/sha/SHA256.java`
- [NIST FIPS 180-4 - Secure Hash Standard](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf) Section 4.1.2
 defines the helper functions and Section 4.2.2 lists the H and K constants used in `src/sha/Constants.java`
