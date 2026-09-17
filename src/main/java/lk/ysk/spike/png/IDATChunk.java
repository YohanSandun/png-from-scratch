package lk.ysk.spike.png;

public class IDATChunk extends PngChunk {

    public IDATChunk(int length, byte[] data, int crc) {
        super(length, Constants.IDAT, data, crc);
    }

    @Override
    public boolean isValid() {
        return true;
    }

}
