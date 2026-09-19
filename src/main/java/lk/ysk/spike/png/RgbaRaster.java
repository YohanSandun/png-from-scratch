package lk.ysk.spike.png;

import lk.ysk.spike.png.chunk.IHDRChunk;

public class RgbaRaster implements Raster {

    private final byte[][] pixels;
    private final IHDRChunk ihdrChunk;

    public RgbaRaster(byte[][] pixels, IHDRChunk ihdrChunk) {
        this.pixels = pixels;
        this.ihdrChunk = ihdrChunk;
    }

    @Override
    public int width() {
        return ihdrChunk.getWidth();
    }

    @Override
    public int height() {
        return ihdrChunk.getHeight();
    }

    @Override
    public int getArgb(int x, int y) {
        x = x * ihdrChunk.getBytesPerPixel();
        return (pixels[y][x+3] & 0xFF) << 24 | (pixels[y][x] & 0xFF) << 16 | (pixels[y][x+1] & 0xFF) << 8 | (pixels[y][x+2] & 0xFF);
    }

    @Override
    public int getRgba(int x, int y) {
        x = x * ihdrChunk.getBytesPerPixel();
        return (pixels[y][x] & 0xFF) << 24 | (pixels[y][x+1] & 0xFF) << 16 | (pixels[y][x+2] & 0xFF) << 8 | (pixels[y][x+3] & 0xFF);
    }

}
