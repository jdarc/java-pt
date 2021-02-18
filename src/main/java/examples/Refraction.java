package examples;

import pt.DefaultSampler;
import pt.MatrixFactory;
import pt.Plane;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Material.clearMaterial;
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;
import static pt.STL.loadSTL;
import static pt.SpecularMode.SPECULAR_MODE_ALL;

public class Refraction extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();

        var glass = clearMaterial(1.5, 0.0);

        // add a sphere primitive
        scene.add(new Sphere(new Vector(-1.5, 0.0, 0.5), 1.0, glass));

        // add a mesh sphere
        var mesh = loadSTL(pathTo("sphere.stl"), glass);
        mesh.smoothNormals();
        mesh.transform(MatrixFactory.translation(new Vector(1.5, 0.0, 0.5)));
        scene.add(mesh);

        // add the floor
        scene.add(new Plane(new Vector(0.0, 0.0, -1.0), new Vector(0.0, 0.0, 1.0), diffuseMaterial(WHITE)));

        // add the light
        scene.add(new Sphere(new Vector(0.0, 0.0, 5.0), 1.0, lightMaterial(WHITE, 30.0)));

        var camera = lookAt(new Vector(0.0, -5.0, 5.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), 50.0);
        var sampler = new DefaultSampler(16, 8);
        sampler.specularMode = SPECULAR_MODE_ALL;
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/refraction%03d.png", iterations);
    }
}
