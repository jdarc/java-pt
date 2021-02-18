package examples;

import pt.DefaultSampler;
import pt.Renderer;
import pt.Scene;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.BLACK;
import static pt.Material.diffuseMaterial;
import static pt.Obj.loadOBJ;

public class Cornell extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        var material = diffuseMaterial(BLACK);
        var mesh = loadOBJ(pathTo("cornellbox-original.obj"), material);
        for (var t : mesh.triangles) {
            scene.add(t);
        }
        var camera = lookAt(new Vector(0.0, 1.0, 3.0), new Vector(0.0, 1.0, 0.0), new Vector(0.0, 1.0, 0.0), 50.0);
        var sampler = new DefaultSampler(4, 8);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/cornell%03d.png", iterations);
    }
}
