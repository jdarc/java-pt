package pt;

public class CapsuleSDF implements SDF {

    public final Vector a;
    public final Vector b;
    public final double radius;
    public final double exponent;

    public CapsuleSDF(Vector a, Vector b, double radius) {
        this.a = a;
        this.b = b;
        this.radius = radius;
        exponent = 2.0;
    }

    public double evaluate(Vector p) {
        var pa = p.sub(a);
        var ba = b.sub(a);
        var h = Math.max(0.0, Math.min(1.0, pa.dot(ba) / ba.dot(ba)));
        return pa.sub(ba.mulScalar(h)).lengthN(exponent) - radius;
    }

    public Box boundingBox() {
        var na = a.min(b);
        var nb = a.max(b);
        return new Box(na.subScalar(radius), nb.addScalar(radius));
    }
}
