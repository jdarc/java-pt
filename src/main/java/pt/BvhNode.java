package pt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BvhNode {

    private static final int MIN_ITEM_COUNT = 8;

    private static final List<Comparator<Shape>> sorters = new ArrayList<>();

    static {
        sorters.add(Comparator.comparingDouble(a -> a.boundingBox().cog.x));
        sorters.add(Comparator.comparingDouble(a -> a.boundingBox().cog.y));
        sorters.add(Comparator.comparingDouble(a -> a.boundingBox().cog.z));
    }

    private final AABB aabb;
    private List<Shape> shapes;
    private BvhNode childA;
    private BvhNode childB;

    public BvhNode(List<Shape> shapes) {
        this.shapes = shapes;
        aabb = new AABB();
        for (var shape : shapes) {
            aabb.grow(shape);
        }
    }

    public void split(int depth) {
        if (shapes.size() > MIN_ITEM_COUNT && depth < 64) {

            var subsetA = new ArrayList<Shape>(shapes.size());
            var subsetB = new ArrayList<Shape>(shapes.size());

            var SAP = 1.0 / aabb.surfaceArea();
            var SAH = Double.MAX_VALUE;

            var a = new AABB();
            var b = new AABB();
            var saa = new double[shapes.size()];
            var sab = new double[shapes.size()];

            for (var sorter : sorters) {
                shapes.sort(sorter);

                a.reset();
                for (var i = 0; i < shapes.size() - 1; ++i) {
                    a.grow(shapes.get(i));
                    saa[i] = a.surfaceArea();
                }

                b.reset();
                for (var i = shapes.size() - 1; i > 0; --i) {
                    b.grow(shapes.get(i));
                    sab[i] = b.surfaceArea();
                }

                var splitIndex = 0;
                for (var i = 0; i < shapes.size() - 1; i++) {
                    var newSAH = (saa[i] * (i + 1) + sab[i + 1] * (shapes.size() - 1 - i)) * SAP;
                    if (newSAH < SAH) {
                        SAH = newSAH;
                        splitIndex = i + 1;
                    }
                }

                if (splitIndex > 0) {
                    subsetA.clear();
                    subsetA.addAll(shapes.subList(0, splitIndex));
                    subsetA.trimToSize();
                    subsetB.clear();
                    subsetB.addAll(shapes.subList(splitIndex, shapes.size()));
                    subsetB.trimToSize();
                }
            }

            shapes = null;

            if (subsetA.size() >= subsetB.size()) {
                childA = new BvhNode(subsetB);
                childB = new BvhNode(subsetA);
            } else {
                childA = new BvhNode(subsetA);
                childB = new BvhNode(subsetB);
            }

            childA.split(depth + 1);
            childB.split(depth + 1);
        }
    }

    public Hit intersect(Ray r) {
        var hit = Hit.NO_HIT;

        var tmax = Double.MAX_VALUE;
        var tn = aabb.intersect(r.origin, r.direction);
        if (tn >= tmax) {
            return hit;
        }

        var stackptr = 0;
        var stack = new BvhTraversalData[64];
        stack[stackptr] = new BvhTraversalData(this, tn);

        while (stackptr >= 0) {
            var cur = stack[stackptr--];
            if (cur.tn < tmax) {
                var node = cur.node;
                if (node.isLeaf()) {
                    for (var shape : node.shapes) {
                        var h = shape.intersect(r);
                        if (h.t < tmax) {
                            tmax = h.t;
                            hit = h;
                        }
                    }
                } else {
                    var na = node.childA;
                    var nb = node.childB;
                    var ta = na.aabb.intersect(r.origin, r.direction);
                    var tb = nb.aabb.intersect(r.origin, r.direction);
                    if (ta > tb) {
                        if (ta < tmax) {
                            stack[++stackptr] = new BvhTraversalData(na, ta);
                        }
                        if (tb < tmax) {
                            stack[++stackptr] = new BvhTraversalData(nb, tb);
                        }
                    } else {
                        if (tb < tmax) {
                            stack[++stackptr] = new BvhTraversalData(nb, tb);
                        }
                        if (ta < tmax) {
                            stack[++stackptr] = new BvhTraversalData(na, ta);
                        }
                    }
                }
            }
        }
        return hit;
    }

    private boolean isLeaf() {
        return childA == null && childB == null;
    }

    private static class BvhTraversalData {

        public final BvhNode node;
        public final double tn;

        public BvhTraversalData(BvhNode node, double tn) {
            this.node = node;
            this.tn = tn;
        }
    }

    private static class AABB {

        public double minx = 0.0;
        public double miny = 0.0;
        public double minz = 0.0;
        public double maxx = 0.0;
        public double maxy = 0.0;
        public double maxz = 0.0;
        public double cogx = 0.0;
        public double cogy = 0.0;
        public double cogz = 0.0;

        public double surfaceArea() {
            var dx = maxx - minx;
            var dy = maxy - miny;
            var dz = maxz - minz;
            return (dx * dy + dy * dz + dz * dx) * 2.0;
        }

        public AABB() {
            reset();
        }

        public void reset() {
            minx = Double.POSITIVE_INFINITY;
            miny = Double.POSITIVE_INFINITY;
            minz = Double.POSITIVE_INFINITY;
            maxx = Double.NEGATIVE_INFINITY;
            maxy = Double.NEGATIVE_INFINITY;
            maxz = Double.NEGATIVE_INFINITY;
            cogx = 0.0;
            cogy = 0.0;
            cogz = 0.0;
        }

        public void grow(double x, double y, double z) {
            if (x < minx) {
                minx = x;
            }
            if (y < miny) {
                miny = y;
            }
            if (z < minz) {
                minz = z;
            }
            if (x > maxx) {
                maxx = x;
            }
            if (y > maxy) {
                maxy = y;
            }
            if (z > maxz) {
                maxz = z;
            }
            cogx = (maxx + minx) * 0.5;
            cogy = (maxy + miny) * 0.5;
            cogz = (maxz + minz) * 0.5;
        }

        public void grow(Vector point) {
            grow(point.x, point.y, point.z);
        }

        public void grow(Shape shape) {
            var box = shape.boundingBox();
            grow(box.min);
            grow(box.max);
        }

        public double intersect(Vector origin, Vector direction) {
            var t0x = (minx - origin.x) / direction.x;
            var t1x = (maxx - origin.x) / direction.x;
            if (t0x < 0.0 && t1x < 0.0) {
                return Double.POSITIVE_INFINITY;
            }

            var t0y = (miny - origin.y) / direction.y;
            var t1y = (maxy - origin.y) / direction.y;
            if (t0y < 0.0 && t1y < 0.0) {
                return Double.POSITIVE_INFINITY;
            }

            var t0z = (minz - origin.z) / direction.z;
            var t1z = (maxz - origin.z) / direction.z;
            if (t0z < 0.0 && t1z < 0.0) {
                return Double.POSITIVE_INFINITY;
            }

            if (t0x > t1x) {
                var tmp = t0x;
                t0x = t1x;
                t1x = tmp;
            }

            if (t0y > t1y) {
                var tmp = t0y;
                t0y = t1y;
                t1y = tmp;
            }

            if (t0z > t1z) {
                var tmp = t0z;
                t0z = t1z;
                t1z = tmp;
            }

            var tn = StrictMath.max(t0x, StrictMath.max(t0y, t0z));
            var tf = StrictMath.min(t1x, StrictMath.min(t1y, t1z));
            return (tn <= tf) ? tn : Double.POSITIVE_INFINITY;
        }
    }
}


