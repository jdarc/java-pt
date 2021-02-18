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
import static pt.Material.clearMaterial;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Material.specularMaterial;
import static pt.SpecularMode.SPECULAR_MODE_FIRST;
import static pt.Util.radians;

public class Example1 extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.add(new Sphere(new Vector(1.5, 1.25, 0.0), 1.25, specularMaterial(hexColor(0x004358), 1.3)));
        scene.add(new Sphere(new Vector(-1.0, 1.0, 2.0), 1.0, specularMaterial(hexColor(0xFFE11A), 1.3)));
        scene.add(new Sphere(new Vector(-2.5, 0.75, 0.0), 0.75, specularMaterial(hexColor(0xFD7400), 1.3)));
        scene.add(new Sphere(new Vector(-0.75, 0.5, -1.0), 0.5, clearMaterial(1.5, 0.0)));
        scene.add(new Cube(new Vector(-10.0, -1.0, -10.0), new Vector(10.0, 0.0, 10.0), glossyMaterial(WHITE, 1.1, radians(10.0))));
        scene.add(new Sphere(new Vector(-1.5, 4.0, 0.0), 0.5, lightMaterial(WHITE, 30.0)));
        var camera = lookAt(new Vector(0.0, 2.0, -5.0), new Vector(0.0, 0.25, 3.0), new Vector(0.0, 1.0, 0.0), 45.0);
        camera.setFocus(new Vector(-0.75, 1.0, -1.0), 0.1);
        var sampler = new DefaultSampler(4, 8);
        sampler.specularMode = SPECULAR_MODE_FIRST;
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.adaptiveSamples = 32;
        renderer.fireflySamples = 256;
        renderer.iterativeRender("renders/example1%03d.png", iterations);
    }
}
