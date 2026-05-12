# Devlog - Jiewen Huang

## 2026-05-10

* Added `src/sha/Constants.java` with the SHA-256 initial hash values (`H`) and the 64 round constants (`K`) from FIPS 180-4. These are the fixed inputs the compression function will need next.
* Matched the package/formatting style of Ardian's `src/aes/Constants.java` to make sure everything is consistent
* Verified the file compiles with `javac`

## 2026-05-11

* Updated `PROPOSAL.md` with updated  proposal and added markdown styling elements to proposal

* Filled in `src/sha/SHA256.java` with the seven SHA-256 helper functions per FIPS 180-4: `rotr`, `ch`, `maj`, `bigSigma0`, `bigSigma1`, `smallSigma0`, `smallSigma1`. These are the bitwise primitives the compression loop will call once that's written
* Compiled cleanly with `javac`
