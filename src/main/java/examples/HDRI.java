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
import static pt.ColorTexture.getTexture;
import static pt.Material.glossyMaterial;
import static pt.Util.radians;

public class HDRI extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.color = WHITE;
        scene.texture = getTexture("courtyard_1k.png");
        if (scene.texture != null) {
            scene.texture.mulScalar(1.5).pow(1.0 / 2.2);
        }
        var material = glossyMaterial(WHITE, 2.0, radians(0.0));
        material.texture = getTexture("examples/checker.png");
        scene.add(new Sphere(new Vector(0.0, 0.0, 0.0), 1.0, material));
        scene.add(new Sphere(new Vector(-2.5, 0.0, 0.0), 1.0, material));
        scene.add(new Sphere(new Vector(2.5, 0.0, 0.0), 1.0, material));
        scene.add(new Sphere(new Vector(0.0, 0.0, -2.5), 1.0, material));
        scene.add(new Sphere(new Vector(0.0, 0.0, 2.5), 1.0, material));
        material = glossyMaterial(hexColor(0xEFECCA), 1.1, radians(45.0));
        scene.add(new Cube(new Vector(-100.0, -100.0, -100.0), new Vector(100.0, -1.0, 100.0), material));
        var camera = lookAt(new Vector(2.0, 3.0, 4.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 1.0, 0.0), 40.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/hdri%03d.png", iterations);
    }
}
