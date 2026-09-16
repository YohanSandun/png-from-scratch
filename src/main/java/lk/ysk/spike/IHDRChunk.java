package lk.ysk.spike;

public class IHDRChunk extends Chunk {

    private final int width;
    private final int height;
    private final int bitDepth;
    private final int colorType;
    private final int compressionMethod;
    private final int filterMethod;
    private final int interlaceMethod;

    public IHDRChunk(int length, byte[] data, int crc) {
        super(length, ChunkType.IHDR, data, crc);

        ByteReader byteReader = new ByteReader(data);
        width = byteReader.readNextInt32();
        height = byteReader.readNextInt32();
        bitDepth = byteReader.readNextUnsignedByte();
        colorType = byteReader.readNextUnsignedByte();
        compressionMethod = byteReader.readNextUnsignedByte();
        filterMethod = byteReader.readNextUnsignedByte();
        interlaceMethod = byteReader.readNextUnsignedByte();
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
}
