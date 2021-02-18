package examples;

import pt.Box;
import pt.Color;
import pt.Cube;
import pt.DefaultSampler;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.STL.loadSTL;
import static pt.Util.radians;

public class Love extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = glossyMaterial(hexColor(0xF2F2F2), 1.5, radians(20.0));
        scene.add(new Cube(new Vector(-100.0, -1.0, -100.0), new Vector(100.0, 0.0, 100.0), material));
        var heart = glossyMaterial(hexColor(0xF60A20), 1.5, radians(20.0));
        var mesh = loadSTL(pathTo("love.stl"), heart);
        mesh.fitInside(new Box(new Vector(-0.5, 0.0, -0.5), new Vector(0.5, 1.0, 0.5)), new Vector(0.5, 0.0, 0.5));
        scene.add(mesh);
        scene.add(new Sphere(new Vector(-2.0, 10.0, 2.0), 1.0, lightMaterial(Color.WHITE, 30.0)));
        scene.add(new Sphere(new Vector(0.0, 10.0, 2.0), 1.0, lightMaterial(Color.WHITE, 30.0)));
        scene.add(new Sphere(new Vector(2.0, 10.0, 2.0), 1.0, lightMaterial(Color.WHITE, 30.0)));
        var camera = lookAt(new Vector(0.0, 1.5, 2.0), new Vector(0.0, 0.5, 0.0), new Vector(0.0, 1.0, 0.0), 35.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/love%03d.png", iterations);
    }
}
