package aes;

public class AES256{

  /*
  The encrypt() function is the main loop AES-256 encryption.
  It takes a 16-byte plaintext block and the 60 word expanded key array and processes the data through 14 rounds.
  It use an initial AddRoundKey step, runs 13 rounds of combined transformations (subBytes, shiftRows, mixColumns, addRoundKey)
  It  finishes with a 14th round that omits the mixColumns step according to the NIST specification.
  */

  public byte[] encrypt(byte[] plaintext, int eKey){
    int[][] state = bytesToState(plaintext);
    addRoundKey(state, getRoundKey(expandedKey, 0));
    for (int i = 0; i < 13; i++){
      subBytes(state);
      shiftRows(state);
      mixColumns(state);
      addRoundKey(state, getRoundKey(expandedKey, round));
    }
    subBytes(state);
    shiftRows(state);
    addRoundKey(state, getRoundKey(expandedKey, 14));
    return stateToBytes(state);
  }

  /*
  Treats the state as a 4x4 matrix of bytes and mixes the columns together. Each column is treated as a polynomial and multiplied by a fixed polynomial.
  This is the third step in the AES encryption process and provides diffusion.
  */

  public void mixColumns(int[][] state){
    for (int i = 0; i < 4; i++){
      int s0 = state[0][i];
      int s1 = state[1][i];
      int s2 = state[2][i];
      int s3 = state[3][i];
      state[0][i] = xtime(s0) ^ xtime(s1) ^ s1 ^ s2 ^ s3;
      state[1][i] = s0 ^ xtime(s1) ^ xtime(s2) ^ s2 ^ s3;
      state[2][i] = s0 ^ s1 ^ xtime(s2) ^ xtime(s3) ^ s3;
      state[3][i] = xtime(s0) ^ s0 ^ s1 ^ s2 ^ xtime(s3);
    }
  }

  /*
  The inverseMixColumns() function is used in the decryption process.
  It takes the state and applies the inverse MixColumns transformation, reversing the diffusion provided by mixColumns() during encryption.
  */

  public void inverseMixColumns(int[][] state){
    for (int i = 0; i < 4; i++){
      int s0 = state[0][i];
      int s1 = state[1][i];
      int s2 = state[2][i];
      int s3 = state[3][i];
      state[0][i] = multiply14(s0) ^ multiply11(s1) ^ multiply13(s2) ^ multiply9(s3);
      state[1][i] = multiply9(s0) ^ multiply14(s1) ^ multiply11(s2) ^ multiply13(s3);
      state[2][i] = multiply13(s0) ^ multiply9(s1) ^ multiply14(s2) ^ multiply11(s3);
      state[3][i] = multiply11(s0) ^ multiply13(s1) ^ multiply9(s2) ^ multiply14(s3);
    }
  }

  /*
    AES processes data in 16 byte or 128 bit blocks. A state is a 4x4 matrix of bytes, so we need to convert the input byte array into a state.
    The first 4 bytes of the input are the first column of the state, the next 4 bytes are the second column, and so on.
    The bitwise AND with 0xFF converts the byte to an unsigned int, which is easier to work with in Java.
  */

  public int[][] bytesToState(byte[] input){
    int[][] state = new int[4][4];
    for(int i = 0; i < 16; i++){
      state[i % 4][i /4 ] = input[i] & 0xFF;
    }
    return state;
  }

  /*
    This funnction converts the 4x4 matrix state back into a 1D byte array.
    The first column of the state becomes the first 4 bytes of the output, the second column becomes the next 4 bytes, etc.
  */

  public byte[] stateToBytes(int[][] state){
    byte[] output = new byte[16];
    for(int i = 0; i < 16; i++){
      output[i] = (byte)(state[i % 4][i /4] & 0xFF);
    }
    return output;
  }

  /*
    The subBytes() function is the first step in the AES encryption process. It takes the state and applies the SBOX substitution to each byte.
    This is a nonlinear transformation that provides confusion, making it difficult for attackers to find patterns in the ciphertext.
  */

