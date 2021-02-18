package pt;

public class TransformSDF implements SDF {

    private final SDF sdf;
    private final Matrix matrix;
    private final Matrix inverse;

    public TransformSDF(SDF sdf, Matrix matrix) {
        this.sdf = sdf;
        this.matrix = matrix;
        inverse = matrix.inverse();
    }

    public double evaluate(Vector p) {
        return sdf.evaluate(inverse.mulPosition(p));
    }

    public Box boundingBox() {
        return matrix.mulBox(sdf.boundingBox());
    }
}
