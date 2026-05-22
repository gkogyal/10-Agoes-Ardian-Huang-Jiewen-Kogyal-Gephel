package aes;

public class Padding {

    /*
    This method pads the input data with zeros to ensure that its length is a multiple of 16 bytes.
    */
    public static byte[] pad(byte[] data) {
        int paddingLength = 16 - (data.length % 16);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        for (int i = data.length; i < paddedData.length; i++) {
            paddedData[i] = 0;
        }
        return paddedData;
    }
}