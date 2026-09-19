package lk.ysk.spike;

import lk.ysk.spike.io.ByteReader;
import lk.ysk.spike.png.PngImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("PNG From Scratch");

        InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("sample-images/sample.png");

        assert input != null;

        ByteReader byteReader = new ByteReader(input.readAllBytes());
        PngImage png = new PngImage(byteReader);
        pngToHtml(png.getPixels());
        System.out.printf("Image Size: %d x %d\n", png.getWidth(), png.getHeight());
    }

    private static void pngToHtml(byte[][] pixels) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<body style=\"background: #191A1C;\">\n");

        sb.append("<div style=\"display: block;\">\n");
        for (int i = 0; i < pixels.length; i++) {
            sb.append("<div style=\"display: flex\">\n");
            for (int j = 0; j < pixels[i].length; j += 4) {
                sb.append("<span style=\"width: 2px; height: 2px; background: rgba(");
                sb.append(pixels[i][j] & 0xFF).append(", ");
                sb.append(pixels[i][j + 1] & 0xFF).append(", ");
                sb.append(pixels[i][j + 2] & 0xFF).append(", ");
                sb.append((pixels[i][j + 3] & 0xFF)/255d).append(");\"></span>");
            }
            sb.append("</div>\n");
        }
        sb.append("</div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        Path path = Path.of("src/main/resources/output.html");
        Files.writeString(path, sb.toString());
    }
}