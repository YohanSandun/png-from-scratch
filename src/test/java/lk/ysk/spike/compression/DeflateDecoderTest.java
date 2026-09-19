package lk.ysk.spike.compression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;
import java.util.zip.Deflater;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeflateDecoderTest {

    static Stream<Arguments> payloads() {
        return Stream.of(
                Arguments.of("empty", new byte[0]),
                Arguments.of("single byte", new byte[]{42}),
                Arguments.of("all zeros 64KB", new byte[64 * 1024]),
                Arguments.of("highly repetitive", repeat("the quick brown fox jumps over the lazy dog. ", 500)),
                Arguments.of("incompressible random 256KB", random(256 * 1024, 1)),
                Arguments.of("text-like", repeat("abcdefghij", 100_000))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("payloads")
    void matchesJdkDeflater(String name, byte[] original) {
        byte[] compressed = deflateRaw(original, Deflater.DEFAULT_COMPRESSION);
        assertArrayEquals(original, new DeflateDecoder(compressed).decode(), name);
    }

    @ParameterizedTest(name = "compression level {0}")
    @ValueSource(ints = {0, 1, 5, 9})
    void handlesEveryBlockType(int level) {
        byte[] original = repeat("deflate block type coverage ", 2000);
        assertArrayEquals(original, new DeflateDecoder(deflateRaw(original, level)).decode());
    }

    @Test
    void decodesBackReferencesLongerThanTheirDistance() {
        // distance 1, length 300: the copy must read bytes it is writing as it goes
        byte[] original = new byte[300];
        Arrays.fill(original, (byte) 'x');

        assertArrayEquals(original, new DeflateDecoder(deflateRaw(original, 9)).decode());
    }

    @Test
    void stopsAtTheFinalBlockAndIgnoresTrailingBytes() {
        byte[] original = repeat("stop at bfinal ", 10);
        byte[] padded = Arrays.copyOf(deflateRaw(original, 9), deflateRaw(original, 9).length + 16);

        assertArrayEquals(original, new DeflateDecoder(padded).decode());
    }

    @Test
    void rejectsReservedBlockType() {
        // 0b111 in the low bits: bfinal = 1, btype = 3 (reserved)
        byte[] reserved = {0x07, 0x00, 0x00, 0x00};

        assertThrows(IllegalArgumentException.class, () -> new DeflateDecoder(reserved).decode());
    }

    // --- helpers -------------------------------------------------------------------

    private static byte[] deflateRaw(byte[] data, int level) {
        Deflater deflater = new Deflater(level, true); // nowrap = raw deflate, no zlib header
        try {
            deflater.setInput(data);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            while (!deflater.finished()) {
                out.write(buffer, 0, deflater.deflate(buffer));
            }
            return out.toByteArray();
        } finally {
            deflater.end();
        }
    }

    private static byte[] repeat(String text, int times) {
        return text.repeat(times).getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] random(int length, long seed) {
        byte[] data = new byte[length];
        new Random(seed).nextBytes(data);
        return data;
    }
}
