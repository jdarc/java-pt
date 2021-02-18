package pt;

public class BounceResult {

    public final Ray ray;
    public final boolean reflect;
    public final double p;

    public BounceResult(Ray ray, boolean reflect, double p) {
        this.ray = ray;
        this.reflect = reflect;
        this.p = p;
    }
}
