package pt;

public class MatrixFactory {

    public static Matrix identity() {
        return new Matrix(1.0, 0.0, 0.0, 0.0,
                          0.0, 1.0, 0.0, 0.0,
                          0.0, 0.0, 1.0, 0.0,
                          0.0, 0.0, 0.0, 1.0);
    }

    public static Matrix translation(Vector v) {
        return new Matrix(1.0, 0.0, 0.0, v.x,
                          0.0, 1.0, 0.0, v.y,
                          0.0, 0.0, 1.0, v.z,
                          0.0, 0.0, 0.0, 1.0);
    }

    public static Matrix scaling(Vector v) {
        return new Matrix(v.x, 0.0, 0.0, 0.0,
                          0.0, v.y, 0.0, 0.0,
                          0.0, 0.0, v.z, 0.0,
                          0.0, 0.0, 0.0, 1.0);
    }

    public static Matrix rotation(Vector v, double a) {
        v = v.normalize();
        var s = StrictMath.sin(a);
        var c = StrictMath.cos(a);
        var m = 1.0 - c;
        return new Matrix(m * v.x * v.x + c, m * v.x * v.y + v.z * s, m * v.z * v.x - v.y * s, 0.0,
                          m * v.x * v.y - v.z * s, m * v.y * v.y + c, m * v.y * v.z + v.x * s, 0.0,
                          m * v.z * v.x + v.y * s, m * v.y * v.z - v.x * s, m * v.z * v.z + c, 0.0,
                          0.0, 0.0, 0.0, 1.0);
    }

    public static Matrix frustum(double l, double r, double b, double t, double n, double f) {
        var t1 = 2.0 * n;
        var t2 = r - l;
        var t3 = t - b;
        var t4 = f - n;
        return new Matrix(t1 / t2, 0.0, (r + l) / t2, 0.0,
                          0.0, t1 / t3, (t + b) / t3, 0.0,
                          0.0, 0.0, (-f - n) / t4, -t1 * f / t4,
                          0.0, 0.0, -1.0, 0.0);
    }

    public static Matrix orthographic(double l, double r, double b, double t, double n, double f) {
        return new Matrix(2.0 / (r - l), 0.0, 0.0, -(r + l) / (r - l),
                          0.0, 2.0 / (t - b), 0.0, -(t + b) / (t - b),
                          0.0, 0.0, -2.0 / (f - n), -(f + n) / (f - n),
                          0.0, 0.0, 0.0, 1.0);
    }

    public static Matrix perspective(double fovy, double aspect, double near, double far) {
        var ymax = near * StrictMath.tan(fovy * Math.PI / 360.0);
        var xmax = ymax * aspect;
        return frustum(-xmax, xmax, -ymax, ymax, near, far);
    }

    public static Matrix lookAtMatrix(Vector eye, Vector center, Vector up) {
        up = up.normalize();
        var f = center.sub(eye).normalize();
        var s = f.cross(up).normalize();
        var u = s.cross(f);
        var m = new Matrix(s.x, u.x, f.x, 0.0,
                           s.y, u.y, f.y, 0.0,
                           s.z, u.z, f.z, 0.0,
                           0.0, 0.0, 0.0, 1.0);
        return m.transpose().inverse().translate(eye);
    }
}
