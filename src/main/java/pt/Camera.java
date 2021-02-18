package pt;

import java.util.concurrent.ThreadLocalRandom;

public class Camera {

    private Vector p;
    private Vector u;
    private Vector v;
    private Vector w;
    private double m;
    private double focalDistance;
    private double apertureRadius;

    public static Camera lookAt(Vector eye, Vector center, Vector up, double fovy) {
        var c = new Camera();
        c.p = eye;
        c.w = center.sub(eye).normalize();
        c.u = up.cross(c.w).normalize();
        c.v = c.w.cross(c.u).normalize();
        c.m = 1.0 / StrictMath.tan(fovy * Math.PI / 360.0);
        return c;
    }

    public void setFocus(Vector focalPoint, double apertureRadius) {
        focalDistance = focalPoint.sub(p).length();
        this.apertureRadius = apertureRadius;
    }

    public Ray castRay(int x, int y, int w, int h, double u, double v) {
        var aspect = (double)w / (double)h;
        var px = ((double)x + u - 0.5) / ((double)w - 1.0) * 2.0 - 1.0;
        var py = ((double)y + v - 0.5) / ((double)h - 1.0) * 2.0 - 1.0;
        var d = Vector.ZERO;
        d = d.add(this.u.mulScalar(-px * aspect));
        d = d.add(this.v.mulScalar(-py));
        d = d.add(this.w.mulScalar(m));
        d = d.normalize();
        var p = new Vector(this.p);
        if (apertureRadius > 0.0) {
            var focalPoint = this.p.add(d.mulScalar(focalDistance));
            var angle = ThreadLocalRandom.current().nextDouble() * 2.0 * Math.PI;
            var radius = ThreadLocalRandom.current().nextDouble() * apertureRadius;
            p = p.add(this.u.mulScalar(StrictMath.cos(angle) * radius));
            p = p.add(this.v.mulScalar(StrictMath.sin(angle) * radius));
            d = focalPoint.sub(p).normalize();
        }
        return new Ray(p, d);
    }
}
