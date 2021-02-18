package pt;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Vector {

    public static final Vector ZERO = new Vector(0.0, 0.0, 0.0);
    public static final Vector AXIS_X = new Vector(1.0, 0.0, 0.0);
    public static final Vector AXIS_Y = new Vector(0.0, 1.0, 0.0);
    public static final Vector AXIS_Z = new Vector(0.0, 0.0, 1.0);
    public static final Vector AXIS_NX = new Vector(-1.0, 0.0, 0.0);
    public static final Vector AXIS_NY = new Vector(0.0, -1.0, 0.0);
    public static final Vector AXIS_NZ = new Vector(0.0, 0.0, -1.0);

    public final double x;
    public final double y;
    public final double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vector v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public static Vector randomUnitVector() {
        var rnd = ThreadLocalRandom.current();
        while (true) {
            var x = rnd.nextDouble() * 2.0 - 1.0;
            var y = rnd.nextDouble() * 2.0 - 1.0;
            var z = rnd.nextDouble() * 2.0 - 1.0;
            if (x * x + y * y + z * z > 1.0) {
                continue;
            }
            return new Vector(x, y, z).normalize();
        }
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthN(double n) {
        if (n == 2.0) {
            return length();
        }
        var a = abs();
        return StrictMath.pow(StrictMath.pow(a.x, n) + StrictMath.pow(a.y, n) + StrictMath.pow(a.z, n), 1.0 / n);
    }

    public double dot(Vector b) {
        return x * b.x + y * b.y + z * b.z;
    }

    public Vector cross(Vector b) {
        var nx = y * b.z - z * b.y;
        var ny = z * b.x - x * b.z;
        var nz = x * b.y - y * b.x;
        return new Vector(nx, ny, nz);
    }

    public Vector normalize() {
        var d = length();
        return new Vector(x / d, y / d, z / d);
    }

    public Vector negate() {
        return new Vector(-x, -y, -z);
    }

    public Vector abs() {
        return new Vector(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Vector add(Vector b) {
        return new Vector(x + b.x, y + b.y, z + b.z);
    }

    public Vector sub(Vector b) {
        return new Vector(x - b.x, y - b.y, z - b.z);
    }

    public Vector mul(Vector b) {
        return new Vector(x * b.x, y * b.y, z * b.z);
    }

    public Vector div(Vector b) {
        return new Vector(x / b.x, y / b.y, z / b.z);
    }

    public Vector mod(Vector b) {
        var nx = x - b.x * Math.floor(x / b.x);
        var ny = y - b.y * Math.floor(y / b.y);
        var nz = z - b.z * Math.floor(z / b.z);
        return new Vector(nx, ny, nz);
    }

    public Vector addScalar(double b) {
        return new Vector(x + b, y + b, z + b);
    }

    public Vector subScalar(double b) {
        return new Vector(x - b, y - b, z - b);
    }

    public Vector mulScalar(double b) {
        return new Vector(x * b, y * b, z * b);
    }

    public Vector divScalar(double b) {
        return new Vector(x / b, y / b, z / b);
    }

    public Vector min(Vector b) {
        return new Vector(Math.min(x, b.x), Math.min(y, b.y), Math.min(z, b.z));
    }

    public Vector max(Vector b) {
        return new Vector(Math.max(x, b.x), Math.max(y, b.y), Math.max(z, b.z));
    }

    public Vector minAxis() {
        var x = Math.abs(this.x);
        var y = Math.abs(this.y);
        var z = Math.abs(this.z);
        if (x <= y && x <= z) {
            return AXIS_X;
        }
        if (y <= x && y <= z) {
            return AXIS_Y;
        }
        return AXIS_Z;
    }

    public double minComponent() {
        return Math.min(Math.min(x, y), z);
    }

    public double maxComponent() {
        return Math.max(Math.max(x, y), z);
    }

    public Vector reflect(Vector i) {
        return i.sub(mulScalar(2.0 * dot(i)));
    }

    public Vector refract(Vector i, double n1, double n2) {
        var nr = n1 / n2;
        var cosI = -dot(i);
        var sinT2 = nr * nr * (1.0 - cosI * cosI);
        if (sinT2 > 1.0) {
            return ZERO;
        }
        var cosT = Math.sqrt(1.0 - sinT2);
        return i.mulScalar(nr).add(mulScalar(nr * cosI - cosT));
    }

    public double reflectance(Vector i, double n1, double n2) {
        var nr = n1 / n2;
        var cosI = -dot(i);
        var sinT2 = nr * nr * (1.0 - cosI * cosI);
        if (sinT2 > 1.0) {
            return 1.0;
        }
        var cosT = Math.sqrt(1.0 - sinT2);
        var rOrth = (n1 * cosI - n2 * cosT) / (n1 * cosI + n2 * cosT);
        var rPar = (n2 * cosI - n1 * cosT) / (n2 * cosI + n1 * cosT);
        return (rOrth * rOrth + rPar * rPar) / 2.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var vector = (Vector)o;
        return Double.compare(vector.x, x) == 0 &&
               Double.compare(vector.y, y) == 0 &&
               Double.compare(vector.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
