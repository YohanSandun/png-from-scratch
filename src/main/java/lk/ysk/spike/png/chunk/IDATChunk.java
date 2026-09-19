package lk.ysk.spike.png.chunk;

import lk.ysk.spike.png.Constants;

public class IDATChunk extends PngChunk {

    public IDATChunk(int length, byte[] data, int crc) {
        super(length, Constants.IDAT, data, crc);
    }

    @Override
    public boolean isValid() {
        return true;
    }

}
