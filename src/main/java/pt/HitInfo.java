package pt;

public class HitInfo {

    public final Ray ray;
    public final Material material;
    public final boolean inside;

    public HitInfo(Ray ray, Material material, boolean inside) {
        this.ray = ray;
        this.material = material;
        this.inside = inside;
    }
}
