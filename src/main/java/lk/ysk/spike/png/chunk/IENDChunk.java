package lk.ysk.spike.png.chunk;

import lk.ysk.spike.png.Constants;

public class IENDChunk extends PngChunk {

    public IENDChunk(int length, byte[] data, int crc) {
        super(length, Constants.IEND, data, crc);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
