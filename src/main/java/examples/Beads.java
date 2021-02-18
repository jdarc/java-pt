package examples;

import pt.DefaultSampler;
import pt.Material;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;
import java.util.Random;

import static pt.Camera.lookAt;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.SpecularMode.SPECULAR_MODE_FIRST;
import static pt.Util.radians;

public class Beads extends BaseExample {

    private final Random rand = new Random(1211L);

    public void run(int iterations) throws IOException {
        for (var i = 0; i < 30; i++) {
            frame(String.format("renders/beads%03d.png", i), (double)i / 30.0, iterations);
        }
    }

    private void frame(String path, double t, int iterations) throws IOException {
        Material[] materials = {
            glossyMaterial(hexColor(0x167F39), 1.3, radians(20.0)),
            glossyMaterial(hexColor(0x45BF55), 1.3, radians(20.0)),
            glossyMaterial(hexColor(0x96ED89), 1.3, radians(20.0)),
            };
        var eye = new Vector(4.0, 2.0, 8.0);
        var center = new Vector(0.0, 0.0, 0.0);
        var up = new Vector(0.0, 0.0, 1.0);
        var scene = new Scene();
        for (var a = 0; a < 80; a++) {
            var material = materials[rand.nextInt(materials.length)];
            var n = 400;
            var xs = lowPassNoise(n, 0.25, 4);
            var ys = lowPassNoise(n, 0.25, 4);
            var zs = lowPassNoise(n, 0.25, 4);
            var position = Vector.ZERO;
            var positions = new Vector[n];
            for (var i = 0; i < n; i++) {
                positions[i] = new Vector(position);
                var v = new Vector(xs[i], ys[i], zs[i]).normalize().mulScalar(0.1);
                position = position.add(v);
            }
            for (var i = 0; i < n - 1; i++) {
                var la = positions[i];
                var lb = positions[i + 1];
                var p = la.add(lb.sub(la).mulScalar(t));
                var sphere = new Sphere(p, 0.1, material);
                scene.add(sphere);
            }
        }
        scene.add(new Sphere(new Vector(4.0, 4.0, 20.0), 2.0, lightMaterial(hexColor(0xFFFFFF), 30.0)));
        var camera = lookAt(eye, center, up, 40.0);
        var sampler = new DefaultSampler(4, 4);
        sampler.specularMode = SPECULAR_MODE_FIRST;
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender(path, iterations);
    }

    private double[] normalize(double[] values, double a, double b) {
        var result = new double[values.length];
        var lo = values[0];
        var hi = values[0];
        for (var x : values) {
            lo = Math.min(lo, x);
            hi = Math.max(hi, x);
        }
        for (var i = 0; i < values.length; i++) {
            var x = values[i];
            var p = (x - lo) / (hi - lo);
            result[i] = a + p * (b - a);
        }
        return result;
    }

    private double[] lowPass(double[] values, double alpha) {
        var result = new double[values.length];
        var y = 0.0;
        for (var i = 0; i < values.length; i++) {
            var x = values[i];
            y -= alpha * (y - x);
            result[i] = y;
        }
        return result;
    }

    private double[] lowPassNoise(int n, double alpha, int iterations) {
        var result = new double[n];
        for (var i = 0; i < result.length; i++) {
            result[i] = rand.nextDouble() * 2.0 - 1.0;
        }
        for (var i = 0; i < iterations; i++) {
            result = lowPass(result, alpha);
        }
        result = normalize(result, -1.0, 1.0);
        return result;
    }
}
