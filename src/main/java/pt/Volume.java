package pt;

import java.awt.image.BufferedImage;

import static pt.Hit.NO_HIT;

public class Volume implements Shape {

    private final int width;
    private final int height;
    private final int depth;
    private final double zScale;
    private final double[] data;
    private final VolumeWindow[] windows;
    private final Box box;

    public Volume(Box box, BufferedImage[] images, double sliceSpacing, VolumeWindow[] windows) {
        var w = images[0].getWidth();
        var h = images[0].getHeight();
        var d = images.length;
        // TODO: w/h aspect ratio
        var zs = sliceSpacing * (double)d / (double)w;
        var data = new double[w * h * d];
        for (var z = 0; z < images.length; z++) {
            var im = images[z];
            for (var y = 0; y < h; y++) {
                for (var x = 0; x < w; x++) {
                    var r = im.getRGB(x, y) >> 0x10 & 0xFF;
                    data[x + y * w + z * w * h] = (double)r / 255.0;
                }
            }
        }
        width = w;
        height = h;
        depth = d;
        zScale = zs;
        this.data = data;
        this.windows = windows;
        this.box = box;
    }

    public double get(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth) {
            return 0.0;
        }
        return data[x + y * width + z * width * height];
    }

    public double sample(double x, double y, double z) {
        z /= zScale;
        x = (x + 1.0) / 2.0 * (double)width;
        y = (y + 1.0) / 2.0 * (double)height;
        z = (z + 1.0) / 2.0 * (double)depth;
        var x0 = (int)Math.floor(x);
        var y0 = (int)Math.floor(y);
        var z0 = (int)Math.floor(z);
        var x1 = x0 + 1;
        var y1 = y0 + 1;
        var z1 = z0 + 1;
        var v000 = get(x0, y0, z0);
        var v001 = get(x0, y0, z1);
        var v010 = get(x0, y1, z0);
        var v011 = get(x0, y1, z1);
        var v100 = get(x1, y0, z0);
        var v101 = get(x1, y0, z1);
        var v110 = get(x1, y1, z0);
        var v111 = get(x1, y1, z1);
        x -= x0;
        y -= y0;
        z -= z0;
        var c00 = v000 * (1.0 - x) + v100 * x;
        var c01 = v001 * (1.0 - x) + v101 * x;
        var c10 = v010 * (1.0 - x) + v110 * x;
        var c11 = v011 * (1.0 - x) + v111 * x;
        var c0 = c00 * (1.0 - y) + c10 * y;
        var c1 = c01 * (1.0 - y) + c11 * y;
        return c0 * (1.0 - z) + c1 * z;
    }

    @Override
    public void compile() {
    }

    @Override
    public Box boundingBox() {
        return box;
    }

    public int sign(Vector a) {
        var s = sample(a.x, a.y, a.z);
        for (var i = 0; i < windows.length; i++) {
            var window = windows[i];
            if (s < window.lo) {
                return i + 1;
            }
            if (s > window.hi) {
                continue;
            }
            return 0;
        }
        return windows.length + 1;
    }

    @Override
    public Hit intersect(Ray ray) {
        var intersect = box.intersect(ray);
        var tmin = intersect[0];
        var tmax = intersect[1];
        var step = 1.0 / 512.0;
        var start = Math.max(step, tmin);
        var sign = -1;
        for (var t = start; t <= tmax; t += step) {
            var p = ray.position(t);
            var s = sign(p);
            if (s == 0 || sign >= 0 && s != sign) {
                t -= step;
                step /= 64.0;
                t += step;
                for (var i = 0; i < 64; i++) {
                    if (sign(ray.position(t)) == 0) {
                        return new Hit(this, t - step, null);
                    }
                    t += step;
                }
            }
            sign = s;
        }
        return NO_HIT;
    }

    @Override
    public Vector uv(Vector p) {
        return Vector.ZERO; // not implemented
    }

    @Override
    public Material materialAt(Vector p) {
        var be = 1000000000.0;
        var bm = Material.DEFAULT;
        var s = sample(p.x, p.y, p.z);
        for (var window : windows) {
            if (s >= window.lo && s <= window.hi) {
                return window.material;
            }
            var e = Math.min(Math.abs(s - window.lo), Math.abs(s - window.hi));
            if (e < be) {
                be = e;
                bm = window.material;
            }
        }
        return bm;
    }

    @Override
    public Vector normalAt(Vector p) {
        var eps = 0.001;
        return new Vector(sample(p.x - eps, p.y, p.z) - sample(p.x + eps, p.y, p.z),
                          sample(p.x, p.y - eps, p.z) - sample(p.x, p.y + eps, p.z),
                          sample(p.x, p.y, p.z - eps) - sample(p.x, p.y, p.z + eps)).normalize();
    }
}
