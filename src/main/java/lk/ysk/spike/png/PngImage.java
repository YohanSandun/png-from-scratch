package lk.ysk.spike.png;

import lk.ysk.spike.compression.ZlibDecoder;
import lk.ysk.spike.io.ByteReader;

import java.util.ArrayList;
import java.util.Arrays;

public class PngImage {

    private final PngReader pngReader;
    private IHDRChunk ihdrChunk;
    private final ArrayList<PngChunk> ancillaryChunks = new ArrayList<>();
    private final ArrayList<IDATChunk> idatChunks = new ArrayList<>();
    private int idatSize = 0;

    public PngImage(ByteReader byteReader) {
        this.pngReader = new PngReader(byteReader);
        decode();
    }

    private void decode() {
        if (!pngReader.isPng()) {
            throw new IllegalArgumentException("Not a valid PNG image");
        }

        PngChunk currentChunk = pngReader.readChunk();
        if (!currentChunk.getType().equals(Constants.IHDR)) {
            throw new IllegalArgumentException("First chunk is not IHDR");
        }
        ihdrChunk = (IHDRChunk) currentChunk;

        while (true) {
            currentChunk = pngReader.readChunk();
            if (currentChunk.getType().equals(Constants.IEND)) {
                break;
            } else if (currentChunk.getType().equals(Constants.IDAT)) {
                IDATChunk idatChunk = (IDATChunk) currentChunk;
                idatSize += idatChunk.getLength();
                idatChunks.add(idatChunk);
            } else {
                ancillaryChunks.add(currentChunk);
            }
        }

        // decompression
        byte[] idat = new byte[idatSize];
        int i = 0;
        for (IDATChunk chunk: idatChunks) {
            System.arraycopy(chunk.getData(), 0, idat, i, chunk.getLength());
            i += chunk.getLength();
        }

        ZlibDecoder zlibDecoder = new ZlibDecoder(idat);
        System.out.println(Arrays.toString(zlibDecoder.decode()));
    }

    public int getWidth() {
        return ihdrChunk.getWidth();
    }

    public int getHeight() {
        return ihdrChunk.getHeight();
    }

}
