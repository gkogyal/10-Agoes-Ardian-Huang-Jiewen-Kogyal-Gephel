package aes;

public class AESKeySchedule{

  /*
  The expandKey() function takes the original 256-bit master key and generates a total of 60 words (each word is 4 bytes) that will be used in the AES rounds.
  The first 8 words are directly taken from the master key. For the remaining words, we use a combination of the previous words, the SBOX substitution, and the RCON round constants to ensure that the generated keys are unique.
  This provides 15 round keys (4 words each) for the 14 rounds of AES-256 encryption, plus an additional key for the initial AddRoundKey step.
  */

  public static int[] expandKey(byte[] masterKey){
    int[] words = new int[60]; // 15 rounds * 4 words
    int temp;
    for (int i = 0; i < 8; i++){
      words[i] = ((masterKey[4*i] & 0xFF) << 24) | ((masterKey[4*i+1] & 0xFF) << 16) | ((masterKey[4*i+2] & 0xFF) << 8) | (masterKey[4*i+3] & 0xFF);
    }

    for (int i = 8; i < 60; i++){
      temp = words[i - 1];
      if (i % 8 == 0){
        temp = (subWord(rotWord(temp)) ^ (Constants.RCON[i / 8 - 1] << 24));
      }
      else if (i % 8 == 4){
        temp = subWord(temp);
      }
      words[i] = words[i-8] ^ temp;
    }
    return words;
  }

  /*
  The subWord() function takes a four-byte word and applies the SBOX substitution to each byte.
  It is identical to the subBytes() function but operates on a single word instead of the entire state.
  Helps make the master key more secure by adding nonlinearity to the key expansion process, making it more resistant to attacks that attempt to find patterns in the generated round keys.
  */

  private static int subWord(int word){
    return (Constants.SBOX[(word >>> 24) & 0xFF] << 24) | (Constants.SBOX[(word >>> 16) & 0xFF] << 16) | (Constants.SBOX[(word >>> 8) & 0xFF] << 8) | (Constants.SBOX[word & 0xFF]);
  }

  /*
  The rotWord() function takes a four-byte word and rotates it to the left by one byte.
  For example, if the input word is [0x01, 0x02, 0x03, 0x04], the output will be [0x02, 0x03, 0x04, 0x01].
  By rotating the word, we introduce more variability into the key generation process.
  */

  private static int rotWord(int word){
    return (word << 8) | word >>> 24;
  }
}
