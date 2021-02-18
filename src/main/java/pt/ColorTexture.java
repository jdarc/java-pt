package pt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pt.Color.BLACK;
import static pt.Util.clampInt;
import static pt.Util.fract;

public class ColorTexture implements Texture {

    public final int width;
    public final int height;
    public final Color[] data;

    public ColorTexture(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();
        data = new Color[width * height];
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                data[y * width + x] = Color.hexColor(image.getRGB(x, y));
            }
        }
    }

    public Color bilinearSample(double u, double v) {
        var v1 = u * ((double)width - 1.5);
        var v2 = v * ((double)height - 1.5);
        var x0 = (int)v1;
        var y0 = (int)v2;
        var x1 = x0 + 1;
        var y1 = y0 + 1;
        var x = v1 - (double)x0;
        var y = v2 - (double)y0;
        var c = BLACK;
        c = c.add(data[y0 * width + x0].mulScalar((1.0 - x) * (1.0 - y)));
        c = c.add(data[y0 * width + x1].mulScalar(x * (1.0 - y)));
        c = c.add(data[y1 * width + x0].mulScalar((1.0 - x) * y));
        c = c.add(data[y1 * width + x1].mulScalar(x * y));
        return c;
    }

    @Override
    public Color sample(double u, double v) {
        u = fract(fract(u) + 1.0);
        v = fract(fract(v) + 1.0);
        return bilinearSample(u, 1.0 - v);
    }

    @Override
    public Vector normalSample(double u, double v) {
        var c = sample(u, v);
        return new Vector(c.r * 2.0 - 1.0, c.g * 2.0 - 1.0, c.b * 2.0 - 1.0).normalize();
    }

    @Override
    public Vector bumpSample(double u, double v) {
        u = fract(fract(u) + 1.0);
        v = fract(fract(v) + 1.0);
        v = 1.0 - v;
        var x = (int)(u * (double)width);
        var y = (int)(v * (double)height);
        var x1 = clampInt(x - 1, 0, width - 1);
        var x2 = clampInt(x + 1, 0, width - 1);
        var y1 = clampInt(y - 1, 0, height - 1);
        var y2 = clampInt(y + 1, 0, height - 1);
        var cx = data[y * width + x1].sub(data[y * width + x2]);
        var cy = data[y1 * width + x].sub(data[y2 * width + x]);
        return new Vector(cx.r, cy.r, 0.0);
    }

    @Override
    public void pow(double a) {
        for (var i = 0; i < data.length; i++) {
            data[i] = data[i].pow(a);
        }
    }

    @Override
    public Texture mulScalar(double a) {
        for (var i = 0; i < data.length; i++) {
            data[i] = data[i].mulScalar(a);
        }
        return this;
    }

    private static final Map<String, Texture> TEXTURES = new HashMap<>();

    public static Texture getTexture(String path) {
        if (TEXTURES.containsKey(path)) {
            return TEXTURES.get(path);
        }

        try {
            var texture = loadTexture(path);
            TEXTURES.put(path, texture);
            return texture;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static Texture loadTexture(String path) throws IOException {
        System.out.printf("Loading IMG: %s\n", path);
        return new ColorTexture(ImageIO.read(new File(path)));
    }
}
