package pt;

public interface Shape {

    void compile();

    Box boundingBox();

    Hit intersect(Ray r);

    Vector uv(Vector v);

    Vector normalAt(Vector v);

    Material materialAt(Vector v);
}
