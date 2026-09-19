package lk.ysk.spike.png;

import lk.ysk.spike.compression.ZlibDecoder;
import lk.ysk.spike.io.ByteReader;
import lk.ysk.spike.png.chunk.IDATChunk;
import lk.ysk.spike.png.chunk.IHDRChunk;
import lk.ysk.spike.png.chunk.PngChunk;

import java.util.ArrayList;
import java.util.Arrays;

public class PngImage {

    private final PngReader pngReader;
    private IHDRChunk ihdrChunk;
    private final ArrayList<PngChunk> ancillaryChunks = new ArrayList<>();
    private final ArrayList<IDATChunk> idatChunks = new ArrayList<>();
    private int idatSize = 0;
    private final byte[][] pixels;
    private Raster raster;

    public PngImage(ByteReader byteReader) {
        this.pngReader = new PngReader(byteReader);
        this.pixels = decode();

        if (ihdrChunk.getColorType() == Constants.ColorTypes.TRUE_COLOR_WITH_ALPHA) {
            raster = new RgbaRaster(pixels, ihdrChunk);
        }
    }

    public Raster getRaster() {
        return raster;
    }

    private byte[][] decode() {
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

        byte[] idat = new byte[idatSize];
        int i = 0;
        for (IDATChunk chunk: idatChunks) {
            System.arraycopy(chunk.getData(), 0, idat, i, chunk.getLength());
            i += chunk.getLength();
        }

        ZlibDecoder zlibDecoder = new ZlibDecoder(idat);
        PngRow[] rows = decodeScanline(zlibDecoder.decode());

        PngFilters filters = new PngFilters(rows, ihdrChunk);
        return filters.removeFilters();
    }

    private PngRow[] decodeScanline(byte[] decoded) {
        PngRow[] imageData = new PngRow[ihdrChunk.getHeight()];
        int rowBytes = ihdrChunk.getWidth() * ihdrChunk.getBytesPerPixel();
        int index = 0;

        for (int row = 0; row < ihdrChunk.getHeight(); row++) {
            int filterType = decoded[index++] & 0xFF;
            imageData[row] = new PngRow(filterType, Arrays.copyOfRange(decoded, index, index + rowBytes));
            index += rowBytes;
        }

        return imageData;
    }

    public int getWidth() {
        return ihdrChunk.getWidth();
    }

    public int getHeight() {
        return ihdrChunk.getHeight();
    }

}
