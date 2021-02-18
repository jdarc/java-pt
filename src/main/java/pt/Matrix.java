package pt;

public class Matrix {

    public final double x00;
    public final double x01;
    public final double x02;
    public final double x03;
    public final double x10;
    public final double x11;
    public final double x12;
    public final double x13;
    public final double x20;
    public final double x21;
    public final double x22;
    public final double x23;
    public final double x30;
    public final double x31;
    public final double x32;
    public final double x33;

    public Matrix(double x00, double x01, double x02, double x03,
                  double x10, double x11, double x12, double x13,
                  double x20, double x21, double x22, double x23,
                  double x30, double x31, double x32, double x33) {
        this.x00 = x00;
        this.x01 = x01;
        this.x02 = x02;
        this.x03 = x03;
        this.x10 = x10;
        this.x11 = x11;
        this.x12 = x12;
        this.x13 = x13;
        this.x20 = x20;
        this.x21 = x21;
        this.x22 = x22;
        this.x23 = x23;
        this.x30 = x30;
        this.x31 = x31;
        this.x32 = x32;
        this.x33 = x33;
    }

    public Matrix translate(Vector v) {
        return MatrixFactory.translation(v).mul(this);
    }

    public Matrix scale(Vector v) {
        return MatrixFactory.scaling(v).mul(this);
    }

    public Matrix rotate(Vector v, double a) {
        return MatrixFactory.rotation(v, a).mul(this);
    }

    public Matrix frustum(double l, double r, double b, double t, double n, double f) {
        return MatrixFactory.frustum(l, r, b, t, n, f).mul(this);
    }

    public Matrix orthographic(double l, double r, double b, double t, double n, double f) {
        return MatrixFactory.orthographic(l, r, b, t, n, f).mul(this);
    }

    public Matrix perspective(double fovy, double aspect, double near, double far) {
        return MatrixFactory.perspective(fovy, aspect, near, far).mul(this);
    }

    public Matrix mul(Matrix b) {
        return new Matrix(x00 * b.x00 + x01 * b.x10 + x02 * b.x20 + x03 * b.x30,
                          x00 * b.x01 + x01 * b.x11 + x02 * b.x21 + x03 * b.x31,
                          x00 * b.x02 + x01 * b.x12 + x02 * b.x22 + x03 * b.x32,
                          x00 * b.x03 + x01 * b.x13 + x02 * b.x23 + x03 * b.x33,
                          x10 * b.x00 + x11 * b.x10 + x12 * b.x20 + x13 * b.x30,
                          x10 * b.x01 + x11 * b.x11 + x12 * b.x21 + x13 * b.x31,
                          x10 * b.x02 + x11 * b.x12 + x12 * b.x22 + x13 * b.x32,
                          x10 * b.x03 + x11 * b.x13 + x12 * b.x23 + x13 * b.x33,
                          x20 * b.x00 + x21 * b.x10 + x22 * b.x20 + x23 * b.x30,
                          x20 * b.x01 + x21 * b.x11 + x22 * b.x21 + x23 * b.x31,
                          x20 * b.x02 + x21 * b.x12 + x22 * b.x22 + x23 * b.x32,
                          x20 * b.x03 + x21 * b.x13 + x22 * b.x23 + x23 * b.x33,
                          x30 * b.x00 + x31 * b.x10 + x32 * b.x20 + x33 * b.x30,
                          x30 * b.x01 + x31 * b.x11 + x32 * b.x21 + x33 * b.x31,
                          x30 * b.x02 + x31 * b.x12 + x32 * b.x22 + x33 * b.x32,
                          x30 * b.x03 + x31 * b.x13 + x32 * b.x23 + x33 * b.x33);
    }

    public Vector mulPosition(Vector b) {
        var x = x00 * b.x + x01 * b.y + x02 * b.z + x03;
        var y = x10 * b.x + x11 * b.y + x12 * b.z + x13;
        var z = x20 * b.x + x21 * b.y + x22 * b.z + x23;
        return new Vector(x, y, z);
    }

