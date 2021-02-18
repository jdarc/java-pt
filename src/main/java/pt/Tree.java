package pt;

import java.util.List;

import static pt.Box.boxForShapes;
import static pt.Hit.NO_HIT;
import static java.lang.System.out;

public class Tree implements Accelerator {

    private final Box box;
    private final Node node;

    public Tree(List<Shape> shapes) {
        out.printf("Building k-d tree (%d shapes)... ", shapes.size());
        box = boxForShapes(shapes);
        node = new Node(shapes);
        node.split(0);
        out.println("OK");
    }

    public Hit intersect(Ray r) {
        var intersect = box.intersect(r);
        var tmin = intersect[0];
        var tmax = intersect[1];
        if (tmax < tmin || tmax <= 0.0) {
            return NO_HIT;
        }
        return node.intersect(r, tmin, tmax);
    }
}
