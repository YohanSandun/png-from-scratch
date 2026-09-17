package lk.ysk.spike.png;

import lk.ysk.spike.io.ByteReader;

public class tEXtChunk extends PngChunk {

    private final String keyword;
    private final String text;

    public tEXtChunk(int length, byte[] data, int crc) {
        super(length, Constants.tEXt, data, crc);

        ByteReader byteReader = new ByteReader(data);
        keyword = byteReader.readString();
        byteReader.skipNextByte(); // null separator (terminator)
        text = byteReader.readString(length - keyword.length() - 1);
    }

    @Override
    public boolean isValid() {
        return true;
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
