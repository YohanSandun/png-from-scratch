package lk.ysk.spike.io;

public class BitReader {

    private final byte[] data;
    private int bytePos = 0;
    private int bitPos = 0;

    private static final int[] BIT_MASKS = {
            0b0000_0000,
            0b0000_0001,
            0b0000_0011,
            0b0000_0111,
            0b0000_1111,
            0b0001_1111,
            0b0011_1111,
            0b0111_1111,
            0b1111_1111,
    };

    public BitReader(byte[] data) {
        this.data = data;
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
        return 0;
    }

    public int readNextBits(int n) {
        if (bitPos + n > 8) {
            throw new IllegalArgumentException("Requested bits are not within the current byte");
        }

        int bits = ((data[bytePos] & 0xFF) >> bitPos) & BIT_MASKS[n];
        bitPos += n;
        if (bitPos == 8) {
            bytePos++;
            bitPos = 0;
        }
        return bits;
    }

    public int readUnsignedInt16(int pos) {
        if (pos + 1 > data.length) {
            throw new IllegalArgumentException("Not enough bytes to read unsigned 16 bit integer");
        }

        return ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
    }
}
