# Devlog - Ardian Agoes

## 2026-05-13


## 2026-05-12
* Wrote `shiftrows()` and `inverseShiftRows()`, which move the bytes to different columns for further mixing. The inverse is for decryption purposes.
* Wrote `addRoundKey`, which applies XOR to the state with the round keys. This is the only part where the secret key is XOR'ed with the state and providing security. 

## 2026-05-11

* Finished `Constants.java`, adding the `INVERSE_SBOX` and `RCON` constants. 
* Wrote `bytesToState` and `stateToBytes` in `AES.java`, mapping 128-bit blocks into a 4x4 matrix where the first four bytes occupy the first column. 
* Wrote the `subBytes` and `invSubBytes` methods for non-linear substitution using the S-Box tables.

## 2026-05-10

* Created my own branch.
* Create the directories for our project: `src/aes`, `src/sha`, `src/vault`, `docker`, and `testing`.
* Implemented the **Rijndael S-Box** as a 1D integer array in `Constants.java`.
* Merged with main.
