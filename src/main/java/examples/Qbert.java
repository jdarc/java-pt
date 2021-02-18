package examples;

import pt.Cube;
import pt.DefaultSampler;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Util.radians;

public class Qbert extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var floor = glossyMaterial(hexColor(0xFCFFF5), 1.2, radians(30.0));
        var cube = glossyMaterial(hexColor(0xFF8C00), 1.3, radians(20.0));
        var ball = glossyMaterial(hexColor(0xD90000), 1.4, radians(10.0));
        var n = 7;
        for (var z = 0; z < n; z++) {
            for (var x = 0; x < n - z; x++) {
                for (var y = 0; y < n - z - x; y++) {
                    scene.add(new Cube(new Vector(x, y, z), new Vector((double)x + 1.0, (double)y + 1.0, (double)z + 1.0), cube));
                    if (x + y == n - z - 1) {
                        if (ThreadLocalRandom.current().nextDouble() > 0.75) {
                            scene.add(new Sphere(new Vector((double)x + 0.5, (double)y + 0.5, (double)z + 1.5), 0.35, ball));
                        }
                    }
                }
            }
        }
        var x = -1000.0;
        var y = -1000.0;
        var z = -1.0;
        scene.add(new Cube(new Vector(x, y, z), new Vector(1000.0, 1000.0, 0.0), floor));
        scene.add(new Sphere(new Vector(n, (double)n / 3.0, (double)n * 2.0), 1.0, lightMaterial(WHITE, 100.0)));
        var camera = lookAt(new Vector((double)n * 2.0, (double)n * 2.0, (double)n * 2.0), new Vector(0.0, 0.0, (double)n / 4.0), new Vector(0.0, 0.0, 1.0), 35.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/qbert%03d.png", iterations);
    }
}
