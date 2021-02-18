package examples;

import pt.DefaultSampler;
import pt.Material;
import pt.Mesh;
import pt.Renderer;
import pt.Scene;
import pt.Triangle;
import pt.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.ColorTexture.loadTexture;
import static pt.Material.glossyMaterial;
import static pt.Util.radians;

public class Craft extends BaseExample {

    private static final int N = 16;
    private static final double A = 1.0 / 2048.0;
    private static final double B = 1.0 / (double)N - A;

    private static final int[] DIRT = {0, 0, 0, 0, 0, 0};
    private static final int[] GRASS = {16, 32, 16, 0, 16, 16};

    private static final Vector[] VP = {
        new Vector(-0.5, -0.5, 0.5),
        new Vector(0.5, -0.5, 0.5),
        new Vector(-0.5, 0.5, 0.5),
        new Vector(0.5, 0.5, 0.5),
        new Vector(-0.5, 0.5, -0.5),
        new Vector(0.5, 0.5, -0.5),
        new Vector(-0.5, -0.5, -0.5),
        new Vector(0.5, -0.5, -0.5),
        };

    private static final Vector[] VT = {
        new Vector(A, A, 0.0),
        new Vector(B, A, 0.0),
        new Vector(A, B, 0.0),
        new Vector(B, B, 0.0),
        };

    private static final Vector[][][] TRIANGLES = {
        {new Vector[]{VP[0], VT[0]}, new Vector[]{VP[1], VT[1]}, new Vector[]{VP[2], VT[2]}},
        {new Vector[]{VP[2], VT[2]}, new Vector[]{VP[1], VT[1]}, new Vector[]{VP[3], VT[3]}},
        {new Vector[]{VP[2], VT[0]}, new Vector[]{VP[3], VT[1]}, new Vector[]{VP[4], VT[2]}},
        {new Vector[]{VP[4], VT[2]}, new Vector[]{VP[3], VT[1]}, new Vector[]{VP[5], VT[3]}},
        {new Vector[]{VP[4], VT[3]}, new Vector[]{VP[5], VT[2]}, new Vector[]{VP[6], VT[1]}},
        {new Vector[]{VP[6], VT[1]}, new Vector[]{VP[5], VT[2]}, new Vector[]{VP[7], VT[0]}},
        {new Vector[]{VP[6], VT[0]}, new Vector[]{VP[7], VT[1]}, new Vector[]{VP[0], VT[2]}},
        {new Vector[]{VP[0], VT[2]}, new Vector[]{VP[7], VT[1]}, new Vector[]{VP[1], VT[3]}},
        {new Vector[]{VP[1], VT[0]}, new Vector[]{VP[7], VT[1]}, new Vector[]{VP[3], VT[2]}},
        {new Vector[]{VP[3], VT[2]}, new Vector[]{VP[7], VT[1]}, new Vector[]{VP[5], VT[3]}},
        {new Vector[]{VP[6], VT[0]}, new Vector[]{VP[0], VT[1]}, new Vector[]{VP[4], VT[2]}},
        {new Vector[]{VP[4], VT[2]}, new Vector[]{VP[0], VT[1]}, new Vector[]{VP[2], VT[3]}},
        };

    private List<Triangle> block(Vector p, Material material, int[] tiles) {
        List<Triangle> result = new ArrayList<>();
        for (var i = 0; i < TRIANGLES.length; i++) {
            var t = TRIANGLES[i];
            var tile = tiles[i / 2];
            var m = new Vector((double)(tile % N) / (double)N, (double)(tile / N) / (double)N, 0.0);
            var v1 = t[0][0];
            var v2 = t[1][0];
            var v3 = t[2][0];
            v1 = v1.add(p);
            v2 = v2.add(p);
            v3 = v3.add(p);
            var t1 = t[0][1];
            var t2 = t[1][1];
            var t3 = t[2][1];
            t1 = t1.add(m);
            t2 = t2.add(m);
            t3 = t3.add(m);
            result.add(new Triangle(v1, v2, v3, t1, t2, t3, material));
        }
        return result;
    }

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.color = WHITE;
        var texture = loadTexture(pathTo("texture.png"));
        var material = glossyMaterial(hexColor(0xFCFAE1), 1.1, radians(20.0));
        material.texture = texture;
        List<Triangle> triangles = new ArrayList<>();
        for (var x = -10; x <= 10; x++) {
            for (var z = -10; z <= 10; z++) {
                var h = ThreadLocalRandom.current().nextInt(4);
                for (var y = 0; y <= h; y++) {
                    var p = new Vector(x, y, z);
                    var tiles = DIRT;
                    if (y == h) {
                        tiles = GRASS;
                    }
                    var cube = block(p, material, tiles);
                    triangles.addAll(cube);
                }
            }
        }
        var mesh = new Mesh(triangles.toArray(new Triangle[0]));
        scene.add(mesh);
        var camera = lookAt(new Vector(-13.0, 11.0, -7.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 1.0, 0.0), 45.0);
        var sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/craft%03d.png", iterations);
    }
}
