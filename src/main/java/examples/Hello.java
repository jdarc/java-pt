package examples;

import pt.DefaultSampler;
import pt.Plane;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;

public class Hello extends BaseExample {

    public void run(int iterations) throws IOException {
        // create a scene
        var scene = new Scene();

        // create a material
        var material = diffuseMaterial(WHITE);

        // add the floor (a plane)
        var plane = new Plane(new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), material);
        scene.add(plane);

        // add the ball (a sphere)
        var sphere = new Sphere(new Vector(0.0, 0.0, 1.0), 1.0, material);
        scene.add(sphere);

        // add a spherical light source
        var light = new Sphere(new Vector(0.0, 0.0, 5.0), 1.0, lightMaterial(WHITE, 8.0));
        scene.add(light);

        // position the camera
        var camera = lookAt(new Vector(3.0, 3.0, 3.0), new Vector(0.0, 0.0, 0.5), new Vector(0.0, 0.0, 1.0), 50.0);

        // render the scene with progressive refinement
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.adaptiveSamples = 128;
        renderer.iterativeRender("renders/hello%03d.png", iterations);
    }
}
