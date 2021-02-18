package pt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pt.Axis.AXIS_NONE;
import static pt.Axis.AXIS_X;
import static pt.Axis.AXIS_Y;
import static pt.Axis.AXIS_Z;
import static pt.Hit.NO_HIT;
import static pt.Util.median;

public class Node {

    private Axis axis;
    private double point;
    private List<Shape> shapes;
    private Node left;
    private Node right;

    public Node(List<Shape> shapes) {
        axis = AXIS_NONE;
        point = 0.0;
        this.shapes = shapes;
        left = null;
        right = null;
    }

    public Hit intersect(Ray r, double tmin, double tmax) {
        double tsplit;
        boolean leftFirst;
        if (axis == AXIS_X) {
            tsplit = (point - r.origin.x) / r.direction.x;
            leftFirst = r.origin.x < point || r.origin.x == point && r.direction.x <= 0.0;
        } else if (axis == AXIS_Y) {
            tsplit = (point - r.origin.y) / r.direction.y;
            leftFirst = r.origin.y < point || r.origin.y == point && r.direction.y <= 0.0;
        } else if (axis == AXIS_Z) {
            tsplit = (point - r.origin.z) / r.direction.z;
            leftFirst = r.origin.z < point || r.origin.z == point && r.direction.z <= 0.0;
        } else {
            return intersectShapes(r);
        }
        Node first;
        Node second;
        if (leftFirst) {
            first = left;
            second = right;
        } else {
            first = right;
            second = left;
        }
        if (tsplit > tmax || tsplit <= 0.0) {
            return first.intersect(r, tmin, tmax);
        } else if (tsplit < tmin) {
            return second.intersect(r, tmin, tmax);
        } else {
            var h1 = first.intersect(r, tmin, tsplit);
            if (h1.t <= tsplit) {
                return h1;
            }
            var h2 = second.intersect(r, tsplit, Math.min(tmax, h1.t));
            if (h1.t <= h2.t) {
                return h1;
            } else {
                return h2;
            }
        }
    }

    public Hit intersectShapes(Ray r) {
        var hit = NO_HIT;
        for (var shape : shapes) {
            var h = shape.intersect(r);
            if (h.t < hit.t) {
                hit = h;
            }
        }
        return hit;
    }

    public int partitionScore(Axis axis, double point) {
        var left = 0;
        var right = 0;
        for (var shape : shapes) {
            var box = shape.boundingBox();
            var partition = box.partition(axis, point);
            if (partition[0]) {
                ++left;
            }
            if (partition[1]) {
                ++right;
            }
        }
        return Math.max(left, right);
    }

    public PartitionResult partition(int size, Axis axis, double point) {
        List<Shape> left = new ArrayList<>(size);
        List<Shape> right = new ArrayList<>(size);
        for (var shape : shapes) {
            var box = shape.boundingBox();
            var partition = box.partition(axis, point);
            if (partition[0]) {
                left.add(shape);
            }
            if (partition[1]) {
                right.add(shape);
            }
        }
        return new PartitionResult(left, right);
    }

    public void split(int depth) {
        if (shapes.size() < 8) {
            return;
        }

        var xs = new double[shapes.size() * 2];
        var ys = new double[shapes.size() * 2];
        var zs = new double[shapes.size() * 2];

        var t = 0;
        for (var shape : shapes) {
            var box = shape.boundingBox();
            xs[t] = box.min.x;
            ys[t] = box.min.y;
            zs[t++] = box.min.z;
            xs[t] = box.max.x;
            ys[t] = box.max.y;
            zs[t++] = box.max.z;
        }

        Arrays.sort(xs);
        Arrays.sort(ys);
        Arrays.sort(zs);

        var mx = median(xs);
        var my = median(ys);
        var mz = median(zs);

        var best = (int)((double)shapes.size() * 0.85);
        var bestAxis = AXIS_NONE;
        var bestPoint = 0.0;
        var sx = partitionScore(AXIS_X, mx);
        if (sx < best) {
            best = sx;
            bestAxis = AXIS_X;
            bestPoint = mx;
        }
        var sy = partitionScore(AXIS_Y, my);
        if (sy < best) {
            best = sy;
            bestAxis = AXIS_Y;
            bestPoint = my;
        }
        var sz = partitionScore(AXIS_Z, mz);
        if (sz < best) {
            best = sz;
            bestAxis = AXIS_Z;
            bestPoint = mz;
        }
        if (bestAxis == AXIS_NONE) {
            return;
        }
        var partition = partition(best, bestAxis, bestPoint);
        axis = bestAxis;
        point = bestPoint;
        left = new Node(partition.left);
        right = new Node(partition.right);
        left.split(depth + 1);
        right.split(depth + 1);
        shapes = null;
    }

    private static class PartitionResult {

        public final List<Shape> left;
        public final List<Shape> right;

        public PartitionResult(List<Shape> left, List<Shape> right) {
            this.left = left;
            this.right = right;
        }
    }
}
