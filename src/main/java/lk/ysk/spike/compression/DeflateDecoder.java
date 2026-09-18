package lk.ysk.spike.compression;

import lk.ysk.spike.io.BitReader;
import lk.ysk.spike.io.ByteWriter;

import java.util.Arrays;

public class DeflateDecoder {

    private static final int[] LENGTH_BASE = {
            3, 4, 5, 6, 7, 8, 9, 10,
            11, 13, 15, 17,
            19, 23, 27, 31,
            35, 43, 51, 59,
            67, 83, 99, 115,
            131, 163, 195, 227,
            258
    };

    private static final int[] LENGTH_EXTRA_BITS = {
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1,
            2, 2, 2, 2,
            3, 3, 3, 3,
            4, 4, 4, 4,
            5, 5, 5, 5,
            0
    };

    private static final int[] DISTANCE_BASE = {
            1, 2, 3, 4,
            5, 7, 9, 13,
            17, 25, 33, 49,
            65, 97, 129, 193,
            257, 385, 513, 769,
            1025, 1537, 2049, 3073,
            4097, 6145, 8193, 12289,
            16385, 24577
    };

    private static final int[] DISTANCE_EXTRA_BITS = {
            0, 0, 0, 0,
            1, 1, 2, 2,
            3, 3, 4, 4,
            5, 5, 6, 6,
            7, 7, 8, 8,
            9, 9, 10, 10,
            11, 11, 12, 12,
            13, 13
    };

    private static final int[] CODE_LENGTH_ORDER = {
            16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15
    };

    private final BitReader bitReader;
    private final ByteWriter byteWriter = new ByteWriter();

    public DeflateDecoder(byte[] data) {
        bitReader = new BitReader(data);

        int bFinal = 0;
        while (bFinal == 0) {
            bFinal = bitReader.readNextBit();
            int bType = bitReader.readNextBits(2);

            if (bType == 0) {
                decodeStoredBlock();
            } else if (bType == 1) {
                decodeFixedHuffmanBlock();
            } else if (bType == 2) {
                decodeDynamicHuffmanBlock();
            } else {
                throw new IllegalArgumentException("Invalid deflate block type");
            }
        }

        System.out.println(byteWriter);
    }

    private void decodeStoredBlock() {
        bitReader.skipCurrentByte();

        int len = bitReader.readNextUnsignedInt16();
        int nLen = bitReader.readNextUnsignedInt16();

        if ((len ^ nLen) != 0xFFFF) {
            throw new IllegalArgumentException("Invalid/corrupted stored block");
        }

        for (int i = 0; i < len; i++) {
            byteWriter.writeByte(bitReader.readNextBits(8));
        }
    }

    private void copyDataFromEarlier(int length, int distance) {
        int pos = byteWriter.getSize() - distance;
        for (int i = 0; i < length; i++) {
            byteWriter.writeByte(byteWriter.readByte(pos + i));
        }
    }

    private void decodeFixedHuffmanBlock() {
        int[] lengths = new int[288];
        int[] distanceLengths = new int[32];

        Arrays.fill(distanceLengths, 5);

        for (int i = 0; i <= 143; i++)
            lengths[i] = 8;

        for (int i = 144; i <= 255; i++)
            lengths[i] = 9;

        for (int i = 256; i <= 279; i++)
            lengths[i] = 7;

        for (int i = 280; i <= 287; i++)
            lengths[i] = 8;

        HuffmanDecoder literalLengthDecoder = new HuffmanDecoder(lengths, bitReader);
        HuffmanDecoder distanceDecoder = new HuffmanDecoder(distanceLengths, bitReader);

        decodeHuffmanBlock(literalLengthDecoder, distanceDecoder);
    }

    private int decodeLength(int symbol) {
        int lengthIndex = symbol - 257;

        if (lengthIndex >= LENGTH_BASE.length) {
            throw new IllegalArgumentException("Invalid length symbol: " + symbol);
        }

        int length = LENGTH_BASE[lengthIndex];
        int extraBits = LENGTH_EXTRA_BITS[lengthIndex];

        if (extraBits > 0) {
            length += bitReader.readNextBits(extraBits);
        }
        return length;
    }

    private int decodeDistance(int symbol) {
        if (symbol >= DISTANCE_BASE.length) {
            throw new IllegalArgumentException("Invalid distance symbol: " + symbol);
        }

        int distance = DISTANCE_BASE[symbol];
        int extraBits = DISTANCE_EXTRA_BITS[symbol];

        if (extraBits > 0) {
            distance += bitReader.readNextBits(extraBits);
        }
        return  distance;
    }

    private void decodeHuffmanBlock(HuffmanDecoder literalLengthDecoder, HuffmanDecoder distanceDecoder) {
        int symbol = literalLengthDecoder.getNextSymbol();
        while (symbol != 256) {
            if (symbol < 256) {
                // literal
                byteWriter.writeByte(symbol);
            } else {
                // distance
                int length = decodeLength(symbol);
                int distanceSymbol = distanceDecoder.getNextSymbol();
                int distance = decodeDistance(distanceSymbol);
                copyDataFromEarlier(length, distance);
            }
            symbol = literalLengthDecoder.getNextSymbol();
        }
    }

    private void decodeDynamicHuffmanBlock() {
        int HLIT = bitReader.readNextBits(5);
        int HDIST = bitReader.readNextBits(5);
        int HCLEN = bitReader.readNextBits(4);

        int[] codeLengthCodeLengths = new int[19];
        for (int i = 0; i < HCLEN + 4; i++) {
            int symbol = CODE_LENGTH_ORDER[i];
            codeLengthCodeLengths[symbol] = bitReader.readNextBits(3);
        }

        HuffmanDecoder codeLengthDecoder = new HuffmanDecoder(codeLengthCodeLengths, bitReader);

        int[] literalLengthCodeLengths = new int[HLIT + 257];
        int[] distanceCodeLengths = new int[HDIST + 1];

        int index = 0;
        while (index < literalLengthCodeLengths.length) {
            index = decodeHuffmanTree(codeLengthDecoder, literalLengthCodeLengths, index);
        }

        index = 0;
        while (index < distanceCodeLengths.length) {
            index = decodeHuffmanTree(codeLengthDecoder, distanceCodeLengths, index);
        }

        HuffmanDecoder literalLengthDecoder = new HuffmanDecoder(literalLengthCodeLengths, bitReader);
        HuffmanDecoder distanceDecoder = new HuffmanDecoder(distanceCodeLengths, bitReader);

        decodeHuffmanBlock(literalLengthDecoder, distanceDecoder);
    }

    private int decodeHuffmanTree(HuffmanDecoder codeLengthDecoder, int[] array, int index) {
        int codeLengthSymbol = codeLengthDecoder.getNextSymbol();
        if (codeLengthSymbol <= 15) {
            array[index++] = codeLengthSymbol;
        } else if (codeLengthSymbol == 16) {
            int repeatLength = bitReader.readNextBits(2) + 3;
            index = repeatCodeLength(array, index, array[index-1], repeatLength);
        } else if (codeLengthSymbol == 17) {
            int repeatLength = bitReader.readNextBits(3) + 3;
            index = repeatCodeLength(array, index, 0, repeatLength);
        } else if (codeLengthSymbol == 18) {
            int repeatLength = bitReader.readNextBits(7) + 11;
            index = repeatCodeLength(array, index, 0, repeatLength);
        }
        return index;
    }

    private int repeatCodeLength(int[] array, int index, int codeLength, int repeat) {
        for (int i = 0; i < repeat; i++) {
            array[index++] = codeLength;
        }
        return index;
    }
}
