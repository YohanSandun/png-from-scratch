package lk.ysk.spike;

import lk.ysk.spike.compression.DeflateDecoder;
import lk.ysk.spike.io.BitReader;
import lk.ysk.spike.io.ByteReader;
import lk.ysk.spike.png.PngImage;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        testDeflate();
//        System.out.println("PNG From Scratch");
//
//        InputStream input = Main.class
//                .getClassLoader()
//                .getResourceAsStream("sample-images/sample1.png");
//
//        assert input != null;
//
//        ByteReader byteReader = new ByteReader(input.readAllBytes());
//        PngImage png = new PngImage(byteReader);
//
//        System.out.printf("Image Size: %d x %d\n", png.getWidth(), png.getHeight());
    }

    private static void testDeflate() throws IOException {
        InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("test-data/fixed.deflate");
        assert input != null;

        DeflateDecoder deflateDecoder = new DeflateDecoder(input.readAllBytes());
    }
}