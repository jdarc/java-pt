package pt;

public class RepeatSDF implements SDF {

    private final SDF sdf;
    private final Vector step;

    public RepeatSDF(SDF sdf, Vector step) {
        this.sdf = sdf;
        this.step = step;
    }

    public double evaluate(Vector p) {
        var q = p.mod(step).sub(step.divScalar(2.0));
        return sdf.evaluate(q);
    }

    public Box boundingBox() {
        return new Box(Vector.ZERO, Vector.ZERO);
    }
}
