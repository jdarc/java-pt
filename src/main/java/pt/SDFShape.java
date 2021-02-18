package pt;

import static pt.Hit.NO_HIT;

public class SDFShape implements Shape, SDF {

    private final SDF sdf;
    private final Material material;

    public SDFShape(SDF sdf, Material material) {
        this.sdf = sdf;
        this.material = material;
    }

    @Override
    public void compile() {
    }

    @Override
    public Hit intersect(Ray ray) {
        var s = this;
        var epsilon = 0.00001;
        var start = 0.0001;
        var jumpSize = 0.001;
        var box = s.boundingBox();
        var intersect = box.intersect(ray);
        var t1 = intersect[0];
        var t2 = intersect[1];
        if (t2 < t1 || t2 < 0.0) {
            return NO_HIT;
        }
        var t = Math.max(start, t1);
        var jump = true;
        for (var i = 0; i < 1000; i++) {
            var d = s.evaluate(ray.position(t));
            if (jump && d < 0.0) {
                t -= jumpSize;
                jump = false;
                continue;
            }
            if (d < epsilon) {
                return new Hit(s, t, null);
            }
            if (jump && d < jumpSize) {
                d = jumpSize;
            }
            t += d;
            if (t > t2) {
                return NO_HIT;
            }
        }
        return NO_HIT;
    }

    @Override
    public Vector uv(Vector v) {
        return Vector.ZERO;
    }

    @Override
    public Vector normalAt(Vector p) {
        var e = 0.0001;
        var x = p.x;
        var y = p.y;
        var z = p.z;
        var n = new Vector(
            evaluate(new Vector(x - e, y, z)) - evaluate(new Vector(x + e, y, z)),
            evaluate(new Vector(x, y - e, z)) - evaluate(new Vector(x, y + e, z)),
            evaluate(new Vector(x, y, z - e)) - evaluate(new Vector(x, y, z + e))
        );
        return n.normalize();
    }

    @Override
    public Material materialAt(Vector v) {
        return material;
    }

    @Override
    public double evaluate(Vector p) {
        return sdf.evaluate(p);
    }

    @Override
    public Box boundingBox() {
        return sdf.boundingBox();
    }
}

