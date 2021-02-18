package examples;

import pt.Box;
import pt.Color;
import pt.DefaultSampler;
import pt.Material;
import pt.MatrixFactory;
import pt.Renderer;
import pt.Sampler;
import pt.Scene;
import pt.Shape;
import pt.Sphere;
import pt.TransformedShape;
import pt.Vector;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.STL.loadSTL;
import static pt.Util.radians;

public class Cubes extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        Shape[] meshes = {
            createMesh(glossyMaterial(hexColor(0x3B596A), 1.5, radians(20.0))),
            createMesh(glossyMaterial(hexColor(0x427676), 1.5, radians(20.0))),
            createMesh(glossyMaterial(hexColor(0x3F9A82), 1.5, radians(20.0))),
            createMesh(glossyMaterial(hexColor(0xA1CD73), 1.5, radians(20.0))),
            createMesh(glossyMaterial(hexColor(0xECDB60), 1.5, radians(20.0))),
            };
        var rnd = ThreadLocalRandom.current();
        for (var x = -8; x <= 8; x++) {
            for (var z = -12; z <= 12; z++) {
                var fy = rnd.nextDouble() * 2.0;
                scene.add(new TransformedShape(meshes[rnd.nextInt(meshes.length)], MatrixFactory.translation(new Vector(x, fy, z))));
                scene.add(new TransformedShape(meshes[rnd.nextInt(meshes.length)], MatrixFactory.translation(new Vector(x, fy - 1.0, z))));
            }
        }
        scene.add(new Sphere(new Vector(8.0, 10.0, 0.0), 3.0, lightMaterial(new Color(WHITE), 30.0)));
        var camera = lookAt(new Vector(-10.0, 10.0, 0.0), new Vector(-2.0, 0.0, 0.0), new Vector(0.0, 1.0, 0.0), 45.0);
        Sampler sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/cubes%03d.png", iterations);
    }

    private Shape createMesh(Material material) throws IOException {
        var mesh = loadSTL(pathTo("cube.stl"), material);
        mesh.fitInside(new Box(new Vector(0.0, 0.0, 0.0), new Vector(1.0, 1.0, 1.0)), new Vector(0.5, 0.5, 0.5));
        return mesh;
    }
}
