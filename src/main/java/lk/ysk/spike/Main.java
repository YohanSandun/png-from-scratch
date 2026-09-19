package lk.ysk.spike;

import lk.ysk.spike.io.ByteReader;
import lk.ysk.spike.png.PngImage;
import lk.ysk.spike.png.Raster;

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
        pngToHtml(png.getRaster());
        System.out.printf("Image Size: %d x %d\n", png.getWidth(), png.getHeight());
    }

    private static void pngToHtml(Raster raster) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<body style=\"background: #191A1C;\">\n<canvas id=\"canvas\"></canvas>\n");
        sb.append("""
                <script>
                const canvas = document.getElementById("canvas");
                canvas.width = %d;
                canvas.height = %d;
                
                const ctx = canvas.getContext("2d");
                const imageData = ctx.createImageData(canvas.width, canvas.height);
                
                imageData.data.set([""".formatted(raster.width(), raster.height()));

        for (int i = 0; i < raster.height(); i++) {
            for (int j = 0; j < raster.width(); j++) {
                int pixel = raster.getRgba(j, i);
                int r = (pixel >> 24) & 0xFF;
                int g = (pixel >> 16) & 0xFF;
                int b = (pixel >> 8) & 0xFF;
                int a = pixel & 0xFF;

                sb.append(r).append(',')
                        .append(g).append(',')
                        .append(b).append(',')
                        .append(a).append(',');
            }
        }

        sb.append("]);\n ctx.putImageData(imageData, 0, 0);\n</script>");
        sb.append("</body>\n");
        sb.append("</html>\n");

        Path path = Path.of("src/main/resources/output.html");
        Files.writeString(path, sb.toString());
    }
}