package pt;

public interface Texture {

    Color sample(double u, double v);

    Vector normalSample(double u, double v);

    Vector bumpSample(double u, double v);

    void pow(double a);

    Texture mulScalar(double a);
}
