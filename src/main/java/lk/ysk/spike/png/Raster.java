package lk.ysk.spike.png;

public interface Raster {

    int width();
    int height();
    int getArgb(int x, int y);
    int getRgba(int x, int y);

}
