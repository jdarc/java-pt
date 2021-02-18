package pt;

public class DifferenceSDF implements SDF {

    private final SDF[] items;

    public DifferenceSDF(SDF... items) {
        this.items = items;
    }

    public double evaluate(Vector p) {
        var result = 0.0;
        for (var i = 0; i < items.length; i++) {
            var d = items[i].evaluate(p);
            if (i == 0) {
                result = d;
            } else if (-d > result) {
                result = -d;
            }
        }
        return result;
    }

    public Box boundingBox() {
        return items[0].boundingBox();
    }
}
