package pt;

import java.util.concurrent.ThreadLocalRandom;

import static pt.BounceType.BOUNCE_TYPE_ANY;
import static pt.BounceType.BOUNCE_TYPE_DIFFUSE;
import static pt.BounceType.BOUNCE_TYPE_SPECULAR;
import static pt.Color.BLACK;
import static pt.LightMode.LIGHT_MODE_ALL;
import static pt.LightMode.LIGHT_MODE_RANDOM;
import static pt.Material.materialAt;
import static pt.SpecularMode.SPECULAR_MODE_ALL;
import static pt.SpecularMode.SPECULAR_MODE_FIRST;
import static pt.SpecularMode.SPECULAR_MODE_NAIVE;
import static pt.Vector.randomUnitVector;

public class DefaultSampler implements Sampler {

    public final int firstHitSamples;
    public final int maxBounces;
    public final boolean directLighting;
    public final boolean softShadows;
    public LightMode lightMode;
    public SpecularMode specularMode;

    public DefaultSampler() {
        firstHitSamples = 1;
        maxBounces = 0;
        directLighting = true;
        softShadows = false;
        lightMode = LIGHT_MODE_ALL;
        specularMode = SPECULAR_MODE_ALL;
    }

    public DefaultSampler(int firstHitSamples, int maxBounces) {
        this.firstHitSamples = firstHitSamples;
        this.maxBounces = maxBounces;
        directLighting = true;
        softShadows = true;
        lightMode = LIGHT_MODE_RANDOM;
        specularMode = SPECULAR_MODE_NAIVE;
    }

    @Override
    public Color sample(Scene scene, Ray ray) {
        return sample(scene, ray, true, firstHitSamples, 0);
    }

    private Color sample(Scene scene, Ray ray, boolean emission, int samples, int depth) {
        if (depth > maxBounces) {
            return BLACK;
        }
        var hit = scene.intersect(ray);
        if (!hit.ok()) {
            return sampleEnvironment(scene, ray);
        }
        var info = hit.info(ray);
        var material = info.material;
        var result = BLACK;
        if (material.emittance > 0.0) {
            if (directLighting && !emission) {
                return BLACK;
            }
            result = result.add(material.color.mulScalar(material.emittance * (double)samples));
        }
        var n = (int)Math.sqrt(samples);
        BounceType ma, mb;
        if (specularMode == SPECULAR_MODE_ALL || depth == 0 && specularMode == SPECULAR_MODE_FIRST) {
            ma = BOUNCE_TYPE_DIFFUSE;
            mb = BOUNCE_TYPE_SPECULAR;
        } else {
            ma = BOUNCE_TYPE_ANY;
            mb = BOUNCE_TYPE_ANY;
        }

        var rnd = ThreadLocalRandom.current();
        for (var u = 0; u < n; u++) {
            for (var v = 0; v < n; v++) {
                for (var mode = ma.ordinal(); mode <= mb.ordinal(); mode++) {
                    var fu = ((double)u + rnd.nextDouble()) / (double)n;
                    var fv = ((double)v + rnd.nextDouble()) / (double)n;
                    var bounceResult = ray.bounce(info, fu, fv, BounceType.values()[mode]);
                    var newRay = bounceResult.ray;
                    var reflected = bounceResult.reflect;
                    var p = bounceResult.p;
                    if (mode == BOUNCE_TYPE_ANY.ordinal()) {
                        p = 1.0;
                    }
                    if (p > 0.0 && reflected) {
                        // specular
                        var indirect = sample(scene, newRay, true, 1, depth + 1);
                        var tinted = indirect.mix(material.color.mul(indirect), material.tint);
                        result = result.add(tinted.mulScalar(p));
                    }
                    if (p > 0.0 && !reflected) {
                        // diffuse
                        var indirect = sample(scene, newRay, false, 1, depth + 1);
                        var direct = BLACK;
                        if (directLighting) {
                            direct = sampleLights(scene, info.ray);
                        }
                        result = result.add(material.color.mul(direct.add(indirect)).mulScalar(p));
                    }
                }
            }
        }

        return result.divScalar(n * n);
    }

    private Color sampleEnvironment(Scene scene, Ray ray) {
        if (scene.texture != null) {
            var d = ray.direction;
            var u = StrictMath.atan2(d.z, d.x) + scene.textureAngle;
            var v = StrictMath.atan2(d.y, new Vector(d.x, 0.0, d.z).length());
            u = (u + Math.PI) / (2.0 * Math.PI);
            v = (v + Math.PI / 2.0) / Math.PI;
            return scene.texture.sample(u, v);
        }
        return scene.color;
    }

    private Color sampleLights(Scene scene, Ray n) {
        var nLights = scene.lights.size();
        if (nLights == 0) {
            return BLACK;
        }

        if (lightMode == LIGHT_MODE_ALL) {
            var result = BLACK;
            for (var light : scene.lights) {
                result = result.add(sampleLight(scene, n, light));
            }
            return result;
        } else {
            // pick a random light
            var light = scene.lights.get(ThreadLocalRandom.current().nextInt(nLights));
            return sampleLight(scene, n, light).mulScalar(nLights);
        }
    }

    private Color sampleLight(Scene scene, Ray n, Shape light) {
        // get bounding sphere center and radius
        Vector center;
        double radius;

        if (light instanceof Sphere) {
            var t = (Sphere)light;
            radius = t.radius;
            center = t.center;
        } else {
            // get bounding sphere from bounding box
            var box = light.boundingBox();
            radius = box.outerRadius();
            center = box.center();
        }

        // get random point in disk
        var point = new Vector(center);
        if (softShadows) {
            var rnd = ThreadLocalRandom.current();
            while (true) {
                var x = rnd.nextDouble() * 2.0 - 1.0;
                var y = rnd.nextDouble() * 2.0 - 1.0;
                if (x * x + y * y <= 1.0) {
                    var l = center.sub(n.origin).normalize();
                    var u = l.cross(randomUnitVector()).normalize();
                    var v = l.cross(u);
                    point = Vector.ZERO;
                    point = point.add(u.mulScalar(x * radius));
                    point = point.add(v.mulScalar(y * radius));
                    point = point.add(center);
                    break;
                }
            }
        }

        // construct ray toward light point
        var ray = new Ray(n.origin, point.sub(n.origin).normalize());

        // get cosine term
        var diffuse = ray.direction.dot(n.direction);
        if (diffuse <= 0.0) {
            return BLACK;
        }

        // check for light visibility
        var hit = scene.intersect(ray);
        if (!hit.ok() || hit.shape != light) {
            return BLACK;
        }

        // compute solid angle (hemisphere coverage)
        var hyp = center.sub(n.origin).length();
        var opp = radius;
        var theta = StrictMath.asin(opp / hyp);
        var adj = opp / StrictMath.tan(theta);
        var d = StrictMath.cos(theta) * adj;
        var r = StrictMath.sin(theta) * adj;
        var coverage = r * r / (d * d);

        // TODO: fix issue where hyp < opp (point inside sphere)
        if (hyp < opp) {
            coverage = 1.0;
        }
        coverage = Math.min(coverage, 1.0);

        // get material properties from light
        var material = materialAt(light, point);

        // combine factors
        var m = material.emittance * diffuse * coverage;
        return material.color.mulScalar(m);
    }
}
