# Devlog - Ardian Agoes

## 2026-05-11

* Finished `Constants.java`, adding the `INVERSE_SBOX` and `RCON` contants. 
* Wrote `bytesToState` and `stateToBytes` in `AES.java`, mapping 128-bit blocks into a 4x4 matrix where the first four bytes occupy the first column. 
* Wrote the `subBytes` and `invSubBytes` methods for non-linear substitution using the S-Box tables.

## 2026-05-10

* Created my own branch.
* Create the directories for our project: `src/aes`, `src/sha`, `src/vault`, `docker`, and `testing`.
* Implemented the **Rijndael S-Box** as a 1D integer array in `Constants.java`.
* Merged with main.