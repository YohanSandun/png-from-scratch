package lk.ysk.spike;

public class ByteReader {

    private final byte[] data;
    private int pos = 0;

    public ByteReader(byte[] data) {
        this.data = data;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public byte readNextByte() {
        if (pos < data.length) {
            return data[pos++];
        }
        return 0;
    }

    public int readNextUnsignedByte() {
        if (pos < data.length) {
            return data[pos++] & 0xFF;
        }
        return 0;
    }

    public int readNextInt32() {
        if (pos + 4 > data.length) {
            throw new IllegalStateException("Not enough bytes to read int32");
        }

        int int32 =
                        ((data[pos] & 0xFF) << 24) |
                        ((data[pos + 1] & 0xFF) << 16) |
                        ((data[pos + 2] & 0xFF) << 8)  |
                        (data[pos + 3]  & 0xFF);

        pos += 4;
        return int32;
    }

    public byte[] readBytes(int length) {
        // TODO: safeguard array length
        byte[] bytes = new byte[length];
        System.arraycopy(data, pos, bytes, 0, length);
        pos += length;
        return bytes;
    }

}
