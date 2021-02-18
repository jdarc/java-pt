package pt;

public class ScaleSDF implements SDF {

    private final SDF sdf;
    private final double factor;

    public ScaleSDF(SDF sdf, double factor) {
        this.sdf = sdf;
        this.factor = factor;
    }

    public double evaluate(Vector p) {
        return sdf.evaluate(p.divScalar(factor)) * factor;
    }

    public Box boundingBox() {
        var f = factor;
        var m = MatrixFactory.scaling(new Vector(f, f, f));
        return m.mulBox(sdf.boundingBox());
    }
}
