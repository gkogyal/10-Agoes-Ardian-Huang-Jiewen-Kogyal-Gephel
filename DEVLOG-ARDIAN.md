# Devlog - Ardian Agoes

## 2026-05-26

* Completed my portion of `PRESENTATION.md` documenting the process of AES256 encryption.

## 2026-05-25

* Completed the `AESCipher.java` wrapper and fixed issues across our directory where there were references to the wrong AES class.
* Implemented a new verbose mode in encrypting and decrypting in `AESCipher.java` for testing and seeing each step.
* Fixed issues in TestAES256.java and removed unnecessary lines in `ChaCha20.java`.

## 2026-05-23

* Implemented unpadding logic in `Padding.java` for decryption and fixed error in logic (instead of appending 0s, append padding length)
* Added new `AesCIPHER.java` as a wrapper class to connect the padding and AES cipher. 

## 2026-05-22

* Implemented padding logic in `Padding.java` to guarantee the input length is a multiple of 16 bytes to avoid out of bound exceptions.

## 2026-05-20

* Fixed even more git merge errors and restored missing code in `ChaCha20.java`
* Created initial `Padding.java` file 

## 2026-05-19

* Fixed an issue where Git conflict markers (`<<<<<<< HEAD`, `=======`, `>>>>>>>`) made during a branch merge messed up our code and halted progress.
* Corrected a type error in the `encrypt` parameters and fixed a loop boundary in `decrypt` where intermediate rounds were incorrectly numbered.
* Wrote a tester in `TestAES256.java` inside the `testing/` directory.

## 2026-05-18

* Fixed errors in `AESKeySchedule.java`, including missing parentheses and XOR operators.
* Completed the galois field multiplication logic for both `mixColumns` and `inverseMixColumns` for matrix diffusion using Rijndael polynomials.
* Wrote the main `encrypt` block loop, orchestrating the 14 rounds of transformations (SubBytes, ShiftRows, MixColumns, AddRoundKey).
* Implemented the `decrypt` method, reversing the encryption process and applying the round keys in reverse order.

## 2026-05-15

* Updated `RCON` constants and wrote `subWord()` for SBOX substitution purposes.
* Wrote `expandKey()` and `rotWord()`. The first helps with word generation to provide roundkeys, and the second rotates 4 word bytes to the left.

## 2026-05-14

* Wrote `xTime()`, a helper function that is used for the mixcolumns step of AES.
* Wrote `multiply9()`, `multiply11()`, `multiply13()`, and `multiply14()` that are helper functions for reversing the mixcolumns step.

## 2026-05-12
* Wrote `shiftrows()` and `inverseShiftRows()`, which move the bytes to different columns for further mixing. The inverse is for decryption purposes.
* Wrote `addRoundKey`, which applies XOR to the state with the round keys. This is the only part where the secret key is XOR'ed with the state and providing security. 

## 2026-05-11

* Finished `Constants.java`, adding the `INVERSE_SBOX` and `RCON` constants. 
* Wrote `bytesToState` and `stateToBytes` in `AES256.java`, mapping 128-bit blocks into a 4x4 matrix where the first four bytes occupy the first column. 
* Wrote the `subBytes` and `invSubBytes` methods for non-linear substitution using the S-Box tables.

## 2026-05-10

* Created my own branch.
* Create the directories for our project: `src/aes`, `src/sha`, `src/vault`, `docker`, and `testing`.
* Implemented the **Rijndael S-Box** as a 1D integer array in `Constants.java`.
* Merged with main.
