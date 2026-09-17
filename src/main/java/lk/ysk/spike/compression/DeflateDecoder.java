package lk.ysk.spike.compression;

import lk.ysk.spike.io.BitReader;

public class DeflateDecoder {

    private final byte[] data;

    public DeflateDecoder(byte[] data) {
        this.data = data;

        BitReader bitReader = new BitReader(data);

        int bFinal = 0;
        while (bFinal == 0) {
            bFinal = bitReader.readNextBit();
            int bType = bitReader.readNextBits(2);

            if (bType == 0) {
                System.out.println("Not implemented: No compression");
                break;
            } else if (bType == 1) {
                System.out.println("Not implemented: Compressed with fixed huffman codes");
                break;
            } else if (bType == 2) {
                System.out.println("Not implemented: Compressed with dynamic huffman codes");
                break;
            } else {
                throw new IllegalArgumentException("Invalid deflate block type");
            }
        }
    }
}
