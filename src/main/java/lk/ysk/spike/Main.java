package lk.ysk.spike;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("PNG From Scratch");

        InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("sample-images/sample.png");

        assert input != null;

        ByteReader byteReader = new ByteReader(input.readAllBytes());
        PngReader pngReader = new PngReader(byteReader);

        System.out.println(pngReader.isPng());
        System.out.println(pngReader.getIHDRChunk());

        //System.out.printf("%X%n", byteReader.getNextByte());
    }
}