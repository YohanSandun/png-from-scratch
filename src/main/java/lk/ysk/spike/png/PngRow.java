package lk.ysk.spike.png;

public class PngRow {

    private final int filterType;
    private final byte[] row;

    public PngRow(int filterType, byte[] row) {
        this.filterType = filterType;
        this.row = row;
    }

    public int getFilterType() {
        return filterType;
    }

    public byte[] getRow() {
        return row;
    }
}
