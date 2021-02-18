package examples;

import pt.Box;
import pt.Camera;
import pt.Cube;
import pt.DefaultSampler;
import pt.MatrixFactory;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Color.BLACK;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.Obj.loadOBJ;
import static pt.Util.radians;

public class Gopher extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();

        // create materials
        var gopher = glossyMaterial(BLACK, 1.2, radians(30.0));
        var wall = glossyMaterial(hexColor(0xFCFAE1), 1.5, radians(10.0));
        var light = lightMaterial(WHITE, 80.0);

        // add walls and lights
        scene.add(new Cube(new Vector(-10.0, -1.0, -10.0), new Vector(-2.0, 10.0, 10.0), wall));
        scene.add(new Cube(new Vector(-10.0, -1.0, -10.0), new Vector(10.0, 0.0, 10.0), wall));
        scene.add(new Sphere(new Vector(4.0, 10.0, 1.0), 1.0, light));

        // load and transform gopher mesh
        var mesh = loadOBJ(pathTo("gopher.obj"), gopher);
        mesh.transform(MatrixFactory.rotation(new Vector(0.0, 1.0, 0.0), radians(-10.0)));
        mesh.smoothNormals();
        mesh.fitInside(new Box(new Vector(-1.0, 0.0, -1.0), new Vector(1.0, 2.0, 1.0)), new Vector(0.5, 0.0, 0.5));
        scene.add(mesh);

        // position camera
        var camera = Camera.lookAt(new Vector(4.0, 1.0, 0.0), new Vector(0.0, 0.9, 0.0), new Vector(0.0, 1.0, 0.0), 40.0);

        // render the scene
        var sampler = new DefaultSampler(16, 16);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/gopher%03d.png", iterations);
    }
}
