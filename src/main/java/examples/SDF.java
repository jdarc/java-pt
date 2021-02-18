package examples;

import pt.CubeSDF;
import pt.CylinderSDF;
import pt.DefaultSampler;
import pt.DifferenceSDF;
import pt.IntersectionSDF;
import pt.Plane;
import pt.Renderer;
import pt.SDFShape;
import pt.Scene;
import pt.Sphere;
import pt.SphereSDF;
import pt.TransformSDF;
import pt.Vector;

import java.io.IOException;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.LightMode.LIGHT_MODE_ALL;
import static pt.Material.glossyMaterial;
import static pt.Material.lightMaterial;
import static pt.MatrixFactory.rotation;
import static pt.SpecularMode.SPECULAR_MODE_ALL;
import static pt.Util.radians;

public class SDF extends BaseExample {

    public void run(int iterations) throws IOException {
        var scene = new Scene();

        var light = lightMaterial(WHITE, 180.0);

        var d = 4.0;
        scene.add(new Sphere(new Vector(-1.0, -1.0, 0.5).normalize().mulScalar(d), 0.25, light));
        scene.add(new Sphere(new Vector(0.0, -1.0, 0.25).normalize().mulScalar(d), 0.25, light));
        scene.add(new Sphere(new Vector(-1.0, 1.0, 0.0).normalize().mulScalar(d), 0.25, light));

        var material = glossyMaterial(hexColor(0x468966), 1.2, radians(20.0));
        var sphere = new SphereSDF(0.65);
        var cube = new CubeSDF(new Vector(1.0, 1.0, 1.0));
        var roundedCube = new IntersectionSDF(sphere, cube);
        var a = new CylinderSDF(0.25, 1.1);
        var b = new TransformSDF(a, rotation(new Vector(1.0, 0.0, 0.0), radians(90.0)));
        var c = new TransformSDF(a, rotation(new Vector(0.0, 0.0, 1.0), radians(90.0)));
        var difference = new DifferenceSDF(roundedCube, a, b, c);
        var sdf = new TransformSDF(difference, rotation(new Vector(0.0, 0.0, 1.0), radians(30.0)));
        scene.add(new SDFShape(sdf, material));

        var floor = glossyMaterial(hexColor(0xFFF0A5), 1.2, radians(20.0));
        scene.add(new Plane(new Vector(0.0, 0.0, -0.5), new Vector(0.0, 0.0, 1.0), floor));

        var camera = lookAt(new Vector(-3.0, 0.0, 1.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), 35.0);
        var sampler = new DefaultSampler(4, 4);
        sampler.lightMode = LIGHT_MODE_ALL;
        sampler.specularMode = SPECULAR_MODE_ALL;
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/sdf%03d.png", iterations);
    }
}
