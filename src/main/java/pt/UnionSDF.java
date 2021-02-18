package pt;

public class UnionSDF implements SDF {

    private final SDF[] items;

    public UnionSDF(SDF... items) {
        this.items = items;
    }

    public double evaluate(Vector p) {
        var result = 0.0;
        for (var i = 0; i < items.length; i++) {
            var d = items[i].evaluate(p);
            if (i == 0 || d < result) {
                result = d;
            }
        }
        return result;
    }

    public Box boundingBox() {
        Box result = null;
        for (var i = 0; i < items.length; i++) {
            var box = items[i].boundingBox();
            result = i == 0 ? box : result.extend(box);
        }
        return result;
    }
}
