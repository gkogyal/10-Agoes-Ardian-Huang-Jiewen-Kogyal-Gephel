# Secure Credential Vault

A command-line password manager that implements **SHA-256**, **AES-256**, and **ChaCha20** from scratch in Java without cryptographic libraries.

Built to demonstrate the inner workings of cryptographic primitives (well-established, low-level cryptographic algorithms): 
1. AES-256: contemporary hashing
2. AES-256: symmetric block encryption
3. ChaCha20: modern stream cipher encryption.

> See also: [PRESENTATION.md](PRESENTATION.md) | [PROPOSAL.md](PROPOSAL.md)

---

### Table of Contents

- [Project Description](#project-description)
- [Features](#features)
- [Repository Structure](#repository-structure)
- [How to Build and Run](#how-to-build-and-run)
- [Running the Tests](#running-tests)
- [Docker: Vulnerability Demo](#docker-vulnerability-demo)
- [Algorithm Summaries](#algorithm-summaries)
- [Presentation](#presentation)
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

### 3. **Testing**: CTF security environment (Docker lab)
Intentionally **vulnerable environment** as a container for an attack simulation.

 - Docker-based isolated lab environment using Digital Ocean droplets
 - deliberate misconfigurations or vulnerabilities for CTF-style exploitation exercises (specifics are WIP)
 - goal is to demonstrate real-world security failure scenarios

### 4. **Visualization**: Terminal-based tracing
Debugging/educational mode to show internals of AES, SHA-256, and ChaCha20 step-by-step

 - SHA-256 round-by-round state evolution
 - AES state transformations (SubBytes, ShiftRows, MixColumns, etc)
 - ChaCha20 state and quarter-round progression
 - test suites for 
 - optional verbose CLI trace mode for debugging/visualizing

---

## Features

---

## Repository Structure (WIP)

```bash

.
├── README.md
├── PROPOSAL.md
├── PRESENTATION.md
├── DEVLOG-ARDIAN.md
├── DEVLOG-JIEWEN.md
├── DEVLOG-GEPHEL.md
├── src/
│   │
│   ├── aes/
│   │   ├── AES256.java           # AES-256 implementation
│   │   ├── AESKeySchedule.java   # 15 Rijndael key expansions
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
├── testing/
│   ├── TestSHA256.java           # SHA-256 test suite
│   ├── TestAES256.java           # AES-256 test suite
│   └── TestChaCha20.java         # ChaCha20 test suite
│
└── docker/
    ├── Dockerfile                # Vulnerable container for attack demo
    └── README_CTF.md             # Instructions for the CTF challenge

```

---

## How to Build and Run

### 1. Compile

### 2. Run the Vault

---

## Running Tests

---

## Docker: Vulnerability Demo

---

## References and Resources

- [Every Algorithm - ChaCha20 Stream Cipher](https://every-algorithm.github.io/2025/06/19/chacha20.html)
- [AMD - ChaCha20 Algorithms](https://docs.amd.com/r/en-US/Vitis_Libraries/security/guide_L1/internals/des.html_0)
- [Implementinng Advanced Encryption Standard](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.197.pdf)
