package examples;

import pt.Cube;
import pt.DefaultSampler;
import pt.Renderer;
import pt.Scene;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;

public class Example3 extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = diffuseMaterial(hexColor(0xFCFAE1));
        scene.add(new Cube(new Vector(-1000.0, -1.0, -1000.0), new Vector(1000.0, 0.0, 1000.0), material));
        for (var x = -20; x <= 20; x++) {
            for (var z = -20; z <= 20; z++) {
                if ((x + z) % 2 == 0) {
                    continue;
                }
                var s = 0.1;
                var min = new Vector((double)x - s, 0.0, (double)z - s);
                var max = new Vector((double)x + s, 2.0, (double)z + s);
                scene.add(new Cube(min, max, material));
            }
        }
        scene.add(new Cube(new Vector(-5.0, 10.0, -5.0), new Vector(5.0, 11.0, 5.0), lightMaterial(WHITE, 5.0)));
        var camera = lookAt(new Vector(20.0, 10.0, 0.0), new Vector(8.0, 0.0, 0.0), new Vector(0.0, 1.0, 0.0), 45.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/example3%03d.png", iterations);
    }
}
