package lk.ysk.spike.png;

import lk.ysk.spike.compression.ZlibDecoder;
import lk.ysk.spike.io.ByteReader;

import java.util.ArrayList;

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

        ihdrChunk = pngReader.readIHDRChunk();
        while (true) {
            PngChunk chunk = pngReader.readChunk();
            if (chunk == null || chunk.getType() == ChunkType.IEND) {
                break;
            }

            if (chunk.getType() == ChunkType.IDAT) {
                IDATChunk idatChunk = (IDATChunk) chunk;
                idatSize += idatChunk.getLength();
                idatChunks.add(idatChunk);
            } else {
                ancillaryChunks.add(chunk);
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
        System.out.println(zlibDecoder);
    }

    public int getWidth() {
        return ihdrChunk.getWidth();
    }

    public int getHeight() {
        return ihdrChunk.getHeight();
    }

}
