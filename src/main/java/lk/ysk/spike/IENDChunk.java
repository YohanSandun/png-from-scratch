package lk.ysk.spike;

public class IENDChunk extends PngChunk {

    public static final byte[] IEND_CHUNK_TYPE = {
            (byte)0x49,
            0x45,
            0x4E,
            0x44
    };

    public IENDChunk(int length, byte[] data, int crc) {
        super(length, ChunkType.IEND, data, crc);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
