package pt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Buffer {

    private final int width;
    private final int height;
    private final Pixel[] pixels;

    public Buffer(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Pixel[width * height];
        for (var i = 0; i < pixels.length; i++) {
            pixels[i] = new Pixel();
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void addSample(int x, int y, Color sample) {
        pixels[y * width + x].addSample(sample);
    }

    public int samples(int x, int y) {
        return pixels[y * width + x].samples();
    }

    public Color color(int x, int y) {
        return pixels[y * width + x].color();
    }

    public Color variance(int x, int y) {
        return pixels[y * width + x].variance();
    }

    public Color standardDeviation(int x, int y) {
        return pixels[y * width + x].standardDeviation();
    }

    public BufferedImage image(Channel channel) {
        var result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var maxSamples = 0.0;
        if (channel == Channel.SAMPLES_CHANNEL) {
            for (var pixel : pixels) {
                maxSamples = Math.max(maxSamples, pixel.samples());
            }
        }

        var buffer = ((DataBufferInt)result.getRaster().getDataBuffer()).getData();
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                Color c;
                switch (channel) {
                    case COLOR_CHANNEL:
                        c = pixels[y * width + x].color().pow(1.0 / 2.2);
                        break;
                    case VARIANCE_CHANNEL:
                        c = pixels[y * width + x].variance();
                        break;
                    case STANDARD_DEVIATION_CHANNEL:
                        c = pixels[y * width + x].standardDeviation();
                        break;
                    default:
                        var p = (double)pixels[y * width + x].samples() / maxSamples;
                        c = new Color(p, p, p);
                        break;
                }
                buffer[y * width + x] = c.toRGBA();
            }
        }

        return result;
    }
}
