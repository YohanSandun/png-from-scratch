package lk.ysk.spike;

public class tEXtChunk extends PngChunk {

    public static final byte[] tEXt_CHUNK_TYPE = {
            (byte) 0x74,
            0x45,
            0x58,
            0x74
    };

    private final String keyword;
    private final String text;

    public tEXtChunk(int length, byte[] data, int crc) {
        super(length, ChunkType.tEXt, data, crc);

        ByteReader byteReader = new ByteReader(data);
        keyword = byteReader.readString();
        byteReader.skipNextByte(); // null separator (terminator)
        text = byteReader.readString(length - keyword.length() - 1);
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return """
        -----tEXt-----
        Keyword:\t %s
        Text:\t\t %s
        ---------------
        """.formatted(keyword, text);
    }
}
