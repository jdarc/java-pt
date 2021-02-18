package examples;

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
import static pt.Color.hexColor;
import static pt.Material.lightMaterial;
import static pt.Material.specularMaterial;
import static pt.Obj.loadOBJ;

public class Teapot extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.add(new Sphere(new Vector(-2.0, 5.0, -3.0), 0.5, lightMaterial(WHITE, 50.0)));
        scene.add(new Sphere(new Vector(5.0, 5.0, -3.0), 0.5, lightMaterial(WHITE, 50.0)));
        scene.add(new Cube(new Vector(-30.0, -1.0, -30.0), new Vector(30.0, 0.0, 30.0), specularMaterial(hexColor(0xFCFAE1), 2.0)));
        var mesh = loadOBJ(pathTo("teapot.obj"), specularMaterial(hexColor(0xB9121B), 2.0));
        scene.add(mesh);
        var camera = lookAt(new Vector(2.0, 5.0, -6.0), new Vector(0.5, 1.0, 0.0), new Vector(0.0, 1.0, 0.0), 45.0);
        Sampler sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/teapot%03d.png", iterations);
    }
}
