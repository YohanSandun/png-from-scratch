package lk.ysk.spike;

public class IDATChunk extends PngChunk {

    public static final byte[] IDAT_CHUNK_TYPE = {
            (byte) 0x49,
            0x44,
            0x41,
            0x54
    };

    public IDATChunk(int length, byte[] data, int crc) {
        super(length, ChunkType.IDAT, data, crc);
    }

    @Override
    public boolean isValid() {
        return true;
    }

}
