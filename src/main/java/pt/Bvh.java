package pt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class Bvh implements Accelerator {

    private final BvhNode node;

    public Bvh(List<Shape> shapes) {
        var t1 = System.nanoTime();
        out.printf("Building BVH (%d shapes)... ", shapes.size());
        node = new BvhNode(new ArrayList<>(shapes));
        node.split(0);
        out.printf("done in %s second(s)", TimeUnit.MILLISECONDS.convert(System.nanoTime() - t1, TimeUnit.NANOSECONDS) / 1000.0);
        out.println(".");
    }

    public Hit intersect(Ray r) {
        return node.intersect(r);
    }
}
