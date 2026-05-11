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
│   │   ├── AES.java            # AES-256 implementation
│   │   ├── AESKeySchedule.java # 15 Rijndael key expansions
│   │   └── Constants.java      # S-Box, Inverse S-Box, RCON values
│   │
│   ├── sha/
│   │   ├── SHA256.java         # SHA-256 implementation
│   │   └── Constants.java      # H and K constants; initial hash values and round constants
│   │
│   ├── vault/
│   │   ├── Vault.java          # main program: CLI app managing interaction, storage, operations
│   │   └── VaultEntry.java     # data model/container for a record
│   │
│   └── chacha20/
│       ├── ChaCha20.java		# ChaCha20 implementation
│       ├── QuarterRound.java	# Helper function for ChaCha20 
│       └── Constants.java		# Sigma constants and utility values
│
├── testing/
│   ├── TestSHA256.java         # SHA-256 test suite
│   └── TestAES.java            # AES-256 test suite
│
└── docker/
    ├── Dockerfile              # Vulnerable container for attack demo
    └── README.md               # Instructions for the CTF challenge

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
