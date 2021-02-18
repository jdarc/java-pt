package examples;

import pt.Cube;
import pt.DefaultSampler;
import pt.MatrixFactory;
import pt.Renderer;
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
import static pt.ColorTexture.getTexture;
import static pt.Material.glossyMaterial;
import static pt.Util.radians;

public class Go extends BaseExample {

    private static final double[][] BLACK_POSITIONS = new double[][]{
        {7.0, 3.0}, {14.0, 17.0}, {14.0, 4.0}, {18.0, 4.0}, {0.0, 7.0}, {5.0, 8.0}, {11.0, 5.0}, {10.0, 7.0}, {7.0, 6.0}, {6.0, 10.0}, {12.0, 6.0}, {3.0, 2.0}, {5.0, 11.0}, {7.0, 5.0}, {14.0, 15.0}, {12.0, 11.0}, {8.0, 12.0},
        {4.0, 15.0}, {2.0, 11.0}, {9.0, 9.0}, {10.0, 3.0}, {6.0, 17.0}, {7.0, 2.0}, {14.0, 5.0}, {13.0, 3.0}, {13.0, 16.0}, {3.0, 6.0}, {1.0, 10.0}, {4.0, 1.0}, {10.0, 9.0}, {5.0, 17.0}, {12.0, 7.0}, {3.0, 5.0}, {2.0, 7.0},
        {5.0, 10.0}, {10.0, 10.0}, {5.0, 7.0}, {7.0, 4.0}, {12.0, 4.0}, {8.0, 13.0}, {9.0, 8.0}, {15.0, 17.0}, {3.0, 10.0}, {4.0, 13.0}, {2.0, 13.0}, {8.0, 16.0}, {12.0, 3.0}, {17.0, 5.0}, {13.0, 2.0}, {15.0, 3.0},
        {2.0, 3.0}, {6.0, 5.0}, {11.0, 7.0}, {16.0, 5.0}, {11.0, 8.0}, {14.0, 7.0}, {15.0, 6.0}, {1.0, 7.0}, {5.0, 9.0}, {10.0, 11.0}, {6.0, 6.0}, {4.0, 18.0}, {7.0, 14.0}, {17.0, 3.0}, {4.0, 9.0}, {10.0, 12.0}, {6.0, 3.0},
        {16.0, 7.0}, {14.0, 14.0}, {16.0, 18.0}, {3.0, 13.0}, {1.0, 13.0}, {2.0, 10.0}, {7.0, 9.0}, {13.0, 1.0}, {12.0, 15.0}, {4.0, 3.0}, {5.0, 2.0}, {10.0, 2.0}
    };

    private static final double[][] WHITE_POSITIONS = new double[][]{
        {16.0, 6.0}, {16.0, 9.0}, {13.0, 4.0}, {1.0, 6.0}, {0.0, 10.0}, {3.0, 7.0}, {1.0, 11.0}, {8.0, 5.0}, {6.0, 7.0}, {5.0, 5.0}, {15.0, 11.0}, {13.0, 7.0}, {18.0, 9.0}, {2.0, 6.0}, {7.0, 10.0}, {15.0, 14.0}, {13.0, 10.0},
        {17.0, 18.0}, {7.0, 15.0}, {5.0, 14.0}, {3.0, 18.0}, {15.0, 16.0}, {14.0, 8.0}, {12.0, 8.0}, {7.0, 13.0}, {1.0, 15.0}, {8.0, 9.0}, {6.0, 14.0}, {12.0, 2.0}, {17.0, 6.0}, {18.0, 5.0}, {17.0, 11.0}, {9.0, 7.0}, {6.0, 4.0},
        {5.0, 4.0}, {6.0, 11.0}, {11.0, 9.0}, {13.0, 6.0}, {18.0, 6.0}, {0.0, 8.0}, {8.0, 3.0}, {4.0, 6.0}, {9.0, 2.0}, {4.0, 17.0}, {14.0, 12.0}, {13.0, 9.0}, {18.0, 11.0}, {3.0, 15.0}, {4.0, 8.0}, {2.0, 8.0}, {12.0, 9.0},
        {16.0, 17.0}, {8.0, 10.0}, {9.0, 11.0}, {17.0, 7.0}, {16.0, 11.0}, {14.0, 10.0}, {3.0, 9.0}, {1.0, 9.0}, {8.0, 7.0}, {2.0, 14.0}, {9.0, 6.0}, {5.0, 3.0}, {14.0, 16.0}, {5.0, 16.0}, {16.0, 8.0}, {13.0, 5.0}, {8.0, 4.0},
        {4.0, 7.0}, {5.0, 6.0}, {11.0, 2.0}, {12.0, 5.0}, {15.0, 8.0}, {2.0, 9.0}, {9.0, 15.0}, {8.0, 1.0}, {4.0, 4.0}, {16.0, 15.0}, {12.0, 10.0}, {13.0, 11.0}, {2.0, 16.0}, {4.0, 14.0}, {5.0, 15.0}, {10.0, 1.0},
        {6.0, 8.0}, {6.0, 12.0}, {17.0, 9.0}, {8.0, 8.0},
        };

