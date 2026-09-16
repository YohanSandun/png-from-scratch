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
        PngImage png = new PngImage(byteReader);

        System.out.printf("Image Size: %d x %d", png.getWidth(), png.getHeight());
    }
}