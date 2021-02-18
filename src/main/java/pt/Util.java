package pt;

import java.time.Duration;

import static pt.Common.EPS;
import static pt.Vector.randomUnitVector;

public class Util {

    public static double radians(double degrees) {
        return degrees * Math.PI / 180.0;
    }

    public static double degrees(double radians) {
        return radians * 180.0 / Math.PI;
    }

    public static Vector cone(Vector direction, double theta, double u, double v) {
        if (theta < EPS) {
            return direction;
        }
        theta *= 1.0 - 2.0 * StrictMath.acos(u) / Math.PI;
        var m1 = StrictMath.sin(theta);
        var m2 = StrictMath.cos(theta);
        var a = v * 2.0 * Math.PI;
        var q = randomUnitVector();
        var s = direction.cross(q);
        var t = direction.cross(s);
        var d = Vector.ZERO;
        d = d.add(s.mulScalar(m1 * StrictMath.cos(a)));
        d = d.add(t.mulScalar(m1 * StrictMath.sin(a)));
        d = d.add(direction.mulScalar(m2));
        d = d.normalize();
        return d;
    }

    public static double median(double[] items) {
        var n = items.length;
        if (n == 0) {
            return 0.0;
        } else if (n % 2 == 1) {
            return items[n / 2];
        } else {
            var a = items[n / 2 - 1];
            var b = items[n / 2];
            return (a + b) / 2.0;
        }
    }

    public static String durationString(Duration d) {
        var h = (int)d.toHours();
        var m = (int)d.toMinutes() % 60;
        var s = (int)d.getSeconds() % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public static String numberString(double x) {
        String[] suffixes = {"", "k", "M", "G"};
        for (var suffix : suffixes) {
            if (x < 1000.0) {
                return String.format("%.1f%s", x, suffix);
            }
            x /= 1000.0;
        }
        return String.format("%.1f%s", x, "T");
    }

    public static double fract(double x) {
        return x % 1.0;
    }

    public static double clamp(double x, double lo, double hi) {
        return x < lo ? lo : Math.min(x, hi);
    }

    public static int clampInt(int x, int lo, int hi) {
        return x < lo ? lo : Math.min(x, hi);
    }

    public static double[] modf(double f) {
        return new double[]{f - f % 1.0, f % 1.0};
    }
}
