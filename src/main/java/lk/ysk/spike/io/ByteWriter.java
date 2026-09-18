package lk.ysk.spike.io;

import java.util.Arrays;

public class ByteWriter {

    private byte[] data = new byte[1024];
    private int size = 0;

    public void writeByte(int value) {
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length*2);
        }

        data[size++] = (byte) value;
    }

    public int getSize() {
        return size;
    }

    public int readByte(int pos) {
        return data[pos];
    }

    public byte[] toByteArray() {
        byte[] output = new byte[size];
        System.arraycopy(data, 0, output, 0, size);
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((char)data[i]);
        }
        return sb.toString();
    }
}
