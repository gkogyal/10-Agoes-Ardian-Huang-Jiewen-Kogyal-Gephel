package aes;

public class AES256{

  /* 
    AES processes data in 16 byte or 128 bit blocks. A state is a 4x4 matrix of bytes, so we need to convert the input byte array into a state. 
    The first 4 bytes of the input are the first column of the state, the next 4 bytes are the second column, and so on. 
    The bitwise AND with 0xFF to convert the byte to an unsigned int, which is easier to work with in Java.
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
}
