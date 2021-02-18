package pt;

import static pt.Common.EPS;
import static pt.Hit.NO_HIT;

public class Cube implements Shape {

    private final Vector min;
    private final Vector max;
    private final Material material;
    private final Box box;

    public Cube(Vector min, Vector max, Material material) {
        this.min = min;
        this.max = max;
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
        var tn = min.sub(r.origin).div(r.direction);
        var tf = max.sub(r.origin).div(r.direction);
        var n = tn.min(tf);
        var f = tn.max(tf);
        var t0 = Math.max(Math.max(n.x, n.y), n.z);
        var t1 = Math.min(Math.min(f.x, f.y), f.z);
        if (t0 > 0.0 && t0 < t1) {
            return new Hit(this, t0, null);
        }
        return NO_HIT;
    }

    @Override
    public Vector uv(Vector p) {
        p = p.sub(min).div(max.sub(min));
        return new Vector(p.x, p.z, 0.0);
    }

    @Override
    public Material materialAt(Vector p) {
        return material;
    }

    @Override
    public Vector normalAt(Vector p) {
        if (p.x < min.x + EPS) {
            return Vector.AXIS_NX;
        } else if (p.x > max.x - EPS) {
            return Vector.AXIS_X;
        } else if (p.y < min.y + EPS) {
            return Vector.AXIS_NY;
        } else if (p.y > max.y - EPS) {
            return Vector.AXIS_Y;
        } else if (p.z < min.z + EPS) {
            return Vector.AXIS_NZ;
        } else if (p.z > max.z - EPS) {
            return Vector.AXIS_Z;
        } else {
            return Vector.AXIS_Y;
        }
    }

    public Mesh mesh() {
        var v000 = new Vector(min.x, min.y, min.z);
        var v001 = new Vector(min.x, min.y, max.z);
        var v010 = new Vector(min.x, max.y, min.z);
        var v011 = new Vector(min.x, max.y, max.z);
        var v100 = new Vector(max.x, min.y, min.z);
        var v101 = new Vector(max.x, min.y, max.z);
        var v110 = new Vector(max.x, max.y, min.z);
        var v111 = new Vector(max.x, max.y, max.z);
        return new Mesh(new Triangle[]{
            new Triangle(v000, v100, v110, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v000, v110, v010, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v001, v101, v111, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v001, v111, v011, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v000, v100, v101, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v000, v101, v001, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v010, v110, v111, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v010, v111, v011, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v000, v010, v011, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v000, v011, v001, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v100, v110, v111, Vector.ZERO, Vector.ZERO, Vector.ZERO, material),
            new Triangle(v100, v111, v101, Vector.ZERO, Vector.ZERO, Vector.ZERO, material)});
    }
}
