package examples;

import pt.DefaultSampler;
import pt.Material;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Util.radians;

public class SpheresAnim extends BaseExample {

    private static final double FPS = 30.0;
    private static final double DURATION_1 = 14.0;
    private static final double DURATION_2 = 2.0;
    private static final double FRAMES = (DURATION_1 + DURATION_2) * FPS;
    private static final double BOUNCE_DURATION = 2.0;
    private static final double FADE_DURATION = 1.0;
    private static final double RPS = 0.125 / DURATION_1;

    private static final Material[] MATERIALS = {
        glossyMaterial(hexColor(0x730046), 1.333, radians(30.0)),
        glossyMaterial(hexColor(0xBFBB11), 1.333, radians(30.0)),
        glossyMaterial(hexColor(0xFFC200), 1.333, radians(30.0)),
        glossyMaterial(hexColor(0xE88801), 1.333, radians(30.0)),
        glossyMaterial(hexColor(0xC93C00), 1.333, radians(30.0)),
        };

    private static final double[] BOUNCE_START = {0.25, 2.25, 3.00, 5.75, 8.00, 9.50, 12.0};

    private static final double[] BOUNCE_DEVIATION = {0.0, 0.05, 0.1, 0.15, 0.15, 0.2, 0.2};

    private static final Random rnd = new Random(123L);

    private void sphere(Scene scene, Vector direction, Vector anchor, double radius, int depth, int height, double t) {
        if (height <= 0) {
            return;
        }
        var tt = t - BOUNCE_START[depth] + rnd.nextGaussian() * BOUNCE_DEVIATION[depth];
        tt = Math.max(Math.min(tt / BOUNCE_DURATION, 1.0), 0.0);
        var r = radius * easeOutElastic(tt);
        if (t > DURATION_1) {
            var u = (t - DURATION_1) / FADE_DURATION + (double)depth * 0.1 - 0.25;
            u = Math.max(Math.min(u, 1.0), 0.0);
            r = radius * (1.0 - easeInQuint(u));
        }
        var center = anchor.add(direction.mulScalar(r));
        var material = MATERIALS[(height + 6) % MATERIALS.length];
        if (tt > 0.0 && r > 0.0) {
            scene.add(new Sphere(center, r, material));
        }
        var r2 = radius / 2.5;
        for (var dx = -1; dx <= 1; dx++) {
            for (var dy = -1; dy <= 1; dy++) {
                for (var dz = -1; dz <= 1; dz++) {
                    var n = 0;
                    if (dx != 0) {
                        n++;
                    }
                    if (dy != 0) {
                        n++;
                    }
                    if (dz != 0) {
                        n++;
                    }
                    if (n != 1) {
                        continue;
                    }
                    var d = new Vector(dx, dy, dz);
                    if (d.equals(direction.negate())) {
                        continue;
                    }
                    var c2 = center.add(d.mulScalar(r));
                    sphere(scene, d, c2, r2, depth + 1, height - 1, t);
                }
            }
        }
    }

    private void frame(int i, int iterations) throws IOException {
        var t = (double)i / FPS;
        var a = t * 2.0 * Math.PI * RPS;
        var x = Math.cos(a) * 5.0;
        var y = Math.sin(a) * 5.0;
        var scene = new Scene();
        scene.color = hexColor(0xFFFFFF);
        sphere(scene, Vector.ZERO, Vector.ZERO, 1.0, 0, 7, t);
        scene.add(new Sphere(new Vector(0.0, 0.0, 6.0), 0.5, lightMaterial(WHITE, 1.0)));
        var camera = lookAt(new Vector(x, y, 1.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), 30.0);
        var sampler = new DefaultSampler(16, 16);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/spheres_anim%03d.png", iterations);
    }

    private double easeInOutCubic(double t) {
        if (t < 0.5) {
            return 4.0 * t * t * t;
        } else {
            return (t - 1.0) * (2.0 * t - 2.0) * (2.0 * t - 2.0) + 1.0;
        }
    }

    private double easeOutElastic(double t) {
        var p = 0.2;
        return Math.pow(2.0, -10.0 * t) * Math.sin((t - p / 4.0) * (2.0 * Math.PI) / p) + 1.0;
    }

    private double easeInBack(double t) {
        var s = 1.70158;
        return 1.0 - t * t * ((s + 1.0) * t - s);
    }

    private double easeInQuint(double t) {
        return t * t * t * t * t;
    }

    public void run(int iterations) throws IOException, InterruptedException {
        for (var i = 240; (double)i < FRAMES; i += 1) {
            frame(i, iterations);
        }
        Thread.sleep(5 * Calendar.getInstance().get(Calendar.SECOND));
    }
}
