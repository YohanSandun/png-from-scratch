package lk.ysk.spike.png;

public class IENDChunk extends PngChunk {

    public IENDChunk(int length, byte[] data, int crc) {
        super(length, Constants.IEND, data, crc);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
