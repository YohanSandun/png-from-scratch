package lk.ysk.spike.png;

import lk.ysk.spike.io.ByteReader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * End-to-end tests against {@link ImageIO}, which ships a reference PNG decoder in the JDK.
 *
 * <p>Both fixtures are 8-bit RGBA (colour type 6), so a scanline is four bytes per pixel in
 * R, G, B, A order. Other colour types are not covered here yet -- see the sub-8-bit stride
 * and interlacing items in architecture-review.html.
 */
class PngImageTest {

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"sample.png", "sample1.png"})
    void decodesIdenticallyToImageIO(String name) throws IOException {
        byte[] bytes = resource("sample-images/" + name);

        BufferedImage expected = ImageIO.read(new ByteArrayInputStream(bytes));
        assertNotNull(expected, "ImageIO could not read the fixture");

        PngImage actual = new PngImage(new ByteReader(bytes));

        assertEquals(expected.getWidth(), actual.getWidth(), "width");
        assertEquals(expected.getHeight(), actual.getHeight(), "height");

        byte[][] pixels = actual.getPixels();
        assertEquals(expected.getHeight(), pixels.length, "scanline count");

        for (int y = 0; y < expected.getHeight(); y++) {
            assertArrayEquals(expectedScanline(expected, y), pixels[y], "scanline " + y);
        }
    }

    /** The reference decoder's row y, laid out as PNG stores it: R, G, B, A per pixel. */
    private static byte[] expectedScanline(BufferedImage image, int y) {
        byte[] scanline = new byte[image.getWidth() * 4];
        for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getRGB(x, y);
            int offset = x * 4;
            scanline[offset] = (byte) (argb >>> 16);
            scanline[offset + 1] = (byte) (argb >>> 8);
            scanline[offset + 2] = (byte) argb;
            scanline[offset + 3] = (byte) (argb >>> 24);
        }
        return scanline;
    }

    private static byte[] resource(String path) throws IOException {
        try (InputStream in = PngImageTest.class.getClassLoader().getResourceAsStream(path)) {
            assertNotNull(in, "missing test resource: " + path);
            return in.readAllBytes();
        }
    }
}
