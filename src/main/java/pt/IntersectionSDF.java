package pt;

public class IntersectionSDF implements SDF {

    private final SDF[] items;

    public IntersectionSDF(SDF... items) {
        this.items = items.clone();
    }

    public double evaluate(Vector p) {
        var result = 0.0;
        for (var i = 0; i < items.length; i++) {
            var d = items[i].evaluate(p);
            if (i == 0 || d > result) {
                result = d;
            }
        }
        return result;
    }

    public Box boundingBox() {
        Box result = null;
        for (var i = 0; i < items.length; i++) {
            var box = items[i].boundingBox();
            if (i == 0) {
                result = box;
            } else {
                result = result.extend(box);
            }
        }
        return result;
    }
}
