package examples;

import pt.Box;
import pt.DefaultSampler;
import pt.MatrixFactory;
import pt.Mesh;
import pt.Renderer;
import pt.Sampler;
import pt.Scene;
import pt.TransformedShape;
import pt.Vector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static pt.Camera.lookAt;
import static pt.Color.WHITE;
import static pt.Color.hexColor;
import static pt.Material.glossyMaterial;
import static pt.STL.loadSTL;
import static pt.Util.radians;

public class Toybrick extends BaseExample {

    private static final double H = 1.46875;

    private static final Map<Integer, Integer> COLORS = new HashMap<>();

    static {
        COLORS.put(1, 0xF2F3F2);
        COLORS.put(2, 0xA1A5A2);
        COLORS.put(3, 0xF9E999);
        COLORS.put(5, 0xD7C599);
        COLORS.put(6, 0xC2DAB8);
        COLORS.put(9, 0xE8BAC7);
        COLORS.put(12, 0xCB8442);
        COLORS.put(18, 0xCC8E68);
        COLORS.put(21, 0xC4281B);
        COLORS.put(22, 0xC470A0);
        COLORS.put(23, 0x0D69AB);
        COLORS.put(24, 0xF5CD2F);
        COLORS.put(25, 0x624732);
        COLORS.put(26, 0x1B2A34);
        COLORS.put(27, 0x6D6E6C);
        COLORS.put(28, 0x287F46);
        COLORS.put(29, 0xA1C48B);
        COLORS.put(36, 0xF3CF9B);
        COLORS.put(37, 0x4B974A);
        COLORS.put(38, 0xA05F34);
        COLORS.put(39, 0xC1CADE);
        COLORS.put(45, 0xB4D2E3);
        COLORS.put(100, 0xEEC4B6);
        COLORS.put(101, 0xDA8679);
        COLORS.put(102, 0x6E99C9);
        COLORS.put(103, 0xC7C1B7);
        COLORS.put(104, 0x6B327B);
        COLORS.put(105, 0xE29B3F);
        COLORS.put(106, 0xDA8540);
        COLORS.put(107, 0x008F9B);
        COLORS.put(108, 0x685C43);
        COLORS.put(110, 0x435493);
        COLORS.put(112, 0x6874AC);
        COLORS.put(115, 0xC7D23C);
        COLORS.put(116, 0x55A5AF);
        COLORS.put(118, 0xB7D7D5);
        COLORS.put(119, 0xA4BD46);
        COLORS.put(120, 0xD9E4A7);
        COLORS.put(121, 0xE7AC58);
        COLORS.put(123, 0xD36F4C);
        COLORS.put(124, 0x923978);
        COLORS.put(125, 0xEAB891);
        COLORS.put(127, 0xDCBC81);
        COLORS.put(128, 0xAE7A59);
        COLORS.put(131, 0x9CA3A8);
        COLORS.put(135, 0x74869C);
        COLORS.put(136, 0x877C90);
        COLORS.put(137, 0xE09864);
        COLORS.put(138, 0x958A73);
        COLORS.put(140, 0x203A56);
        COLORS.put(141, 0x27462C);
        COLORS.put(145, 0x7988A1);
        COLORS.put(146, 0x958EA3);
        COLORS.put(147, 0x938767);
        COLORS.put(148, 0x575857);
        COLORS.put(149, 0x161D32);
        COLORS.put(150, 0xABADAC);
        COLORS.put(151, 0x789081);
        COLORS.put(153, 0x957976);
        COLORS.put(154, 0x7B2E2F);
        COLORS.put(168, 0x756C62);
        COLORS.put(180, 0xD7A94B);
        COLORS.put(200, 0x828A5D);
        COLORS.put(190, 0xF9D62E);
        COLORS.put(191, 0xE8AB2D);
        COLORS.put(192, 0x694027);
        COLORS.put(193, 0xCF6024);
        COLORS.put(194, 0xA3A2A4);
        COLORS.put(195, 0x4667A4);
        COLORS.put(196, 0x23478B);
        COLORS.put(198, 0x8E4285);
        COLORS.put(199, 0x635F61);
        COLORS.put(208, 0xE5E4DE);
        COLORS.put(209, 0xB08E44);
        COLORS.put(210, 0x709578);
        COLORS.put(211, 0x79B5B5);
        COLORS.put(212, 0x9FC3E9);
        COLORS.put(213, 0x6C81B7);
        COLORS.put(216, 0x8F4C2A);
        COLORS.put(217, 0x7C5C45);
        COLORS.put(218, 0x96709F);
        COLORS.put(219, 0x6B629B);
        COLORS.put(220, 0xA7A9CE);
        COLORS.put(221, 0xCD6298);
        COLORS.put(222, 0xE4ADC8);
        COLORS.put(223, 0xDC9095);
        COLORS.put(224, 0xF0D5A0);
        COLORS.put(225, 0xEBB87F);
        COLORS.put(226, 0xFDEA8C);
        COLORS.put(232, 0x7DBBDD);
        COLORS.put(268, 0x342B75);
    }

    private Mesh createBrick(int color) throws IOException {
        var material = glossyMaterial(hexColor(COLORS.get(color)), 1.3, radians(20.0));
        var mesh = loadSTL(pathTo("toybrick.stl"), material);
        mesh.smoothNormalsThreshold(radians(20.0));
        mesh.fitInside(new Box(Vector.ZERO, new Vector(2.0, 4.0, 10.0)), new Vector(0.0, 0.0, 0.0));
        return mesh;
    }

    public void run(int iterations) throws IOException {
        var scene = new Scene();
        scene.color = WHITE;
        Mesh[] meshes = {
            createBrick(1),  // white
            createBrick(21), // bright red
            createBrick(23), // bright blue
            createBrick(24), // bright yellow
            createBrick(26), // black
            createBrick(28), // dark green
        };

        var rand = ThreadLocalRandom.current();
        for (var x = -30; x <= 50; x += 2) {
            for (var y = -50; y <= 20; y += 4) {
                var h = rand.nextInt(5) + 1;
                for (var i = 0; i < h; i++) {
                    var dy = 0;
                    if ((x / 2 + i) % 2 == 0) {
                        dy = 2;
                    }
                    var z = (double)i * H;
                    var mesh = meshes[rand.nextInt(meshes.length)];
                    var m = MatrixFactory.translation(new Vector(x, y + dy, z));
                    scene.add(new TransformedShape(mesh, m));
                }
            }
        }
        var camera = lookAt(new Vector(-23.0, 13.0, 20.0), new Vector(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0), 45.0);
        Sampler sampler = new DefaultSampler(4, 4);
        var renderer = new Renderer(scene, camera, sampler, 960, 540);
        renderer.iterativeRender("renders/toybrick%03d.png", iterations);
    }
}
