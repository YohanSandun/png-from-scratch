package lk.ysk.spike.io;

public class BitReader {

    private final byte[] data;
    private int bytePos = 0;
    private int bitPos = 0;

    public BitReader(byte[] data) {
        this.data = data;
    }

    public void skipCurrentByte() {
        if (bitPos != 0) {
            bytePos++;
            bitPos = 0;
        }
    }

    public int readNextBit() {
        if (bytePos < data.length) {
            int bit = ((data[bytePos] & 0xFF) >> bitPos++) & 1;
            if (bitPos == 8) {
                bytePos++;
                bitPos = 0;
            }
            return bit;
        }
        throw new IllegalArgumentException("Unexpected end of input");
    }

    public int readNextBits(int n) {
        int result = 0;
        for (int i = 0; i < n; i++){
            result = (readNextBit() << i) | result;
        }
        return result;
    }

    public int readUnsignedInt16BigEndian(int pos) {
        if (pos + 2 > data.length) {
            throw new IllegalArgumentException("Not enough bytes to read unsigned 16 bit integer");
        }

        return ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
    }

    public int readNextUnsignedInt16() {
        if (bytePos + 2 > data.length) {
            throw new IllegalArgumentException("Not enough bytes to read unsigned 16 bit integer");
        }
        int int16 = (data[bytePos] & 0xFF) | ((data[bytePos + 1] & 0xFF) << 8);
        bytePos += 2;
        bitPos = 0;
        return int16;
    }
}
