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
import static pt.SpecularMode.SPECULAR_MODE_FIRST;
import static pt.Util.radians;

public class Bunny extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = glossyMaterial(hexColor(0xF2EBC7), 1.5, radians(0.0));
        var mesh = loadOBJ(pathTo("bunny.obj"), material);
        mesh.smoothNormals();
        mesh.fitInside(new Box(new Vector(-1.0, 0.0, -1.0), new Vector(1.0, 2.0, 1.0)), new Vector(0.5, 0.0, 0.5));
        scene.add(mesh);
        var floor = glossyMaterial(hexColor(0x33332D), 1.2, radians(20.0));
        scene.add(new Cube(new Vector(-10000.0, -10000.0, -10000.0), new Vector(10000.0, 0.0, 10000.0), floor));
        scene.add(new Sphere(new Vector(0.0, 5.0, 0.0), 1.0, lightMaterial(WHITE, 20.0)));
        scene.add(new Sphere(new Vector(4.0, 5.0, 4.0), 1.0, lightMaterial(WHITE, 20.0)));
        var camera = lookAt(new Vector(-1.0, 2.0, 3.0), new Vector(0.0, 0.75, 0.0), new Vector(0.0, 1.0, 0.0), 50.0);
        var sampler = new DefaultSampler(4, 4);
        sampler.specularMode = SPECULAR_MODE_FIRST;
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.fireflySamples = 128;
        renderer.iterativeRender("renders/bunny%03d.png", iterations);
    }
}
