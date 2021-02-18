package pt;

public class SphereSDF implements SDF {

    private final double radius;
    private final double exponent;

    public SphereSDF(double radius) {
        this.radius = radius;
        exponent = 2.0;
    }

    public double evaluate(Vector p) {
        return p.lengthN(exponent) - radius;
    }

    public Box boundingBox() {
        var r = radius;
        return new Box(new Vector(-r, -r, -r), new Vector(r, r, r));
    }
}
