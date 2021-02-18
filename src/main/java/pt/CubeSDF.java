package pt;

public class CubeSDF implements SDF {

    private final Vector size;

    public CubeSDF(Vector size) {
        this.size = size;
    }

    public double evaluate(Vector p) {
        var x = p.x;
        var y = p.y;
        var z = p.z;
        if (x < 0.0) {
            x = -x;
        }
        if (y < 0.0) {
            y = -y;
        }
        if (z < 0.0) {
            z = -z;
        }
        x -= size.x / 2.0;
        y -= size.y / 2.0;
        z -= size.z / 2.0;
        var a = x;
        if (y > a) {
            a = y;
        }
        if (z > a) {
            a = z;
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
        if (z < 0.0) {
            z = 0.0;
        }
        var b = Math.sqrt(x * x + y * y + z * z);
        return a + b;
    }

    public Box boundingBox() {
        var x = size.x / 2.0;
        var y = size.y / 2.0;
        var z = size.z / 2.0;
        return new Box(new Vector(-x, -y, -z), new Vector(x, y, z));
    }
}
