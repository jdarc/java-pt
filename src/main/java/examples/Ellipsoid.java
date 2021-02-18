package examples;

import pt.Cube;
import pt.DefaultSampler;
import pt.MatrixFactory;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.TransformedShape;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Util.radians;

public class Ellipsoid extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var wall = glossyMaterial(hexColor(0xFCFAE1), 1.333, radians(30.0));
        scene.add(new Sphere(new Vector(10.0, 10.0, 10.0), 2.0, lightMaterial(WHITE, 50.0)));
        scene.add(new Cube(new Vector(-100.0, -100.0, -100.0), new Vector(-12.0, 100.0, 100.0), wall));
        scene.add(new Cube(new Vector(-100.0, -100.0, -100.0), new Vector(100.0, -1.0, 100.0), wall));
        var material = glossyMaterial(hexColor(0x167F39), 1.333, radians(30.0));
        var sphere = new Sphere(Vector.ZERO, 1.0, material);
        for (var i = 0; i < 180; i += 30) {
            var m = MatrixFactory.identity();
            m = m.scale(new Vector(0.3, 1.0, 5.0));
            m = m.rotate(new Vector(0.0, 1.0, 0.0), radians(i));
            var shape = new TransformedShape(sphere, m);
            scene.add(shape);
        }
        var camera = lookAt(new Vector(8.0, 8.0, 0.0), new Vector(1.0, 0.0, 0.0), new Vector(0.0, 1.0, 0.0), 45.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/ellipsoid%03d.png", iterations);
    }
}
