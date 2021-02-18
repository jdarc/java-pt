package pt;

import java.util.List;

public class Box {

    public final Vector min;
    public final Vector max;
    public final Vector cog;

    public Box(Vector min, Vector max) {
        this.min = min;
        this.max = max;
        cog = new Vector((max.x + min.x) * 0.5, (max.y + min.y) * 0.5, (max.z + min.z) * 0.5);
    }

    public static Box boxForShapes(List<Shape> shapes) {
        if (shapes.isEmpty()) {
            return new Box(Vector.ZERO, Vector.ZERO);
        }
        var box = shapes.get(0).boundingBox();
        for (var shape : shapes) {
            box = box.extend(shape.boundingBox());
        }
        return box;
    }

    public static Box boxForTriangles(Triangle[] shapes) {
        if (shapes.length == 0) {
            return new Box(Vector.ZERO, Vector.ZERO);
        }
        var box = shapes[0].boundingBox();
        for (var shape : shapes) {
            box = box.extend(shape.boundingBox());
        }
        return box;
    }

    public Vector anchor(Vector anchor) {
        return min.add(size().mul(anchor));
    }

    public Vector center() {
        return anchor(new Vector(0.5, 0.5, 0.5));
    }

    public double outerRadius() {
        return min.sub(center()).length();
    }

    public double innerRadius() {
        return center().sub(min).maxComponent();
    }

    public Vector size() {
        return max.sub(min);
    }

    public Box extend(Box b) {
        return new Box(min.min(b.min), max.max(b.max));
    }

    public boolean contains(Vector b) {
        return min.x <= b.x && max.x >= b.x &&
               min.y <= b.y && max.y >= b.y &&
               min.z <= b.z && max.z >= b.z;
    }

    public boolean intersects(Box b) {
        return !(min.x > b.max.x || max.x < b.min.x || min.y > b.max.y ||
                 max.y < b.min.y || min.z > b.max.z || max.z < b.min.z);
    }

    public double[] intersect(Ray r) {
        var x1 = (min.x - r.origin.x) / r.direction.x;
        var y1 = (min.y - r.origin.y) / r.direction.y;
        var z1 = (min.z - r.origin.z) / r.direction.z;
        var x2 = (max.x - r.origin.x) / r.direction.x;
        var y2 = (max.y - r.origin.y) / r.direction.y;
        var z2 = (max.z - r.origin.z) / r.direction.z;
        if (x1 > x2) {
            var temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 > y2) {
            var temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (z1 > z2) {
            var temp = z1;
            z1 = z2;
            z2 = temp;
        }
        var t1 = Math.max(Math.max(x1, y1), z1);
        var t2 = Math.min(Math.min(x2, y2), z2);
        return new double[]{t1, t2};
    }

    public Boolean[] partition(Axis axis, double point) {
        var left = false;
        var right = false;
        switch (axis) {
            case AXIS_X:
                left = min.x <= point;
                right = max.x >= point;
                break;
            case AXIS_Y:
                left = min.y <= point;
                right = max.y >= point;
                break;
            case AXIS_Z:
                left = min.z <= point;
                right = max.z >= point;
                break;
        }
        return new Boolean[]{left, right};
    }
}

