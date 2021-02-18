package pt;

public class Pixel {

    private int samples = 0;
    private Color m = Color.BLACK;
    private Color v = Color.BLACK;

    public void addSample(Color sample) {
        if (++samples == 1) {
            m = sample;
            return;
        }
        var om = m;
        m = m.add(sample.sub(m).divScalar(samples));
        v = v.add(sample.sub(om).mul(sample.sub(m)));
    }

    public int samples() {
        return samples;
    }

    public Color color() {
        return m;
    }

    public Color variance() {
        return samples < 2 ? Color.BLACK : v.divScalar(samples - 1);
    }

    public Color standardDeviation() {
        return variance().pow(0.5);
    }
}
