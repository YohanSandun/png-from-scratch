package lk.ysk.spike;

public abstract class Chunk {

    private int length;
    private byte[] data;
    private int crc;
    private ChunkType type;

    public Chunk(int length, ChunkType type, byte[] data, int crc) {
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

    public ChunkType getType() {
        return type;
    }

    public void setType(ChunkType type) {
        this.type = type;
    }

    public abstract boolean isValid();

    @Override
    public String toString() {
        return """
        -----CHUNK-----
        Type:\t %s
        Length:\t %d
        CRC:\t %d
        ---------------
        """.formatted(type.toString(), length, crc);
    }
}
