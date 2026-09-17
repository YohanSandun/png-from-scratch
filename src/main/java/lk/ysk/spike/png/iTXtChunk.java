package lk.ysk.spike.png;

import lk.ysk.spike.io.ByteReader;

public class iTXtChunk extends PngChunk {

    private final String keyword;
    private final int compressionFlag;
    private final int compressionMethod;
    private final String languageTag;
    private final String translateKeyword;
    private final String text;

    public iTXtChunk(int length, byte[] data, int crc) {
        super(length, Constants.iTXt, data, crc);

        ByteReader byteReader = new ByteReader(data);
        keyword = byteReader.readString();
        byteReader.skipNextByte(); // null separator (terminator)
        compressionFlag = byteReader.readNextUnsignedByte();
        compressionMethod = byteReader.readNextUnsignedByte();
        languageTag = byteReader.readString();
        byteReader.skipNextByte();
        translateKeyword = byteReader.readString();
        byteReader.skipNextByte();
        text = byteReader.readString();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return """
        -----iTXt-----
        Keyword:\t\t %s
        Compression Flag:\t %d
        Compression Method:\t %d
        Language Tag:\t\t %s
        Translate Keyword:\t %s
        Text:\t\t\t %s
        ---------------
        """.formatted(keyword, compressionFlag, compressionMethod, languageTag, translateKeyword, text);
    }
}
