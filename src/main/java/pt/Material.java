package pt;

import static pt.Color.BLACK;

public class Material {

    public static final Material DEFAULT = diffuseMaterial(BLACK);

    public Color color;
    public Texture texture;
    public Texture normalTexture;
    public final Texture bumpTexture;
    public final Texture glossTexture;
    public final double bumpMultiplier;
    public double emittance;
    public final double index; // refractive index
    public double gloss; // reflection cone angle in radians
    public final double tint; // specular and refractive tinting
    public final double reflectivity; // metallic reflection
    public final boolean transparent;

    public Material(Color color, Texture texture, Texture normalTexture, Texture bumpTexture, Texture glossTexture,
                    double bumpMultiplier, double emittance, double index, double gloss, double tint, double reflectivity, boolean transparent) {
        this.color = color;
        this.texture = texture;
        this.normalTexture = normalTexture;
        this.bumpTexture = bumpTexture;
        this.glossTexture = glossTexture;
        this.bumpMultiplier = bumpMultiplier;
        this.emittance = emittance;
        this.index = index;
        this.gloss = gloss;
        this.tint = tint;
        this.reflectivity = reflectivity;
        this.transparent = transparent;
    }

    public Material(Material parent) {
        color = parent.color;
        texture = parent.texture;
        normalTexture = parent.normalTexture;
        bumpTexture = parent.bumpTexture;
        glossTexture = parent.glossTexture;
        bumpMultiplier = parent.bumpMultiplier;
        emittance = parent.emittance;
        index = parent.index;
        gloss = parent.gloss;
        tint = parent.tint;
        reflectivity = parent.reflectivity;
        transparent = parent.transparent;
    }

    public static Material diffuseMaterial(Color color) {
        return new Material(color, null, null, null, null, 1.0, 0.0, 1.0, 0.0, 0.0, -1.0, false);
    }

    public static Material specularMaterial(Color color, double index) {
        return new Material(color, null, null, null, null, 1.0, 0.0, index, 0.0, 0.0, -1.0, false);
    }

    public static Material glossyMaterial(Color color, double index, double gloss) {
        return new Material(color, null, null, null, null, 1.0, 0.0, index, gloss, 0.0, -1.0, false);
    }

    public static Material clearMaterial(double index, double gloss) {
        return new Material(BLACK, null, null, null, null, 1.0, 0.0, index, gloss, 0.0, -1.0, true);
    }

    public static Material transparentMaterial(Color color, double index, double gloss, double tint) {
        return new Material(color, null, null, null, null, 1.0, 0.0, index, gloss, tint, -1.0, true);
    }

    public static Material metallicMaterial(Color color, double gloss, double tint) {
        return new Material(color, null, null, null, null, 1.0, 0.0, 1.0, gloss, tint, 1.0, false);
    }

    public static Material lightMaterial(Color color, double emittance) {
        return new Material(color, null, null, null, null, 1.0, emittance, 1.0, 0.0, 0.0, -1.0, false);
    }

    public static Material materialAt(Shape shape, Vector point) {
        var material = shape.materialAt(point);
        if (material.texture != null || material.glossTexture != null) {
            var uv = shape.uv(point);
            material = new Material(material);
            if (material.texture != null) {
                material.color = material.texture.sample(uv.x, uv.y);
            }
            if (material.glossTexture != null) {
                var c = material.glossTexture.sample(uv.x, uv.y);
                material.gloss = (c.r + c.g + c.b) / 3.0;
            }
        }
        return material;
    }
}
