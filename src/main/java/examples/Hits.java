package examples;

import pt.Box;
import pt.Color;
import pt.Cube;
import pt.DefaultSampler;
import pt.Renderer;
import pt.Sampler;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;
import static pt.STL.loadSTL;
import static pt.Util.radians;

public class Hits extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = diffuseMaterial(new Color(0.95, 0.95, 1.0));
        var light = lightMaterial(WHITE, 300.0);
        scene.add(new Sphere(new Vector(-0.75, -0.75, 5.0), 0.25, light));
        scene.add(new Cube(new Vector(-1000.0, -1000.0, -1000.0), new Vector(1000.0, 1000.0, 0.0), material));
        var mesh = loadSTL(pathTo("hits.stl"), material);
        mesh.smoothNormalsThreshold(radians(10.0));
        mesh.fitInside(new Box(new Vector(-1.0, -1.0, 0.0), new Vector(1.0, 1.0, 2.0)), new Vector(0.5, 0.5, 0.0));
        scene.add(mesh);
        var camera = lookAt(new Vector(1.6, -3.0, 2.0), new Vector(-0.25, 0.5, 0.5), new Vector(0.0, 0.0, 1.0), 50.0);
        Sampler sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/hits%03d.png", iterations);
    }
}
