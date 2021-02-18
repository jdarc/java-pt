package pt;

public class Color {

    public static final Color BLACK = new Color(0.0, 0.0, 0.0);
    public static final Color WHITE = new Color(1.0, 1.0, 1.0);

    public final double r;
    public final double g;
    public final double b;

    public Color(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(Color c) {
        r = c.r;
        g = c.g;
        b = c.b;
    }

    public static Color hexColor(int x) {
        var r = (double)(x >>> 0x10 & 0xff) / 255.0;
        var g = (double)(x >>> 0x08 & 0xff) / 255.0;
        var b = (double)(x & 0xff) / 255.0;
        return new Color(r, g, b).pow(2.2);
    }

    public static Color kelvin(double k) {
        double red, grn, blu;

        // red
        if (k >= 6600.0) {
            var a = 351.97690566805693;
            var b = 0.114206453784165;
            var c = -40.25366309332127;
            var x = k / 100.0 - 55.0;
            red = a + b * x + c * StrictMath.log(x);
        } else {
            red = 255.0;
        }

        // green
        if (k >= 6600.0) {
            var a = 325.4494125711974;
            var b = 0.07943456536662342;
            var c = -28.0852963507957;
            var x = k / 100.0 - 50.0;
            grn = a + b * x + c * StrictMath.log(x);
        } else if (k >= 1000.0) {
            var a = -155.25485562709179;
            var b = -0.44596950469579133;
            var c = 104.49216199393888;
            var x = k / 100.0 - 2.0;
            grn = a + b * x + c * StrictMath.log(x);
        } else {
            grn = 0.0;
        }

        // blue
        if (k >= 6600.0) {
            blu = 255.0;
        } else if (k >= 2000.0) {
            var a = -254.76935184120902;
            var b = 0.8274096064007395;
            var c = 115.67994401066147;
            var x = k / 100.0 - 10.0;
            blu = a + b * x + c * StrictMath.log(x);
        } else {
            blu = 0.0;
        }

        red = Math.min(1.0, red / 255.0);
        grn = Math.min(1.0, grn / 255.0);
        blu = Math.min(1.0, blu / 255.0);
        return new Color(red, grn, blu);
    }

    public Color add(Color c) {
        return new Color(r + c.r, g + c.g, b + c.b);
    }

    public Color sub(Color c) {
        return new Color(r - c.r, g - c.g, b - c.b);
    }

    public Color mul(Color c) {
        return new Color(r * c.r, g * c.g, b * c.b);
    }

    public Color mulScalar(double s) {
        return new Color(r * s, g * s, b * s);
    }

    public Color divScalar(double s) {
        return new Color(r / s, g / s, b / s);
    }

    public Color min(Color c) {
        return new Color(Math.min(r, c.r), Math.min(g, c.g), Math.min(b, c.b));
    }

    public Color max(Color c) {
        return new Color(Math.max(r, c.r), Math.max(g, c.g), Math.max(b, c.b));
    }

    public double minComponent() {
        return Math.min(Math.min(r, g), b);
    }

    public double maxComponent() {
        return Math.max(Math.max(r, g), b);
    }

    public Color pow(double s) {
        return new Color(StrictMath.pow(r, s), StrictMath.pow(g, s), StrictMath.pow(b, s));
    }

    public Color mix(Color c, double pct) {
        var a = mulScalar(1.0 - pct);
        c = c.mulScalar(pct);
        return a.add(c);
    }

    public int toRGBA() {
        var r = (int)Math.max(0.0, Math.min(255.0, this.r * 255.0));
        var g = (int)Math.max(0.0, Math.min(255.0, this.g * 255.0));
        var b = (int)Math.max(0.0, Math.min(255.0, this.b * 255.0));
        return 0xFF000000 | r << 0x10 | g << 0x08 | b;
    }
}