    private static boolean intersects(Scene scene, Shape shape) {
        var box = shape.boundingBox();
        for (var other : scene.shapes) {
            if (box.intersects(other.boundingBox())) {
                return true;
            }
        }
        return false;
    }

    private static Vector offset(double stdev) {
        var rand = ThreadLocalRandom.current();
        var a = rand.nextDouble() * 2.0 * Math.PI;
        var r = rand.nextGaussian() * stdev;
        var x = Math.cos(a) * r;
        var y = Math.sin(a) * r;
        return new Vector(x, 0.0, y);
    }

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.color = WHITE;
        var black = glossyMaterial(hexColor(0x111111), 1.5, radians(45.0));
        var white = glossyMaterial(hexColor(0xFFFFFF), 1.6, radians(20.0));
        for (var p : BLACK_POSITIONS) {
            while (true) {
                var m = MatrixFactory.scaling(new Vector(0.48, 0.2, 0.48)).translate(new Vector(p[0] - 9.5, 0.0, p[1] - 9.5));
                m = m.translate(offset(0.02));
                var shape = new TransformedShape(new Sphere(Vector.ZERO, 1.0, black), m);
                if (intersects(scene, shape)) {
                    continue;
                }
                scene.add(shape);
                break;
            }
        }
        for (var p : WHITE_POSITIONS) {
            while (true) {
                var m = MatrixFactory.scaling(new Vector(0.48, 0.2, 0.48)).translate(new Vector(p[0] - 9.5, 0.0, p[1] - 9.5));
                m = m.translate(offset(0.02));
                var shape = new TransformedShape(new Sphere(Vector.ZERO, 1.0, white), m);
                if (intersects(scene, shape)) {
                    continue;
                }
                scene.add(shape);
                break;
            }
        }
        for (var i = 0; i < 19; i++) {
            var x = (double)i - 9.5;
            var m = 0.015;
            scene.add(new Cube(new Vector(x - m, -1.0, -9.5), new Vector(x + m, -0.195, 8.5), black));
            scene.add(new Cube(new Vector(-9.5, -1.0, x - m), new Vector(8.5, -0.195, x + m), black));
        }
        var material = glossyMaterial(hexColor(0xEFECCA), 1.2, radians(30.0));
        material.texture = getTexture("wood.jpg");
        scene.add(new Cube(new Vector(-12.0, -12.0, -12.0), new Vector(12.0, -0.2, 12.0), material));
        scene.texture = getTexture("courtyard_1k.jpg");
        var camera = lookAt(new Vector(-0.5, 5.0, 5.0), new Vector(-0.5, 0.0, 0.5), new Vector(0.0, 1.0, 0.0), 50.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/go%03d.png", iterations);
    }
}
