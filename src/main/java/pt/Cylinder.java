package pt;

import static pt.Common.EPS;
import static pt.Hit.NO_HIT;

public class Cylinder implements Shape {

    private final double radius;
    private final double z0;
    private final double z1;
    private final Material material;

    public Cylinder(double radius, double z0, double z1, Material material) {
        this.radius = radius;
        this.z0 = z0;
        this.z1 = z1;
        this.material = material;
    }

    @Override
    public void compile() {
    }

    @Override
    public Box boundingBox() {
        var r = radius;
        return new Box(new Vector(-r, -r, z0), new Vector(r, r, z1));
    }

    @Override
    public Hit intersect(Ray ray) {
        var o = ray.origin;
        var d = ray.direction;
        var a = d.x * d.x + d.y * d.y;
        var b = 2.0 * o.x * d.x + 2.0 * o.y * d.y;
        var c = o.x * o.x + o.y * o.y - radius * radius;
        var q = b * b - 4.0 * a * c;
        if (q < EPS) {
            return NO_HIT;
        }
        var s = Math.sqrt(q);
        var t0 = (-b + s) / (2.0 * a);
        var t1 = (-b - s) / (2.0 * a);
        if (t0 > t1) {
            var temp = t0;
            t0 = t1;
            t1 = temp;
        }
        var z0 = o.z + t0 * d.z;
        var z1 = o.z + t1 * d.z;
        if (t0 > EPS && this.z0 < z0 && z0 < this.z1) {
            return new Hit(this, t0, null);
        }
        if (t1 > EPS && this.z0 < z1 && z1 < this.z1) {
            return new Hit(this, t1, null);
        }
        return NO_HIT;
    }

    @Override
    public Vector uv(Vector p) {
        return Vector.ZERO;
    }

    @Override
    public Vector normalAt(Vector p) {
        return new Vector(p.x, p.y, 0.0).normalize();
    }

    @Override
    public Material materialAt(Vector p) {
        return material;
    }
}
