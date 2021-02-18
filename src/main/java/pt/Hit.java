package pt;

public class Hit {

    public static final Hit NO_HIT = new Hit(null, Double.POSITIVE_INFINITY, null);

    public final Shape shape;
    public final double t;
    public final HitInfo hitInfo;

    public Hit(Shape shape, double t, HitInfo info) {
        this.shape = shape;
        this.t = t;
        hitInfo = info;
    }

    public boolean ok() {
        return t < Common.INF;
    }

    public HitInfo info(Ray r) {
        if (hitInfo != null) {
            return hitInfo;
        }
        var position = r.position(t);
        var normal = shape.normalAt(position);
        var material = Material.materialAt(shape, position);
        var inside = false;
        if (normal.dot(r.direction) > 0.0) {
            normal = normal.negate();
            inside = !(shape instanceof Volume || shape instanceof SDFShape);
        }
        return new HitInfo(new Ray(position, normal), material, inside);
    }
}
