package lk.ysk.spike.png;

import lk.ysk.spike.png.chunk.IHDRChunk;

public class PngFilters {

    private final PngRow[] rows;
    private final IHDRChunk ihdrChunk;
    private final int bytesPerPixel;

    public PngFilters(PngRow[] rows, IHDRChunk ihdrChunk) {
        this.rows = rows;
        this.ihdrChunk = ihdrChunk;
        this.bytesPerPixel = ihdrChunk.getBytesPerPixel();
    }

    private int left(byte[] row, int index) {
        if (index < bytesPerPixel) {
            return 0;
        }
        return row[index - bytesPerPixel] & 0xFF;
    }

    public byte[][] removeFilters() {
        byte[][] pixels = new byte[ihdrChunk.getHeight()][ihdrChunk.getWidth()];
        for (int row = 0; row < rows.length; row++) {
            int filterType = rows[row].getFilterType();

            if (filterType == 1) {
                removeSubFilter(rows[row].getRow());
            } else if (filterType == 2) {
                removeUpFilter(rows[row].getRow(), rows[row-1].getRow());
            } else if (filterType == 3) {
                removeAverageFilter(rows[row].getRow(), rows[row-1].getRow());
            } else if (filterType == 4) {
                removePaethFilter(rows[row].getRow(), rows[row-1].getRow());
            } else if (filterType != 0) {
                throw new IllegalArgumentException("Invalid filter type: " + filterType);
            }

            pixels[row] = rows[row].getRow();
        }
        return pixels;
    }

    private void removeSubFilter(byte[] row) {
        for (int i = 0; i < row.length; i++) {
            row[i] = (byte)(((row[i] & 0xFF) + left(row, i)) % 256);
        }
    }

    private void removeUpFilter(byte[] row, byte[] prevRow) {
        for (int i = 0; i < row.length; i++) {
            row[i] = (byte)(((row[i] & 0xFF) + (prevRow[i] & 0xFF)) % 256);
        }
    }

    private void removeAverageFilter(byte[] row, byte[] prevRow) {
        for (int i = 0; i < row.length; i++) {
            row[i] = (byte)(((row[i] & 0xFF) + ((left(row, i) + (prevRow[i] & 0xFF)) >>> 1)) % 256);
        }
    }

    private void removePaethFilter(byte[] row, byte[] prevRow) {
        for (int i = 0; i < row.length; i++) {
            int a = left(row, i);
            int b = prevRow[i] & 0xFF;
            int c = left(prevRow, i);
            row[i] = (byte)(((row[i] & 0xFF) + calculatePaeth(a, b, c)) % 256);
        }
    }

    private int calculatePaeth(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);

        if (pa <= pb && pa <= pc){
            return a;
        } else if (pb <= pc) {
            return b;
        }
        return c;
    }
}
