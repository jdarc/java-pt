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

public class Maze extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var floor = glossyMaterial(hexColor(0x7E827A), 1.1, radians(30.0));
        var material = glossyMaterial(hexColor(0xE3CDA4), 1.1, radians(30.0));
        scene.add(new Cube(new Vector(-10000.0, -10000.0, -10000.0), new Vector(10000.0, 10000.0, 0.0), floor));
        var n = 24;
        for (var x = -n; x <= n; x++) {
            for (var y = -n; y <= n; y++) {
                if (ThreadLocalRandom.current().nextDouble() > 0.8) {
                    var min = new Vector((double)x - 0.5, (double)y - 0.5, 0.0);
                    var max = new Vector((double)x + 0.5, (double)y + 0.5, 1.0);
                    var cube = new Cube(min, max, material);
                    scene.add(cube);
                }
            }
        }
        scene.add(new Sphere(new Vector(0.0, 0.0, 2.25), 0.25, lightMaterial(WHITE, 500.0)));
        var camera = lookAt(new Vector(1.0, 0.0, 30.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), 35.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/maze%03d.png", iterations);
    }
}
