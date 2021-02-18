package pt;

public interface SDF {

    double evaluate(Vector p);

    Box boundingBox();
}
