package examples;

import pt.Cube;
import pt.DefaultSampler;
import pt.Material;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.clearMaterial;
import static pt.Material.diffuseMaterial;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Material.metallicMaterial;
import static pt.Material.specularMaterial;
import static pt.Material.transparentMaterial;
import static pt.Util.radians;

public class Materials extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var r = 0.4;
        Material material;

        material = diffuseMaterial(hexColor(0x334D5C));
        scene.add(new Sphere(new Vector(-2.0, r, 0.0), r, material));

        material = specularMaterial(hexColor(0x334D5C), 2.0);
        scene.add(new Sphere(new Vector(-1.0, r, 0.0), r, material));

        material = glossyMaterial(hexColor(0x334D5C), 2.0, radians(50.0));
        scene.add(new Sphere(new Vector(0.0, r, 0.0), r, material));

        material = transparentMaterial(hexColor(0x334D5C), 2.0, radians(20.0), 1.0);
        scene.add(new Sphere(new Vector(1.0, r, 0.0), r, material));

        material = clearMaterial(2.0, 0.0);
        scene.add(new Sphere(new Vector(2.0, r, 0.0), r, material));

        material = metallicMaterial(hexColor(0xFFFFFF), 0.0, 1.0);
        scene.add(new Sphere(new Vector(0.0, 1.5, -4.0), 1.5, material));

        scene
            .add(new Cube(new Vector(-1000.0, -1.0, -1000.0), new Vector(1000.0, 0.0, 1000.0), glossyMaterial(hexColor(0xFFFFFF), 1.4, radians(20.0))));
        scene.add(new Sphere(new Vector(0.0, 5.0, 0.0), 1.0, lightMaterial(WHITE, 25.0)));
        var camera = lookAt(new Vector(0.0, 3.0, 6.0), new Vector(0.0, 1.0, 0.0), new Vector(0.0, 1.0, 0.0), 30.0);
        var sampler = new DefaultSampler(16, 16);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/materials%03d.png", iterations);
    }
}
