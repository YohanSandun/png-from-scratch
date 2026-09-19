package lk.ysk.spike.png.chunk;

import lk.ysk.spike.io.ByteReader;
import lk.ysk.spike.png.Constants;

public class IHDRChunk extends PngChunk {

    private final int width;
    private final int height;
    private final int bitDepth;
    private final int colorType;
    private final int compressionMethod;
    private final int filterMethod;
    private final int interlaceMethod;
    private final int bytesPerPixel;

    public IHDRChunk(int length, byte[] data, int crc) {
        super(length, Constants.IHDR, data, crc);

        ByteReader byteReader = new ByteReader(data);
        width = byteReader.readNextInt32();
        height = byteReader.readNextInt32();
        bitDepth = byteReader.readNextUnsignedByte();
        colorType = byteReader.readNextUnsignedByte();
        compressionMethod = byteReader.readNextUnsignedByte();
        filterMethod = byteReader.readNextUnsignedByte();
        interlaceMethod = byteReader.readNextUnsignedByte();

        int channels = switch (colorType) {
            case 0, 3 -> 1;
            case 2 -> 3;
            case 4 -> 2;
            case 6 -> 4;
            default -> throw new IllegalArgumentException("Invalid color type");
        };

        int bitsPerPixel = channels * bitDepth;
        bytesPerPixel = (bitsPerPixel + 7) / 8;
    }

    @Override
    public boolean isValid() {
        if (width == 0 || height == 0) return false;

        if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8 && bitDepth != 16) return false;

        return true;
    }

    @Override
    public String toString() {
        return """
        -----IHDR-----
        Width:\t\t\t\t %d
        Height:\t\t\t\t %d
        Bit Depth:\t\t\t %d
        Color Type:\t\t\t %d
        Compression:\t\t %d
        Filter Method:\t\t %d
        Interlace Method:\t %d
        ---------------
        """.formatted(width, height, bitDepth, colorType, compressionMethod, filterMethod, interlaceMethod);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public int getColorType() {
        return colorType;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public int getFilterMethod() {
        return filterMethod;
    }

    public int getInterlaceMethod() {
        return interlaceMethod;
    }

    public int getBytesPerPixel() {
        return bytesPerPixel;
    }
}
