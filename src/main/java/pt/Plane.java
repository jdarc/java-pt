package pt;

import static pt.Common.EPS;
import static pt.Hit.NO_HIT;

public class Plane implements Shape {

    private final Vector point;
    private final Vector normal;
    private final Material material;

    public Plane(Vector point, Vector normal, Material material) {
        this.point = point;
        this.normal = normal.normalize();
        this.material = material;
    }

    @Override
    public void compile() {
    }

    @Override
    public Box boundingBox() {
        return new Box(new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
                       new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Override
    public Hit intersect(Ray ray) {
        var d = normal.dot(ray.direction);
        if (Math.abs(d) < EPS) {
            return NO_HIT;
        }
        var t = point.sub(ray.origin).dot(normal) / d;
        if (t < EPS) {
            return NO_HIT;
        }
        return new Hit(this, t, null);
    }

    @Override
    public Vector uv(Vector a) {
        return Vector.ZERO;
    }

    @Override
    public Material materialAt(Vector a) {
        return material;
    }

    @Override
    public Vector normalAt(Vector a) {
        return normal;
    }
}
