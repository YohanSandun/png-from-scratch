package lk.ysk.spike;

import java.util.ArrayList;

public class PngImage {

    private final PngReader pngReader;
    private IHDRChunk ihdrChunk;
    private ArrayList<PngChunk> ancillaryChunks = new ArrayList<>();

    public PngImage(ByteReader byteReader) {
        this.pngReader = new PngReader(byteReader);
        decode();
    }

    private void decode() {
        if (!pngReader.isPng()) {
            throw new IllegalArgumentException("Not a valid PNG image");
        }

        ihdrChunk = pngReader.readIHDRChunk();
        while (true) {
            PngChunk chunk = pngReader.readChunk();
            if (chunk == null) {
                break;
            }
            ancillaryChunks.add(chunk);
        }
    }

    public int getWidth() {
        return ihdrChunk.getWidth();
    }

    public int getHeight() {
        return ihdrChunk.getHeight();
    }

}
