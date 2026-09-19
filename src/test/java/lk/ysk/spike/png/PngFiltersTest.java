package lk.ysk.spike.png;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PngFiltersTest {

    /** colorType, bitDepth, expected bytes per pixel. */
    @ParameterizedTest(name = "colorType={0} bitDepth={1} -> bpp={2}")
    @CsvSource({
            "0,  8, 1",   // greyscale
            "4,  8, 2",   // greyscale + alpha
            "2,  8, 3",   // RGB
            "6,  8, 4",   // RGBA
            "0, 16, 2",   // greyscale 16-bit
            "4, 16, 4",   // greyscale + alpha 16-bit
            "2, 16, 6",   // RGB 16-bit
            "6, 16, 8",   // RGBA 16-bit
    })
    void reconstructsEveryFilterType(int colorType, int bitDepth, int expectedBpp) {
        int width = 9;
        int height = 6;

        IHDRChunk header = header(width, height, bitDepth, colorType);
        assertEquals(expectedBpp, header.getBytesPerPixel(), "bytes per pixel");

        int stride = width * expectedBpp;
        byte[][] original = randomRows(height, stride, 20250919L);

        // Row 0 uses None. Rows 1+ cycle through Sub, Up, Average and Paeth so that every
        // reconstruction path runs against a real previous row.
        int[] filterTypes = new int[height];
        for (int row = 1; row < height; row++) {
            filterTypes[row] = 1 + ((row - 1) % 4);
        }

        PngRow[] rows = applyFilters(original, filterTypes, expectedBpp);
        byte[][] reconstructed = new PngFilters(rows, header).removeFilters();

        assertEquals(height, reconstructed.length, "row count");
        for (int row = 0; row < height; row++) {
            assertArrayEquals(original[row], reconstructed[row],
                    "row " + row + " (filter type " + filterTypes[row] + ")");
        }
    }

    @ParameterizedTest(name = "filterType={0}")
    @CsvSource({"1", "2", "3", "4"})
    void reconstructsLeadingPixelOfRow(int filterType) {
        int width = 4;
        int bpp = 4;

        IHDRChunk header = header(width, 2, 8, 6);
        byte[][] original = randomRows(2, width * bpp, 7L);

        PngRow[] rows = applyFilters(original, new int[]{0, filterType}, bpp);
        byte[][] reconstructed = new PngFilters(rows, header).removeFilters();

        assertArrayEquals(original[1], reconstructed[1], "filtered row");
    }

    // --- helpers -------------------------------------------------------------------

    private static IHDRChunk header(int width, int height, int bitDepth, int colorType) {
        byte[] data = new byte[13];
        writeInt32(data, 0, width);
        writeInt32(data, 4, height);
        data[8] = (byte) bitDepth;
        data[9] = (byte) colorType;
        data[10] = 0; // compression method
        data[11] = 0; // filter method
        data[12] = 0; // interlace method
        return new IHDRChunk(data.length, data, 0);
    }

    private static void writeInt32(byte[] target, int offset, int value) {
        target[offset] = (byte) (value >>> 24);
        target[offset + 1] = (byte) (value >>> 16);
        target[offset + 2] = (byte) (value >>> 8);
        target[offset + 3] = (byte) value;
    }

    private static byte[][] randomRows(int height, int stride, long seed) {
        Random random = new Random(seed);
        byte[][] rows = new byte[height][stride];
        for (byte[] row : rows) {
            random.nextBytes(row);
        }
        return rows;
    }

    /** Encodes rows the way a PNG encoder would, so the decoder has something to invert. */
    private static PngRow[] applyFilters(byte[][] original, int[] filterTypes, int bpp) {
        PngRow[] rows = new PngRow[original.length];
        byte[] zeroRow = new byte[original[0].length];

        for (int row = 0; row < original.length; row++) {
            byte[] current = original[row];
            byte[] previous = row > 0 ? original[row - 1] : zeroRow;
            byte[] filtered = new byte[current.length];

            for (int i = 0; i < current.length; i++) {
                int raw = current[i] & 0xFF;
                int a = i >= bpp ? current[i - bpp] & 0xFF : 0;
                int b = previous[i] & 0xFF;
                int c = i >= bpp ? previous[i - bpp] & 0xFF : 0;

                int predictor = switch (filterTypes[row]) {
                    case 0 -> 0;
                    case 1 -> a;
                    case 2 -> b;
                    case 3 -> (a + b) / 2;
                    case 4 -> paeth(a, b, c);
                    default -> throw new IllegalArgumentException("bad filter type");
                };
                filtered[i] = (byte) (raw - predictor);
            }
            rows[row] = new PngRow(filterTypes[row], filtered);
        }
        return rows;
    }

    private static int paeth(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) return a;
        if (pb <= pc) return b;
        return c;
    }
}
