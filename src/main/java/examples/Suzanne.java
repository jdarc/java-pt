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
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;
import static pt.Material.specularMaterial;
import static pt.Obj.loadOBJ;

public class Suzanne extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = diffuseMaterial(hexColor(0x334D5C));
        scene.add(new Sphere(new Vector(0.5, 1.0, 3.0), 1.0, lightMaterial(WHITE, 4.0)));
        scene.add(new Sphere(new Vector(1.5, 1.0, 3.0), 1.0, lightMaterial(WHITE, 4.0)));
        scene.add(new Cube(new Vector(-5.0, -5.0, -2.0), new Vector(5.0, 5.0, -1.0), material));
        var mesh = loadOBJ(pathTo("suzanne.obj"), specularMaterial(hexColor(0xEFC94C), 1.3));
        scene.add(mesh);
        var camera = lookAt(new Vector(1.0, -0.45, 4.0), new Vector(1.0, -0.6, 0.4), new Vector(0.0, 1.0, 0.0), 40.0);
        var sampler = new DefaultSampler(16, 8);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/suzanne%03d.png", iterations);
    }
}
