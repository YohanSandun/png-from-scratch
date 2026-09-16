package lk.ysk.spike;

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

    public boolean verifyChunkType(byte[] compareTo) {
        for (byte b : compareTo) {
            if (byteReader.readNextByte() != b) {
                return false;
            }
        }
        return true;
    }

    private boolean isByteSequenceSame(byte[] bytesA, byte[] bytesB) {
        if (bytesA.length != bytesB.length) return false;

        for (int i = 0; i < bytesB.length; i++) {
            if (bytesB[i] != bytesA[i]) {
                return false;
            }
        }
        return true;
    }

    private ChunkType readChunkType() {
        byte[] bytes = byteReader.readBytes(4);
        if (isByteSequenceSame(bytes, IHDRChunk.IHDR_CHUNK_TYPE)) {
            return ChunkType.IHDR;
        }
        else if (isByteSequenceSame(bytes, tEXtChunk.tEXt_CHUNK_TYPE)) {
            return ChunkType.tEXt;
        }
        return ChunkType.INVALID;
    }

    public IHDRChunk readIHDRChunk() {
        int length = byteReader.readNextInt32();
        if (verifyChunkType(IHDRChunk.IHDR_CHUNK_TYPE)) {
            return new IHDRChunk(length, byteReader.readBytes(length), byteReader.readNextInt32());
        }
        throw new IllegalArgumentException("IHDR chunk not found!");
    }

    public PngChunk readChunk() {
        int length = byteReader.readNextInt32();
        ChunkType type = readChunkType();
        byte[] data = byteReader.readBytes(length);
        int crc = byteReader.readNextInt32();

        return switch (type) {
            case IHDR -> new IHDRChunk(length, data, crc);
            case tEXt -> new tEXtChunk(length, data, crc);
            default -> null;
        };
    }

}
