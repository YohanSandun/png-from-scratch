package lk.ysk.spike.png;

public class sRGBChunk extends PngChunk {

    private final int renderingIntent;

    public sRGBChunk(int length, byte[] data, int crc) {
        super(length, Constants.sRGB, data, crc);

        renderingIntent = data[0] & 0xFF;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return """
        -----sRGB-----
        Rendering Intent: %d
        ---------------
        """.formatted(renderingIntent);
    }
}
