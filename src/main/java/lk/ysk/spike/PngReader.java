package lk.ysk.spike;

public class PngReader {

    private static final int PNG_SIGNATURE_START = 0;
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

    private static final int IHDR_CHUNK_START = PNG_SIGNATURE.length;
    private static final byte[] IHDR_CHUNK_TYPE = {
            (byte) 0x49,
            0x48,
            0x44,
            0x52
    };

    private final ByteReader byteReader;

    public PngReader(ByteReader byteReader) {
        this.byteReader = byteReader;
    }

    public boolean isPng() {
        byteReader.setPos(PNG_SIGNATURE_START);
        for (byte b : PNG_SIGNATURE) {
            if (byteReader.readNextByte() != b) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyChunkType(byte[] type) {
        for (byte b : type) {
            if (byteReader.readNextByte() != b) {
                return false;
            }
        }
        return true;
    }

    public IHDRChunk getIHDRChunk() {
        byteReader.setPos(IHDR_CHUNK_START);
        int length = byteReader.readNextInt32();
        if (verifyChunkType(IHDR_CHUNK_TYPE)) {
            return new IHDRChunk(length, byteReader.readBytes(length), byteReader.readNextInt32());
        }
        return null;
    }

}
