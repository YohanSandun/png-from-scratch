package lk.ysk.spike.compression;

import lk.ysk.spike.io.BitReader;

public class ZlibDecoder {

    private final byte[] data;
    private final int compressionMethod;
    private final int compressionInfo;
    private final int checkBits;
    private final int presetDictionary;
    private final int compressionLevel;

    public ZlibDecoder(byte[] data) {
        this.data = data;

        BitReader bitReader = new BitReader(data);

        if (!hasValidFCHECK(bitReader)) {
            throw new IllegalArgumentException("Invalid FECHECK checksum");
        }

        compressionMethod = bitReader.readNextBits(4);
        compressionInfo = bitReader.readNextBits(4);
        checkBits = bitReader.readNextBits(5);
        presetDictionary = bitReader.readNextBit();
        compressionLevel = bitReader.readNextBits(2);

        if (presetDictionary == 1) {
            throw new IllegalArgumentException("Preset dictionaries are not supported in PNGs");
        }
    }

    public byte[] decode() {
        byte[] deflate = new byte[data.length-2];
        System.arraycopy(data, 2, deflate, 0, deflate.length);
        DeflateDecoder deflateDecoder = new DeflateDecoder(deflate);
        return deflateDecoder.decode();
    }

    private boolean hasValidFCHECK(BitReader bitReader) {
        return bitReader.readUnsignedInt16BigEndian(0) % 31 == 0;
    }

    @Override
    public String toString() {
        return """
        -----ZLIB-----
        Compression Method:\t %d
        Compression Info:\t %d
        Check Bits:\t\t\t %d
        Preset Dictionary:\t %d
        Compression Level:\t %d
        --------------
        """.formatted(compressionMethod, compressionInfo, checkBits, presetDictionary, compressionLevel);
    }
}
