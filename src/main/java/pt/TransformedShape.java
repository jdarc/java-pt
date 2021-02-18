package pt;

public class TransformedShape implements Shape {

    private final Shape shape;
    private final Matrix matrix;
    private final Matrix inverse;

    public TransformedShape(Shape shape, Matrix matrix) {
        this.shape = shape;
        this.matrix = matrix;
        inverse = matrix.inverse();
    }

    @Override
    public void compile() {
        shape.compile();
    }

    @Override
    public Box boundingBox() {
        return matrix.mulBox(shape.boundingBox());
    }

    @Override
    public Hit intersect(Ray r) {
        var shapeRay = inverse.mulRay(r);
        var hit = shape.intersect(shapeRay);
        if (!hit.ok()) {
            return hit;
        }
        var shape = hit.shape;
        var shapePosition = shapeRay.position(hit.t);
        var shapeNormal = shape.normalAt(shapePosition);
        var position = matrix.mulPosition(shapePosition);
        var normal = inverse.transpose().mulDirection(shapeNormal);
        var material = Material.materialAt(shape, shapePosition);
        var inside = false;
        if (shapeNormal.dot(shapeRay.direction) > 0.0) {
            normal = normal.negate();
            inside = true;
        }
        var ray = new Ray(position, normal);
        var info = new HitInfo(ray, material, inside);
        return new Hit(hit.shape, position.sub(r.origin).length(), info);
    }

    @Override
    public Vector uv(Vector v) {
        return shape.uv(v);
    }

    @Override
    public Vector normalAt(Vector v) {
        return shape.normalAt(v);
    }

    @Override
    public Material materialAt(Vector v) {
        return shape.materialAt(v);
    }
}
