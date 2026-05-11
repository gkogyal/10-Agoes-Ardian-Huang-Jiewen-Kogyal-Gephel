# Project Proposal -- Secure Credential Vault

**Cipher Vault** will involve a from-scratch implementation of SHA-256, AES-256, and ChaCha20 in a working credential vault with command line interface that demonstrates how cryptographic algorithms protect sensitive data.

Each algorithm is built from basic mathematical/bitwise operators and has a live debug mode with verbose option to visualize the process.

---

## Work Breakdown

- Ardian — AES-256
- Jiewen — SHA-256 
- Gephel - ChaCha20
- everyone - Docker vulnerability demo with CTF challenge, visualization, and CLI application layer.

| Component | Primary | Secondary |
|---|---|---|
| SHA-256 | Jiewen | Gephel (review) |
| AES-256 | Ardian | Jiewen (review) |
| ChaCha20 | Gephel | Ardian (review) |
| Vault CLI | Member A | Member B |
| Test Suite | Member A | Member B |
| Docker Demo | Member A | Member B |
| MD Files | All | — |

---

## Specific Deliverables

### 1. SHA-256 Implementation (`src/sha/SHA256.java`)

### 2. AES-256 Implementation (`src/aes/AES.java`, `src/aes/AESKeySchedule.java`)

### 3. ChaCha20 Implementation (`src/chacha/ChaCha20.java`, `src/chacha/QuarterRound.java`)

### 4. Vault Application (`src/vault/Vault.java`)

### 5. Test Suite (`testing/*`)

### 6. Docker Vulnerability Demo (`docker/*`)
