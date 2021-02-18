package examples;

import pt.DefaultSampler;
import pt.Material;
import pt.Mesh;
import pt.Renderer;
import pt.Scene;
import pt.Sphere;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.LightMode.LIGHT_MODE_ALL;
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;
import static pt.Material.metallicMaterial;
import static pt.Obj.loadOBJ;
import static pt.SpecularMode.SPECULAR_MODE_ALL;
import static pt.Util.radians;

public class VeachScene extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();

        Material material;
        Mesh mesh;

        material = diffuseMaterial(WHITE);
        mesh = loadOBJ(pathTo("veach_scene/backdrop.obj"), material);
        scene.add(mesh);

        material = metallicMaterial(WHITE, radians(20.0), 0.0);
        mesh = loadOBJ(pathTo("veach_scene/bar0.obj"), material);
        scene.add(mesh);

        material = metallicMaterial(WHITE, radians(15.0), 0.0);
        mesh = loadOBJ(pathTo("veach_scene/bar1.obj"), material);
        scene.add(mesh);

        material = metallicMaterial(WHITE, radians(10.0), 0.0);
        mesh = loadOBJ(pathTo("veach_scene/bar2.obj"), material);
        scene.add(mesh);

        material = metallicMaterial(WHITE, radians(5.0), 0.0);
        mesh = loadOBJ(pathTo("veach_scene/bar3.obj"), material);
        scene.add(mesh);

        material = metallicMaterial(WHITE, radians(0.0), 0.0);
        mesh = loadOBJ(pathTo("veach_scene/bar4.obj"), material);
        scene.add(mesh);

        scene.add(new Sphere(new Vector(3.75, 4.281, 0.0), 1.8 / 2.0, lightMaterial(WHITE, 3.0)));
        scene.add(new Sphere(new Vector(1.25, 4.281, 0.0), 0.6 / 2.0, lightMaterial(WHITE, 9.0)));
        scene.add(new Sphere(new Vector(-1.25, 4.281, 0.0), 0.2 / 2.0, lightMaterial(WHITE, 27.0)));
        scene.add(new Sphere(new Vector(-3.75, 4.281, 0.0), 0.066 / 2.0, lightMaterial(WHITE, 81.803)));

        scene.add(new Sphere(new Vector(0.0, 10.0, 4.0), 1.0, lightMaterial(WHITE, 50.0)));

        var camera = lookAt(new Vector(0.0, 5.0, 12.0), new Vector(0.0, 1.0, 0.0), new Vector(0.0, 1.0, 0.0), 50.0);
        var sampler = new DefaultSampler(4, 8);
        sampler.specularMode = SPECULAR_MODE_ALL;
        sampler.lightMode = LIGHT_MODE_ALL;
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/veach%03d.png", iterations);
    }
}
