package examples;

import pt.Box;
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
import static pt.Obj.loadOBJ;
import static pt.Util.radians;

public class Dragon extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();

        var material = glossyMaterial(hexColor(0xB7CA79), 1.5, radians(20.0));
        var mesh = loadOBJ(pathTo("dragon.obj"), material);
        mesh.fitInside(new Box(new Vector(-1.0, 0.0, -1.0), new Vector(1.0, 2.0, 1.0)), new Vector(0.5, 0.0, 0.5));
        scene.add(mesh);

        var floor = glossyMaterial(hexColor(0xD8CAA8), 1.2, radians(5.0));
        scene.add(new Cube(new Vector(-50.0, -50.0, -50.0), new Vector(50.0, 0.0, 50.0), floor));

        var light = lightMaterial(WHITE, 75.0);
        scene.add(new Sphere(new Vector(-1.0, 10.0, 0.0), 1.0, light));

        var mouth = lightMaterial(hexColor(0xFFFAD5), 500.0);
        scene.add(new Sphere(new Vector(-0.05, 1.0, -0.5), 0.03, mouth));

        var camera = lookAt(new Vector(-3.0, 2.0, -1.0), new Vector(0.0, 0.6, -0.1), new Vector(0.0, 1.0, 0.0), 35.0);
        camera.setFocus(new Vector(0.0, 1.0, -0.5), 0.03);
        var sampler = new DefaultSampler(4, 8);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/dragon%03d.png", iterations);
    }
}
