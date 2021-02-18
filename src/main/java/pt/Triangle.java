package pt;

import static pt.Common.EPS;
import static pt.Hit.NO_HIT;

public class Triangle implements Shape {

    public Material material;
    public Vector v1 = Vector.ZERO;
    public Vector v2 = Vector.ZERO;
    public Vector v3 = Vector.ZERO;
    public Vector n1 = Vector.ZERO;
    public Vector n2 = Vector.ZERO;
    public Vector n3 = Vector.ZERO;
    public Vector t1 = Vector.ZERO;
    public Vector t2 = Vector.ZERO;
    public Vector t3 = Vector.ZERO;

    public Triangle() {
    }

    public Triangle(Vector v1, Vector v2, Vector v3, Vector t1, Vector t2, Vector t3, Material material) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.material = material;
        fixNormals();
    }

    public Vector[] vertices() {
        return new Vector[]{v1, v2, v3};
    }

    public void compile() {
    }

    public Box boundingBox() {
        var min = v1.min(v2).min(v3);
        var max = v1.max(v2).max(v3);
        return new Box(min, max);
    }

    @Override
    public Hit intersect(Ray r) {
        var e1x = v2.x - v1.x;
        var e1y = v2.y - v1.y;
        var e1z = v2.z - v1.z;
        var e2x = v3.x - v1.x;
        var e2y = v3.y - v1.y;
        var e2z = v3.z - v1.z;
        var px = r.direction.y * e2z - r.direction.z * e2y;
        var py = r.direction.z * e2x - r.direction.x * e2z;
        var pz = r.direction.x * e2y - r.direction.y * e2x;
        var det = e1x * px + e1y * py + e1z * pz;
        if (det > -EPS && det < EPS) {
            return NO_HIT;
        }
        var inv = 1.0 / det;
        var tx = r.origin.x - v1.x;
        var ty = r.origin.y - v1.y;
        var tz = r.origin.z - v1.z;
        var u = (tx * px + ty * py + tz * pz) * inv;
        if (u < 0.0 || u > 1.0) {
            return NO_HIT;
        }
        var qx = ty * e1z - tz * e1y;
        var qy = tz * e1x - tx * e1z;
        var qz = tx * e1y - ty * e1x;
        var v = (r.direction.x * qx + r.direction.y * qy + r.direction.z * qz) * inv;
        if (v < 0.0 || u + v > 1.0) {
            return NO_HIT;
        }
        var d = (e2x * qx + e2y * qy + e2z * qz) * inv;
        return d < EPS ? NO_HIT : new Hit(this, d, null);
    }

    @Override
    public Vector uv(Vector p) {
        var barycentric = barycentric(p);
        var u = barycentric[0];
        var v = barycentric[1];
        var w = barycentric[2];
        var n = Vector.ZERO;
        n = n.add(t1.mulScalar(u));
        n = n.add(t2.mulScalar(v));
        n = n.add(t3.mulScalar(w));
        return new Vector(n.x, n.y, 0.0);
    }

    public Vector normalAt(Vector p) {
        var t = this;
        var barycentric = t.barycentric(p);
        var u = barycentric[0];
        var v = barycentric[1];
        var w = barycentric[2];

        var n = Vector.ZERO;
        n = n.add(t.n1.mulScalar(u));
        n = n.add(t.n2.mulScalar(v));
        n = n.add(t.n3.mulScalar(w));
        n = n.normalize();
        if (t.material.normalTexture != null) {
            var b = Vector.ZERO;
            b = b.add(t.t1.mulScalar(u));
            b = b.add(t.t2.mulScalar(v));
            b = b.add(t.t3.mulScalar(w));
            var ns = t.material.normalTexture.normalSample(b.x, b.y);
            var dv1 = t.v2.sub(t.v1);
            var dv2 = t.v3.sub(t.v1);
            var dt1 = t.t2.sub(t.t1);
            var dt2 = t.t3.sub(t.t1);
            var T = dv1.mulScalar(dt2.y).sub(dv2.mulScalar(dt1.y)).normalize();
            var B = dv2.mulScalar(dt1.x).sub(dv1.mulScalar(dt2.x)).normalize();
            var N = T.cross(B);
            var matrix = new Matrix(T.x, B.x, N.x, 0.0,
                                    T.y, B.y, N.y, 0.0,
                                    T.z, B.z, N.z, 0.0,
                                    0.0, 0.0, 0.0, 1.0);
            n = matrix.mulDirection(ns);
        }
        if (t.material.bumpTexture != null) {
            var b = Vector.ZERO;
            b = b.add(t.t1.mulScalar(u));
            b = b.add(t.t2.mulScalar(v));
            b = b.add(t.t3.mulScalar(w));
            var bump = t.material.bumpTexture.bumpSample(b.x, b.y);
            var dv1 = t.v2.sub(t.v1);
            var dv2 = t.v3.sub(t.v1);
            var dt1 = t.t2.sub(t.t1);
            var dt2 = t.t3.sub(t.t1);
            var tangent = dv1.mulScalar(dt2.y).sub(dv2.mulScalar(dt1.y)).normalize();
            var bitangent = dv2.mulScalar(dt1.x).sub(dv1.mulScalar(dt2.x)).normalize();
            n = n.add(tangent.mulScalar(bump.x * t.material.bumpMultiplier));
            n = n.add(bitangent.mulScalar(bump.y * t.material.bumpMultiplier));
        }
        return n.normalize();
    }

    @Override
    public Material materialAt(Vector p) {
        return material;
    }

    public double area() {
        var e1 = v2.sub(v1);
        var e2 = v3.sub(v1);
        return e1.cross(e2).length() / 2.0;
    }

    public Vector normal() {
        var e1 = v2.sub(v1);
        var e2 = v3.sub(v1);
        return e1.cross(e2).normalize();
    }

    public double[] barycentric(Vector p) {
        var v0 = v2.sub(v1);
        var v1 = v3.sub(this.v1);
        var v2 = p.sub(this.v1);
        var d00 = v0.dot(v0);
        var d01 = v0.dot(v1);
        var d11 = v1.dot(v1);
        var d20 = v2.dot(v0);
        var d21 = v2.dot(v1);
        var d = d00 * d11 - d01 * d01;
        var v = (d11 * d20 - d01 * d21) / d;
        var w = (d00 * d21 - d01 * d20) / d;
        var u = 1.0 - v - w;
        return new double[]{u, v, w};
    }

    public void fixNormals() {
        var n = normal();
        if (n1.equals(Vector.ZERO)) {
            n1 = n;
        }
        if (n2.equals(Vector.ZERO)) {
            n2 = n;
        }
        if (n3.equals(Vector.ZERO)) {
            n3 = n;
        }
    }

    public Triangle dup() {
        var t = new Triangle();
        t.material = material;
        t.v1 = v1;
        t.v2 = v2;
        t.v3 = v3;
        t.n1 = n1;
        t.n2 = n2;
        t.n3 = n3;
        t.t1 = t1;
        t.t2 = t2;
        t.t3 = t3;
        return t;
    }
}