  public void subBytes(int[][] state){
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 4; j++){
        state[i][j] = Constants.SBOX[state[i][j]];
      }
    }
  }

  /*
    The inverseSubBytes() function is used in the decryption process.
    It takes the state and applies the inverse SBOX substitution to each byte, reversing the transformation done by subBytes().
  */
  public void inverseSubBytes(int[][] state){
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 4; j++){
        state[i][j] = Constants.INVERSE_SBOX[state[i][j]];
      }
    }
  }

  /*
    The shiftRows() function is the second step in the AES encryption process. It takes the state and shifts the rows to the left.
    The first row is not shifted, the second row is shifted by 1 position, the third row is shifted by 2 positions, and the fourth row is shifted by 3 positions.
  */

  public void shiftRows(int[][] state){
    int[] temp = new int[4];
    for(int i = 1; i < 4; i++){
      for (int j = 0; j < 4; j++){
        temp[j] = state[i][(j + i) % 4];
      }
      for (int j = 0; j < 4; j++){
        state[i][j] = temp[j];
      }
    }
  }

  /*
    The inverseShiftRows() function is used in the decryption process.
    It takes the state and shifts the rows to the right, reversing the transformation done by shiftRows().
  */

  public void inverseShiftRows(int[][] state){
    int[] temp = new int[4];
    for(int i = 1; i < 4; i++){
      for (int j = 0; j < 4; j++){
        temp[j] = state[i][(j - i + 4) % 4];
      }
      for (int j = 0; j < 4; j++){
        state[i][j] = temp[j];
      }
    }
  }

  /*
    XORs the state with the round key. This is the last step in each round of AES encryption.
    This is the step that actually incorporates the key into the encryption process, providing the security of the cipher.
  */

  public void addRoundKey(int[][] state, int[][] roundKey){
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 4; j++){
        state[i][j] = state[i][j] ^ roundKey[i][j];
      }
    }
  }

   /*
    The xtime() function is a helper function used in the MixColumns step of AES.
    It multiplies a byte by 0x02 in the finite field GF(2^8) --> this allows our numbers to wrap around when they exceed 255.
    This is done by left-shifting the byte and then applying a bitwise AND with 0xFF to ensure the result stays within the range of a byte.
    If the most significant bit of the byte is 1, it also XORs the result with 0x1B (x^8 + x^4 + x^3 + x + 1), which is the irreducible polynomial used in AES.
  */

  private int xtime(int byteValue){
    return ((byteValue << 1) ^ (((byteValue >> 7) & 1) * 0x1B)) & 0xFF;
  }

  /*
    The multiply9(), multiply11(), multiply13(), and multiply14() functions are helper functions used in the inverse MixColumns step of AES.
    They multiply a byte by 0x09, 0x0B, 0x0D, and 0x0E respectively in the finite field GF(2^8).
    These functions are implemented using the xtime() function to perform the necessary multiplications.
  */

  private int multiply9(int byteValue){
    return xtime(xtime(xtime(byteValue))) ^ byteValue;
  }

  private int multiply11(int byteValue){
    return xtime(xtime(xtime(byteValue))) ^ xtime(byteValue) ^ byteValue;
  }

  private int multiply13(int byteValue){
    return xtime(xtime(xtime(byteValue))) ^ xtime(xtime(byteValue)) ^ byteValue;
  }

  private int multiply14(int byteValue){
    return xtime(xtime(xtime(byteValue))) ^ xtime(xtime(byteValue)) ^ xtime(byteValue);
  }

  private int[][] getRoundKey(int[] expandedKey, int round){
    int[][] roundKey = new int[4][4];
    for (int i = 0; i < 4; i++){
      int word = expandedKey[round * 4 + i];
      roundKey[0][i] = (word >>> 24) & 0xFF;
      roundKey[1][i] = (word >>> 16) & 0xFF;
      roundKey[2][i] = (word >>> 8) & 0xFF;
      roundKey[3][i] = word & 0xFF;
    }
    return roundKey;
  }
}
