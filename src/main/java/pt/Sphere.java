package pt;

import static pt.Common.EPS;
import static pt.Hit.NO_HIT;

public class Sphere implements Shape {

    public final Vector center;
    public final double radius;
    private final Material material;
    private final Box box;

    public Sphere(Vector center, double radius, Material material) {
        var min = new Vector(center.x - radius, center.y - radius, center.z - radius);
        var max = new Vector(center.x + radius, center.y + radius, center.z + radius);
        this.center = center;
        this.radius = radius;
        this.material = material;
        box = new Box(min, max);
    }

    @Override
    public void compile() {
    }

    @Override
    public Box boundingBox() {
        return box;
    }

    @Override
    public Hit intersect(Ray r) {
        var to = r.origin.sub(center);
        var b = to.dot(r.direction);
        var c = to.dot(to) - radius * radius;
        var d = b * b - c;
        if (d > 0.0) {
            d = Math.sqrt(d);
            var t1 = -b - d;
            if (t1 > EPS) {
                return new Hit(this, t1, null);
            }
            var t2 = -b + d;
            if (t2 > EPS) {
                return new Hit(this, t2, null);
            }
        }
        return NO_HIT;
    }

    @Override
    public Vector uv(Vector p) {
        p = p.sub(center);
        var u = StrictMath.atan2(p.z, p.x);
        var v = StrictMath.atan2(p.y, new Vector(p.x, 0.0, p.z).length());
        u = 1.0 - (u + Math.PI) / (2.0 * Math.PI);
        v = (v + Math.PI / 2.0) / Math.PI;
        return new Vector(u, v, 0.0);
    }

    @Override
    public Material materialAt(Vector p) {
        return material;
    }

    @Override
    public Vector normalAt(Vector p) {
        return p.sub(center).normalize();
    }
}
