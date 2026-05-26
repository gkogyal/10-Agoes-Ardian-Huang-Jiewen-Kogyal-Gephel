package aes;

public class Padding {

    /*
     Pads a byte array so its total length becomes a multiple of 16 bytes.
     This is required because AES can only process data in 16-byte chunks.
     This follows the PKCS#7 standard meaning the value of the padding bytes equals the number of padding bytes added.
     If the input is already a multiple of 16 bytes, a full block of 16 padding bytes (0x10) is appended so that the unpadding process can correctly identify and remove the padding.
    */    

    public static byte[] pad(byte[] data) {
        int paddingLength = 16 - (data.length % 16);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        for (int i = data.length; i < paddedData.length; i++) {
            paddedData[i] = (byte) paddingLength; 
        }
        return paddedData;
    }

    /*
        Removes the padding from a byte array that was padded using the pad() method.
        It checks the last byte to determine how many padding bytes were added, verifies that all padding bytes are correct, and then returns a new array containing only the original unpadded data.
        If the padding is invalid (incorrect length or incorrect padding bytes), it throws an IllegalArgumentException.
        paddedData.length % 16 != 0 ensures that the input is a valid multiple of the block size, which is necessary for proper unpadding.
        paddingLength < 1 || paddingLength > 16 ensures that the padding length is within the the valid range.
        Creates a new array for the unpadded data and copies the original data into it before returning.
    */

    public static byte[] unpad(byte[] paddedData) {
        if (paddedData.length == 0 || paddedData.length % 16 != 0) {
            throw new IllegalArgumentException("Invalid padded data length");   
        }
        int paddingLength = paddedData[paddedData.length - 1] & 0xFF;
        if (paddingLength < 1 || paddingLength > 16) {
            throw new IllegalArgumentException("Invalid padding length");
        }
        for (int i = paddedData.length - paddingLength; i < paddedData.length; i++) {
            if ((paddedData[i] & 0xFF) != paddingLength) {
                throw new IllegalArgumentException("Invalid padding bytes");
            }
        }
        int unpaddedLength = paddedData.length - paddingLength;
        byte[] unpaddedData = new byte[unpaddedLength];
        System.arraycopy(paddedData, 0, unpaddedData, 0, unpaddedLength);
        return unpaddedData;
    }
}