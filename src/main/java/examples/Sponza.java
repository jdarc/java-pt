package examples;

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

public class Sponza extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = glossyMaterial(hexColor(0xFCFAE1), 1.5, radians(20.0));
        var mesh = loadOBJ(pathTo("dabrovic-sponza/sponza.obj"), material);
        mesh.moveTo(Vector.ZERO, new Vector(0.5, 0.0, 0.5));
        scene.add(mesh);
        scene.add(new Sphere(new Vector(0.0, 20.0, 0.0), 3.0, lightMaterial(WHITE, 100.0)));
        var camera = lookAt(new Vector(-10.0, 2.0, 0.0), new Vector(0.0, 4.0, 0.0), new Vector(0.0, 1.0, 0.0), 45.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/sponze%03d.png", iterations);
    }
}
