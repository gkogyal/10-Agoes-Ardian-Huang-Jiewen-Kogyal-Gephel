package aes;

public class AESKeySchedule{

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

  private static int subWord(int word){
    return (Constants.SBOX[(word >>> 24) & 0xFF] << 24) | (Constants.SBOX[(word >>> 16) & 0xFF] << 16) | (Constants.SBOX[(word >>> 8) & 0xFF] << 8) | (Constants.SBOX[word & 0xFF]);
  }

  /*
  Takes a four byte word and moves the first byte to the very end;
  */
  private static int rotWord(int word){
    return (word << 8) | word >>> 24;
  }
}
