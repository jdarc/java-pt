package examples;

import pt.Cube;
import pt.DefaultSampler;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Util.radians;

public class Example2 extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = glossyMaterial(hexColor(0xEFC94C), 3.0, radians(30.0));
        var whiteMat = glossyMaterial(WHITE, 3.0, radians(30.0));
        for (var x = 0; x < 40; x++) {
            for (var z = 0; z < 40; z++) {
                var center = new Vector((double)x - 19.5, 0.0, (double)z - 19.5);
                scene.add(new Sphere(center, 0.4, material));
            }
        }
        scene.add(new Cube(new Vector(-100.0, -1.0, -100.0), new Vector(100.0, 0.0, 100.0), whiteMat));
        scene.add(new Sphere(new Vector(-1.0, 4.0, -1.0), 1.0, lightMaterial(WHITE, 30.0)));
        var camera = lookAt(new Vector(0.0, 4.0, -8.0), new Vector(0.0, 0.0, -2.0), new Vector(0.0, 1.0, 0.0), 45.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/example2%03d.png", iterations);
    }
}
