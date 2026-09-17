package lk.ysk.spike.png;

import lk.ysk.spike.io.ByteReader;

public class PngReader {

    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89,
            0x50,
            0x4E,
            0x47,
            0x0D,
            0x0A,
            0x1A,
            0x0A
    };

    private final ByteReader byteReader;

    public PngReader(ByteReader byteReader) {
        this.byteReader = byteReader;
    }

    public boolean isPng() {
        byteReader.setPos(0);
        for (byte b : PNG_SIGNATURE) {
            if (byteReader.readNextByte() != b) {
                return false;
            }
        }
        return true;
    }

    public PngChunk readChunk() {
        int length = byteReader.readNextInt32();
        String type = byteReader.readString(4);
        byte[] data = byteReader.readBytes(length);
        int crc = byteReader.readNextInt32();

        return switch (type) {
            case Constants.IHDR -> new IHDRChunk(length, data, crc);
            case Constants.IDAT -> new IDATChunk(length, data, crc);
            case Constants.IEND -> new IENDChunk(length, data, crc);
            case Constants.tEXt -> new tEXtChunk(length, data, crc);
            case Constants.iTXt -> new iTXtChunk(length, data, crc);
            case Constants.sRGB -> new sRGBChunk(length, data, crc);
            case Constants.gAMA -> new gAMAChunk(length, data, crc);
            default -> {
                System.out.println("Skipping unknown chunk type: " + type);
                yield new PngChunk(length, type, data, crc);
            }
        };
    }

}
