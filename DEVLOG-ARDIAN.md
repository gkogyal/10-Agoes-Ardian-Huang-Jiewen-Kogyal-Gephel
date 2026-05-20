# Devlog - Ardian Agoes

## 2026-05-19

* Fixed an issue where  Git conflict markers (`<<<<<<< HEAD`, `=======`, `>>>>>>>`) made during a branch merge messed up our code and halted progress.
* Corrected a type error in the `encrypt` parameters and fixed a loop boundary in `decrypt` where intermediate rounds were incorrectly numbered.
* Wrote a tester in `AESTester.java` inside the `testing/` directory.

## 2026-05-18

* Fixed errors in `AESKeySchedule.java`, including missing parentheses and XOR operators.
* Completed the galois field multiplication logic for both `mixColumns` and `inverseMixColumns` for matrix diffusion using Rijndael polynomials.
* Wrote the main `encrypt` block loop, orchestrating the 14 rounds of transformations (SubBytes, ShiftRows, MixColumns, AddRoundKey).
* Implemented the `decrypt` method, reversing the encryption process and applying the round keys in reverse order.

## 2026-05-15

* Updated `RCON` constants and wrote `subWord()` for SBOX substitution purposes;
* Wrote `expandKey()` and `rotWord()`. The first helps with word generation to provide roundkeys, and the second rotates 4 word bytes to the left.

## 2026-05-14

* Wrote `xTime()`, a helper function that is used for the mixcolumns step of AES.
* Wrote `multiply9()`, `multiply11()`, `multiply13()`, and `multiply14()` that are helper functions for reversinng the mixcolumns step.

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
