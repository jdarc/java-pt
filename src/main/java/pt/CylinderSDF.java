package pt;

public class CylinderSDF implements SDF {

    private final double radius;
    private final double height;

    public CylinderSDF(double radius, double height) {
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double evaluate(Vector p) {
        var x = Math.sqrt(p.x * p.x + p.z * p.z);
        var y = p.y;
        if (x < 0.0) {
            x = -x;
        }
        if (y < 0.0) {
            y = -y;
        }
        x -= radius;
        y -= height / 2.0;
        var a = x;
        if (y > a) {
            a = y;
        }
        if (a > 0.0) {
            a = 0.0;
        }
        if (x < 0.0) {
            x = 0.0;
        }
        if (y < 0.0) {
            y = 0.0;
        }
        var b = Math.sqrt(x * x + y * y);
        return a + b;
    }

    @Override
    public Box boundingBox() {
        var h = height / 2.0;
        return new Box(new Vector(-radius, -h, -radius), new Vector(radius, h, radius));
    }
}
