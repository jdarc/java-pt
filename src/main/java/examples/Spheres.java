package examples;

import pt.Color;
import pt.DefaultSampler;
import pt.Material;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Util.radians;

public class Spheres extends BaseExample {

    private static final Material[] materials = {
        glossyMaterial(hexColor(0x730046), 1.4, radians(30.0)),
        glossyMaterial(hexColor(0xBFBB11), 1.4, radians(30.0)),
        glossyMaterial(hexColor(0xFFC200), 1.4, radians(30.0)),
        glossyMaterial(hexColor(0xE88801), 1.4, radians(30.0)),
        glossyMaterial(hexColor(0xC93C00), 1.4, radians(30.0)),
        };

    private static void sphere(Scene scene, Vector previous, Vector center, double radius, int depth) {
        if (depth <= 0) {
            return;
        }
        var material = materials[(depth + 5) % materials.length];
        scene.add(new Sphere(center, radius, material));
        var r2 = radius / 2.5;
        var offset = radius + r2;
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
                    if (d.equals(previous.negate())) {
                        continue;
                    }
                    var c2 = center.add(d.mulScalar(offset));
                    sphere(scene, d, c2, r2, depth - 1);
                }
            }
        }
    }

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.color = hexColor(0xFFFFFF);
        sphere(scene, Vector.ZERO, Vector.ZERO, 1.0, 8);
        scene.add(new Sphere(new Vector(0.0, 0.0, 6.0), 0.5, lightMaterial(Color.WHITE, 1.0)));
        var camera = lookAt(new Vector(3.0, 1.75, 1.0), new Vector(0.75, 0.5, 0.0), new Vector(0.0, 0.0, 1.0), 30.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/spheres%03d.png", iterations);
    }
}
