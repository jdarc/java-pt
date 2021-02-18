package pt;

public class TorusSDF implements SDF {

    private final double majorRadius;
    private final double minorRadius;
    private final double majorExponent;
    private final double minorExponent;

    public TorusSDF(double major, double minor) {
        majorRadius = major;
        minorRadius = minor;
        majorExponent = 2.0;
        minorExponent = 2.0;
    }

    public double evaluate(Vector p) {
        var q = new Vector(new Vector(p.x, p.y, 0.0).lengthN(majorExponent) - majorRadius, p.z, 0.0);
        return q.lengthN(minorExponent) - minorRadius;
    }

    public Box boundingBox() {
        var b = minorRadius + majorRadius;
        return new Box(new Vector(-b, -b, -minorRadius), new Vector(b, b, minorRadius));
    }
}
