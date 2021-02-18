package examples;

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
import static pt.Color.kelvin;
import static pt.Material.diffuseMaterial;
import static pt.Material.lightMaterial;

public class Runway extends BaseExample {

    public void run(int iterations) throws IOException {
        var radius = 2.0;
        var height = 3.0;
        var emission = 3.0;

        var scene = new Scene();

        var white = diffuseMaterial(WHITE);
        var floor = new Cube(new Vector(-250.0, -1500.0, -1.0), new Vector(250.0, 6200.0, 0.0), white);
        scene.add(floor);

        var light = lightMaterial(kelvin(2700.0), emission);
        for (var y = 0; y <= 6000; y += 40) {
            scene.add(new Sphere(new Vector(-100.0, y, height), radius, light));
            scene.add(new Sphere(new Vector(0.0, y, height), radius, light));
            scene.add(new Sphere(new Vector(100.0, y, height), radius, light));
        }

        for (var y = -40; y >= -750; y -= 20) {
            scene.add(new Sphere(new Vector(-10.0, y, height), radius, light));
            scene.add(new Sphere(new Vector(0.0, y, height), radius, light));
            scene.add(new Sphere(new Vector(10.0, y, height), radius, light));
        }

        var green = lightMaterial(hexColor(0x0BDB46), emission);
        var red = lightMaterial(hexColor(0xDC4522), emission);
        for (var x = -160; x <= 160; x += 10) {
            scene.add(new Sphere(new Vector(x, -20.0, height), radius, green));
            scene.add(new Sphere(new Vector(x, 6100.0, height), radius, red));
        }

        scene.add(new Sphere(new Vector(-160.0, 250.0, height), radius, red));
        scene.add(new Sphere(new Vector(-180.0, 250.0, height), radius, red));
        scene.add(new Sphere(new Vector(-200.0, 250.0, height), radius, light));
        scene.add(new Sphere(new Vector(-220.0, 250.0, height), radius, light));

        for (var i = 0; i < 5; i++) {
            for (var j = 1; j <= 4; j++) {
                var x = (double)(j + 4) * 7.5;
                scene.add(new Sphere(new Vector(x, (i + 1) * -120, height), radius, red));
                scene.add(new Sphere(new Vector(-x, (i + 1) * -120, height), radius, red));
                scene.add(new Sphere(new Vector(x, -(double)((i + 1) * -120), height), radius, light));
                scene.add(new Sphere(new Vector(-x, -(double)((i + 1) * -120), height), radius, light));
            }
        }

        var camera = lookAt(new Vector(0.0, -1500.0, 200.0), new Vector(0.0, -100.0, 0.0), new Vector(0.0, 0.0, 1.0), 20.0);
        camera.setFocus(new Vector(0.0, 20000.0, 0.0), 1.0);

        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/runway%03d.png", iterations);
    }
}
