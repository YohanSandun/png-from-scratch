package lk.ysk.spike.compression;

import lk.ysk.spike.io.BitReader;

import java.util.HashMap;
import java.util.Map;

public class HuffmanDecoder {

    private static final int MAX_BITS = 15;
    private final Map<Integer, Integer> codes;
    private final BitReader bitReader;
    private final int minBits;
    private final int maxBits;

    public HuffmanDecoder(int[] codeLengths, BitReader bitReader) {
        this.bitReader = bitReader;
        codes = new HashMap<>();

        int minBits = Integer.MAX_VALUE;
        int maxBits = Integer.MIN_VALUE;

        int[] blCount = new int[MAX_BITS + 1];
        for (int codeLength : codeLengths) {
            if (codeLength > 0) {
                blCount[codeLength]++;
                minBits = Math.min(minBits, codeLength);
                maxBits = Math.max(maxBits, codeLength);
            }
        }

        if (maxBits == Integer.MIN_VALUE) {
            this.minBits = 0;
            this.maxBits = 0;
        } else {
            this.minBits = minBits;
            this.maxBits = maxBits;
        }

        int code = 0;
        int[] nextCode = new int[MAX_BITS + 1];
        for (int bits = 1; bits <= MAX_BITS; bits++) {
            code = (code + blCount[bits - 1]) << 1;
            nextCode[bits] = code;
        }

        for (int symbol = 0; symbol < codeLengths.length; symbol++) {
            int length = codeLengths[symbol];

            if (length == 0) continue;

            codes.put(key(length, reverseBits(nextCode[length], length)), symbol);
            nextCode[length]++;
        }
    }

    private int key(int length, int bits) {
        return (length << 16) | bits;
    }

    private int reverseBits(int code, int length) {
        int reversed = 0;

        for (int i = 0; i < length; i++) {
            reversed = (reversed << 1) | (code & 1);
            code >>>= 1;
        }

        return reversed;
    }

    public int getNextSymbol() {
        int bits = 0;

        for (int len = 1; len <= maxBits; len++) {
            bits |= bitReader.readNextBit() << (len - 1);

            if (len >= minBits) {
                Integer symbol = codes.get(key(len, bits));
                if (symbol != null) {
                    return symbol;
                }
            }
        }

        throw new IllegalArgumentException("Invalid Huffman code");
    }

}
