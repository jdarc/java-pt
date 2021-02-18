package examples;

import pt.Box;
import pt.DefaultSampler;
import pt.Material;
import pt.MatrixFactory;
import pt.Renderer;
import pt.Scene;
import pt.Shape;
import pt.Sphere;
import pt.TransformedShape;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.STL.loadSTL;
import static pt.Util.radians;

public class Cylinder extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        Shape[] meshes = {
            createMesh(glossyMaterial(hexColor(0x730046), 1.6, radians(45.0))),
            createMesh(glossyMaterial(hexColor(0xBFBB11), 1.6, radians(45.0))),
            createMesh(glossyMaterial(hexColor(0xFFC200), 1.6, radians(45.0))),
            createMesh(glossyMaterial(hexColor(0xE88801), 1.6, radians(45.0))),
            createMesh(glossyMaterial(hexColor(0xC93C00), 1.6, radians(45.0))),
            };

        for (var x = -6; x <= 3; x++) {
            var mesh = meshes[(x + 6) % meshes.length];
            for (var y = -5; y <= 4; y++) {
                var fx = (double)x / 2.0;
                var fz = (double)x / 2.0;
                scene.add(new TransformedShape(mesh, MatrixFactory.translation(new Vector(fx, y, fz))));
            }
        }
        scene.add(new Sphere(new Vector(1.0, 0.0, 10.0), 3.0, lightMaterial(WHITE, 20.0)));
        var camera = lookAt(new Vector(-5.0, 0.0, 5.0), new Vector(1.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), 45.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/cylinder%03d.png", iterations);
    }

    private Shape createMesh(Material material) throws IOException {
        var mesh = loadSTL(pathTo("cylinder.stl"), material);
        if (mesh != null) {
            mesh.fitInside(new Box(new Vector(-0.1, -0.1, 0.0), new Vector(1.1, 1.1, 100.0)), new Vector(0.5, 0.5, 0.0));
            mesh.smoothNormalsThreshold(radians(10.0));
        }
        return mesh;
    }
}
