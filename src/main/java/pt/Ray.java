package pt;

import java.util.concurrent.ThreadLocalRandom;

import static pt.Util.cone;
import static pt.Vector.randomUnitVector;

public class Ray {

    public final Vector origin;
    public final Vector direction;

    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vector position(double t) {
        return origin.add(direction.mulScalar(t));
    }

    public Ray reflect(Ray i) {
        return new Ray(origin, direction.reflect(i.direction));
    }

    public Ray refract(Ray i, double n1, double n2) {
        var direction = this.direction.refract(i.direction, n1, n2);
        var origin = this.origin.add(direction.mulScalar(0.0001));
        return new Ray(origin, direction);
    }

    public double reflectance(Ray i, double n1, double n2) {
        return direction.reflectance(i.direction, n1, n2);
    }

    public Ray weightedBounce(double u, double v) {
        var radius = Math.sqrt(u);
        var theta = 2.0 * Math.PI * v;
        var s = direction.cross(randomUnitVector()).normalize();
        var t = direction.cross(s);
        var d = Vector.ZERO;
        d = d.add(s.mulScalar(radius * StrictMath.cos(theta)));
        d = d.add(t.mulScalar(radius * StrictMath.sin(theta)));
        d = d.add(direction.mulScalar(Math.sqrt(1.0 - u)));
        return new Ray(origin, d);
    }

    public Ray coneBounce(double theta, double u, double v) {
        return new Ray(origin, cone(direction, theta, u, v));
    }

    public BounceResult bounce(HitInfo info, double u, double v, BounceType bounceType) {
        var n1 = 1.0;
        var n2 = info.material.index;
        if (info.inside) {
            n1 = info.material.index;
            n2 = 1.0;
        }
        var p = info.material.reflectivity >= 0.0 ? info.material.reflectivity : info.ray.reflectance(this, n1, n2);
        var reflect = false;
        switch (bounceType) {
            case BOUNCE_TYPE_ANY:
                reflect = ThreadLocalRandom.current().nextDouble() < p;
                break;
            case BOUNCE_TYPE_DIFFUSE:
                break;
            case BOUNCE_TYPE_SPECULAR:
                reflect = true;
                break;
        }
        if (reflect) {
            var reflected = info.ray.reflect(this);
            return new BounceResult(reflected.coneBounce(info.material.gloss, u, v), true, p);
        } else if (info.material.transparent) {
            var refracted = info.ray.refract(this, n1, n2);
            return new BounceResult(refracted.coneBounce(info.material.gloss, u, v), true, 1.0 - p);
        } else {
            return new BounceResult(info.ray.weightedBounce(u, v), false, 1.0 - p);
        }
    }
}
