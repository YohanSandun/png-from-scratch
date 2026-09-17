package lk.ysk.spike.png;

public class PngChunk {

    private int length;
    private byte[] data;
    private int crc;
    private String type;

    public PngChunk(int length, String type, byte[] data, int crc) {
        this.length = length;
        this.data = data;
        this.crc = crc;
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return """
        -----CHUNK-----
        Type:\t %s
        Length:\t %d
        CRC:\t %d
        ---------------
        """.formatted(type, length, crc);
    }
}
