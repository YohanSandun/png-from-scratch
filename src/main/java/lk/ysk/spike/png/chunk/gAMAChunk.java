package lk.ysk.spike.png.chunk;

import lk.ysk.spike.png.Constants;

public class gAMAChunk extends PngChunk {

    public gAMAChunk(int length, byte[] data, int crc) {
        super(length, Constants.gAMA, data, crc);
        //TODO: GAMA impl
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