    public Vector mulDirection(Vector b) {
        var x = x00 * b.x + x01 * b.y + x02 * b.z;
        var y = x10 * b.x + x11 * b.y + x12 * b.z;
        var z = x20 * b.x + x21 * b.y + x22 * b.z;
        return new Vector(x, y, z).normalize();
    }

    public Ray mulRay(Ray b) {
        return new Ray(mulPosition(b.origin), mulDirection(b.direction));
    }

    public Box mulBox(Box box) {
        var r = new Vector(x00, x10, x20);
        var u = new Vector(x01, x11, x21);
        var b = new Vector(x02, x12, x22);
        var t = new Vector(x03, x13, x23);
        var xa = r.mulScalar(box.min.x);
        var xb = r.mulScalar(box.max.x);
        var ya = u.mulScalar(box.min.y);
        var yb = u.mulScalar(box.max.y);
        var za = b.mulScalar(box.min.z);
        var zb = b.mulScalar(box.max.z);
        var xmin = xa.min(xb);
        var ymin = ya.min(yb);
        var zmin = za.min(zb);
        var xmax = xa.max(xb);
        var ymax = ya.max(yb);
        var zmax = za.max(zb);
        var min = xmin.add(ymin).add(zmin).add(t);
        var max = xmax.add(ymax).add(zmax).add(t);
        return new Box(min, max);
    }

    public Matrix transpose() {
        return new Matrix(x00, x10, x20, x30,
                          x01, x11, x21, x31,
                          x02, x12, x22, x32,
                          x03, x13, x23, x33);
    }

    public double determinant() {
        return x00 * x11 * x22 * x33 - x00 * x11 * x23 * x32 +
               x00 * x12 * x23 * x31 - x00 * x12 * x21 * x33 +
               x00 * x13 * x21 * x32 - x00 * x13 * x22 * x31 -
               x01 * x12 * x23 * x30 + x01 * x12 * x20 * x33 -
               x01 * x13 * x20 * x32 + x01 * x13 * x22 * x30 -
               x01 * x10 * x22 * x33 + x01 * x10 * x23 * x32 +
               x02 * x13 * x20 * x31 - x02 * x13 * x21 * x30 +
               x02 * x10 * x21 * x33 - x02 * x10 * x23 * x31 +
               x02 * x11 * x23 * x30 - x02 * x11 * x20 * x33 -
               x03 * x10 * x21 * x32 + x03 * x10 * x22 * x31 -
               x03 * x11 * x22 * x30 + x03 * x11 * x20 * x32 -
               x03 * x12 * x20 * x31 + x03 * x12 * x21 * x30;
    }

