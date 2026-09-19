package lk.ysk.spike.compression;

import lk.ysk.spike.io.BitReader;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HuffmanDecoderTest {

    /** The DEFLATE fixed literal/length code lengths (RFC 1951 section 3.2.6). */
    private static int[] fixedLiteralLengths() {
        int[] lengths = new int[288];
        Arrays.fill(lengths, 0, 144, 8);
        Arrays.fill(lengths, 144, 256, 9);
        Arrays.fill(lengths, 256, 280, 7);
        Arrays.fill(lengths, 280, 288, 8);
        return lengths;
    }

    @Test
    void doesNotMatchALongCodeAfterTooFewBits() {
        // Symbol 0 = 00110000, symbol 1 = 00110001, packed LSB-first as DEFLATE requires.
        byte[] stream = {(byte) 0x0C, (byte) 0x8C};

        HuffmanDecoder decoder = new HuffmanDecoder(fixedLiteralLengths(), new BitReader(stream));

        assertEquals(0, decoder.getNextSymbol(), "first symbol");
        assertEquals(1, decoder.getNextSymbol(), "second symbol -- wrong here means the first read consumed too few bits");
    }

    @Test
    void decodesSevenBitCodes() {
        byte[] stream = {0x00};

        HuffmanDecoder decoder = new HuffmanDecoder(fixedLiteralLengths(), new BitReader(stream));

        assertEquals(256, decoder.getNextSymbol());
    }

    @Test
    void distinguishesCodesThatShareAReversedValue() {
        // symbols A, B, C, D with code lengths 1, 3, 3, 2 (Kraft sum = 1, a complete table)
        int[] lengths = {1, 3, 3, 2};

        // B = 110, emitted MSB-first into an LSB-first bit stream -> bits 0,1,1
        byte[] stream = {0b0000_0011};

        HuffmanDecoder decoder = new HuffmanDecoder(lengths, new BitReader(stream));

        assertEquals(1, decoder.getNextSymbol(), "expected symbol B, not the shorter code D");
    }

    @Test
    void rejectsAnUndecodableBitPattern() {
        // Only one symbol, with the 1-bit code 0. A stream of 1 bits never matches it.
        int[] lengths = {1};
        byte[] stream = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        HuffmanDecoder decoder = new HuffmanDecoder(lengths, new BitReader(stream));

        assertThrows(IllegalArgumentException.class, decoder::getNextSymbol);
    }
}
