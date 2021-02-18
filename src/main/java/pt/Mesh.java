package pt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Mesh implements Shape {

    public Triangle[] triangles;
    private Box box;
    private Accelerator tree;

    public Mesh(Triangle[] triangles) {
        this.triangles = triangles;
        box = null;
        tree = null;
    }

    public void dirty() {
        box = null;
        tree = null;
    }

    @Override
    public void compile() {
        if (tree == null) {
//            tree = new Tree(Arrays.asList(triangles));
            tree = new Bvh(Arrays.asList(triangles));
        }
    }

    public void add(Mesh m) {
        triangles = Stream.concat(Arrays.stream(triangles), Arrays.stream(m.triangles)).toArray(Triangle[]::new);
        dirty();
    }

    @Override
    public Box boundingBox() {
        if (box == null) {
            var min = triangles[0].v1;
            var max = triangles[0].v1;
            for (var t : triangles) {
                min = min.min(t.v1).min(t.v2).min(t.v3);
                max = max.max(t.v1).max(t.v2).max(t.v3);
            }
            box = new Box(min, max);
        }
        return box;
    }

    @Override
    public Hit intersect(Ray r) {
        return tree.intersect(r);
    }

    @Override
    public Vector uv(Vector v) {
        return Vector.ZERO;
    }

    @Override
    public Material materialAt(Vector v) {
        return Material.DEFAULT;
    }

    @Override
    public Vector normalAt(Vector v) {
        return Vector.ZERO;
    }

    public Vector smoothNormalsThreshold(Vector normal, List<Vector> normals, double threshold) {
        var result = Vector.ZERO;
        for (var n : normals) {
            if (n.dot(normal) >= threshold) {
                result = result.add(n);
            }
        }
        return result.normalize();
    }

    public void smoothNormalsThreshold(double radians) {
        var threshold = StrictMath.cos(radians);
        var lookup = new HashMap<Vector, List<Vector>>();
        for (var t : triangles) {
            lookup.putIfAbsent(t.v1, new ArrayList<>());
            lookup.putIfAbsent(t.v2, new ArrayList<>());
            lookup.putIfAbsent(t.v3, new ArrayList<>());
        }
        for (var t : triangles) {
            lookup.get(t.v1).add(t.n1);
            lookup.get(t.v2).add(t.n2);
            lookup.get(t.v3).add(t.n3);
        }
        for (var t : triangles) {
            t.n1 = smoothNormalsThreshold(t.n1, lookup.get(t.v1), threshold);
            t.n2 = smoothNormalsThreshold(t.n2, lookup.get(t.v2), threshold);
            t.n3 = smoothNormalsThreshold(t.n3, lookup.get(t.v3), threshold);
        }
    }

    public void smoothNormals() {
        var lookup = new HashMap<Vector, Vector>();

        for (var t : triangles) {
            lookup.put(t.v1, lookup.getOrDefault(t.v1, Vector.ZERO).add(t.n1));
            lookup.put(t.v2, lookup.getOrDefault(t.v2, Vector.ZERO).add(t.n2));
            lookup.put(t.v3, lookup.getOrDefault(t.v3, Vector.ZERO).add(t.n3));
        }

        lookup.replaceAll((v, value) -> value.normalize());

        for (var t : triangles) {
            t.n1 = lookup.get(t.v1);
            t.n2 = lookup.get(t.v2);
            t.n3 = lookup.get(t.v3);
        }
    }

    public void unitCube() {
        fitInside(new Box(Vector.ZERO, new Vector(1.0, 1.0, 1.0)), Vector.ZERO);
        moveTo(Vector.ZERO, new Vector(0.5, 0.5, 0.5));
    }

    public void moveTo(Vector position, Vector anchor) {
        transform(MatrixFactory.translation(position.sub(boundingBox().anchor(anchor))));
    }

    public void fitInside(Box box, Vector anchor) {
        var scale = box.size().div(boundingBox().size()).minComponent();
        var extra = box.size().sub(boundingBox().size().mulScalar(scale));
        var matrix = MatrixFactory.identity();
        matrix = matrix.translate(boundingBox().min.negate());
        matrix = matrix.scale(new Vector(scale, scale, scale));
        matrix = matrix.translate(box.min.add(extra.mul(anchor)));
        transform(matrix);
    }

    public void transform(Matrix m) {
        for (var t : triangles) {
            t.v1 = m.mulPosition(t.v1);
            t.v2 = m.mulPosition(t.v2);
            t.v3 = m.mulPosition(t.v3);
            t.n1 = m.mulDirection(t.n1);
            t.n2 = m.mulDirection(t.n2);
            t.n3 = m.mulDirection(t.n3);
        }
        dirty();
    }

    public void setMaterial(Material m) {
        for (var t : triangles) {
            t.material = m;
        }
    }
}