    public Matrix inverse() {
        var m = this;
        var d = 1.0 / m.determinant();
        var x00 = (m.x12 * m.x23 * m.x31 - m.x13 * m.x22 * m.x31 + m.x13 * m.x21 * m.x32 - m.x11 * m.x23 * m.x32 - m.x12 * m.x21 * m.x33 + m.x11 * m.x22 * m.x33) * d;
        var x01 = (m.x03 * m.x22 * m.x31 - m.x02 * m.x23 * m.x31 - m.x03 * m.x21 * m.x32 + m.x01 * m.x23 * m.x32 + m.x02 * m.x21 * m.x33 - m.x01 * m.x22 * m.x33) * d;
        var x02 = (m.x02 * m.x13 * m.x31 - m.x03 * m.x12 * m.x31 + m.x03 * m.x11 * m.x32 - m.x01 * m.x13 * m.x32 - m.x02 * m.x11 * m.x33 + m.x01 * m.x12 * m.x33) * d;
        var x03 = (m.x03 * m.x12 * m.x21 - m.x02 * m.x13 * m.x21 - m.x03 * m.x11 * m.x22 + m.x01 * m.x13 * m.x22 + m.x02 * m.x11 * m.x23 - m.x01 * m.x12 * m.x23) * d;
        var x10 = (m.x13 * m.x22 * m.x30 - m.x12 * m.x23 * m.x30 - m.x13 * m.x20 * m.x32 + m.x10 * m.x23 * m.x32 + m.x12 * m.x20 * m.x33 - m.x10 * m.x22 * m.x33) * d;
        var x11 = (m.x02 * m.x23 * m.x30 - m.x03 * m.x22 * m.x30 + m.x03 * m.x20 * m.x32 - m.x00 * m.x23 * m.x32 - m.x02 * m.x20 * m.x33 + m.x00 * m.x22 * m.x33) * d;
        var x12 = (m.x03 * m.x12 * m.x30 - m.x02 * m.x13 * m.x30 - m.x03 * m.x10 * m.x32 + m.x00 * m.x13 * m.x32 + m.x02 * m.x10 * m.x33 - m.x00 * m.x12 * m.x33) * d;
        var x13 = (m.x02 * m.x13 * m.x20 - m.x03 * m.x12 * m.x20 + m.x03 * m.x10 * m.x22 - m.x00 * m.x13 * m.x22 - m.x02 * m.x10 * m.x23 + m.x00 * m.x12 * m.x23) * d;
        var x20 = (m.x11 * m.x23 * m.x30 - m.x13 * m.x21 * m.x30 + m.x13 * m.x20 * m.x31 - m.x10 * m.x23 * m.x31 - m.x11 * m.x20 * m.x33 + m.x10 * m.x21 * m.x33) * d;
        var x21 = (m.x03 * m.x21 * m.x30 - m.x01 * m.x23 * m.x30 - m.x03 * m.x20 * m.x31 + m.x00 * m.x23 * m.x31 + m.x01 * m.x20 * m.x33 - m.x00 * m.x21 * m.x33) * d;
        var x22 = (m.x01 * m.x13 * m.x30 - m.x03 * m.x11 * m.x30 + m.x03 * m.x10 * m.x31 - m.x00 * m.x13 * m.x31 - m.x01 * m.x10 * m.x33 + m.x00 * m.x11 * m.x33) * d;
        var x23 = (m.x03 * m.x11 * m.x20 - m.x01 * m.x13 * m.x20 - m.x03 * m.x10 * m.x21 + m.x00 * m.x13 * m.x21 + m.x01 * m.x10 * m.x23 - m.x00 * m.x11 * m.x23) * d;
        var x30 = (m.x12 * m.x21 * m.x30 - m.x11 * m.x22 * m.x30 - m.x12 * m.x20 * m.x31 + m.x10 * m.x22 * m.x31 + m.x11 * m.x20 * m.x32 - m.x10 * m.x21 * m.x32) * d;
        var x31 = (m.x01 * m.x22 * m.x30 - m.x02 * m.x21 * m.x30 + m.x02 * m.x20 * m.x31 - m.x00 * m.x22 * m.x31 - m.x01 * m.x20 * m.x32 + m.x00 * m.x21 * m.x32) * d;
        var x32 = (m.x02 * m.x11 * m.x30 - m.x01 * m.x12 * m.x30 - m.x02 * m.x10 * m.x31 + m.x00 * m.x12 * m.x31 + m.x01 * m.x10 * m.x32 - m.x00 * m.x11 * m.x32) * d;
        var x33 = (m.x01 * m.x12 * m.x20 - m.x02 * m.x11 * m.x20 + m.x02 * m.x10 * m.x21 - m.x00 * m.x12 * m.x21 - m.x01 * m.x10 * m.x22 + m.x00 * m.x11 * m.x22) * d;
        return new Matrix(x00, x01, x02, x03, x10, x11, x12, x13, x20, x21, x22, x23, x30, x31, x32, x33);
    }
}
