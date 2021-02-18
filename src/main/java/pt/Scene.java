package pt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static pt.Color.BLACK;

public class Scene {

    public Color color = BLACK;
    public Texture texture;
    public double textureAngle;
    public final List<Shape> shapes = new ArrayList<>();
    public final List<Shape> lights = new ArrayList<>();
    private Accelerator tree;
    private final AtomicLong rays = new AtomicLong();

    public void compile() {
        shapes.forEach(Shape::compile);
        if (tree == null) {
            tree = new Bvh(shapes);
        }
    }

    public void add(Shape shape) {
        shapes.add(shape);
        if (shape.materialAt(Vector.ZERO).emittance > 0.0) {
            lights.add(shape);
        }
        tree = null;
    }

    public void resetRayCount() {
        rays.set(0L);
    }

    public long rayCount() {
        return rays.longValue();
    }

    public Hit intersect(Ray r) {
        rays.incrementAndGet();
        return tree.intersect(r);
    }
}
