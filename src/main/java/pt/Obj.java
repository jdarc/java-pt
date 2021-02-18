package pt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Obj {

    public static Mesh loadOBJ(String path, Material parent) throws IOException {
        System.out.println(String.format("Loading OBJ: %s", path));
        var file = new File(path);
        var triangles = new ArrayList<Triangle>();
        try (var br = new BufferedReader(new FileReader(file))) {
            var vs = new ArrayList<Vector>();
            vs.add(Vector.ZERO);
            var vts = new ArrayList<Vector>();
            vts.add(Vector.ZERO);
            var vns = new ArrayList<Vector>();
            vns.add(Vector.ZERO);
            Map<String, Material> materials = new HashMap<>();
            var material = new Material(parent);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("#")) {
                    continue;
                }
                var fields = line.split("\\s+");
                if (fields.length == 0) {
                    continue;
                }
                var keyword = fields[0];
                switch (keyword) {
                    case "mtllib":
                        var p = Paths.get(file.getParent(), fields[1]).toString();
                        loadMTL(p, parent, materials);
                        break;
                    case "usemtl":
                        if (materials.containsKey(fields[1])) {
                            material = materials.get(fields[1]);
                        }
                        break;
                    case "v": {
                        var x = Double.parseDouble(fields[1]);
                        var y = Double.parseDouble(fields[2]);
                        var z = Double.parseDouble(fields[3]);
                        vs.add(new Vector(x, y, z));
                        break;
                    }
                    case "vt": {
                        var x = Double.parseDouble(fields[1]);
                        var y = Double.parseDouble(fields[2]);
                        vts.add(new Vector(x, y, 0.0));
                        break;
                    }
                    case "vn":
                        var x = Double.parseDouble(fields[1]);
                        var y = Double.parseDouble(fields[2]);
                        var z = Double.parseDouble(fields[3]);
                        vns.add(new Vector(x, y, z));
                        break;
                    case "f":
                        var fvs = new int[fields.length - 1];
                        var fvts = new int[fields.length - 1];
                        var fvns = new int[fields.length - 1];
                        for (var i = 1; i < fields.length; i++) {
                            var arg = fields[i];
                            var vertex = (arg + "//").split("/", -1);
                            fvs[i - 1] = parseIndex(vertex[0], vs.size());
                            fvts[i - 1] = parseIndex(vertex[1], vts.size());
                            fvns[i - 1] = parseIndex(vertex[2], vns.size());
                        }
                        for (var i = 1; i < fvs.length - 1; i++) {
                            var t = new Triangle();
                            t.material = material;
                            t.v1 = new Vector(vs.get(fvs[0]));
                            t.v2 = new Vector(vs.get(fvs[i]));
                            t.v3 = new Vector(vs.get(fvs[i + 1]));
                            t.t1 = new Vector(vts.get(fvts[0]));
                            t.t2 = new Vector(vts.get(fvts[i]));
                            t.t3 = new Vector(vts.get(fvts[i + 1]));
                            t.n1 = new Vector(vns.get(fvns[0]));
                            t.n2 = new Vector(vns.get(fvns[i]));
                            t.n3 = new Vector(vns.get(fvns[i + 1]));
                            t.fixNormals();
                            triangles.add(t);
                        }
                        break;
                }
            }
        }
        return new Mesh(triangles.toArray(new Triangle[0]));
    }

    private static void loadMTL(String path, Material parent, Map<String, Material> materials) throws IOException {
        System.out.println(String.format("Loading MTL: %s", path));
        var file = new File(path);
        try (var br = new BufferedReader(new FileReader(file))) {
            var material = new Material(parent);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("#")) {
                    continue;
                }
                var fields = line.split("\\s+");
                if (fields.length == 0) {
                    continue;
                }
                var keyword = fields[0];
                switch (keyword) {
                    case "newmtl":
                        material = new Material(parent);
                        materials.put(fields[1], material);
                        break;
                    case "ke": {
                        var r = Double.parseDouble(fields[1]);
                        var g = Double.parseDouble(fields[2]);
                        var b = Double.parseDouble(fields[3]);
                        var max = Math.max(Math.max(r, g), b);
                        if (max > 0.0) {
                            material.color = new Color(r / max, g / max, b / max);
                            material.emittance = max;
                        }
                        break;
                    }
                    case "kd":
                        var r = Double.parseDouble(fields[1]);
                        var g = Double.parseDouble(fields[2]);
                        var b = Double.parseDouble(fields[3]);
                        material.color = new Color(r, g, b);
                        break;
                    case "map_kd": {
                        var p = Paths.get(file.getParent(), fields[1]).toString();
                        material.texture = ColorTexture.getTexture(p);
                        break;
                    }
                    case "MAP_BUMP":
                        var p = Paths.get(file.getParent(), fields[1]).toString();
                        material.normalTexture = ColorTexture.getTexture(p);
                        if (material.normalTexture != null) {
                            material.normalTexture.pow(1.0 / 2.2);
                        }
                        break;
                }
            }
        }
    }

    private static int parseIndex(String value, int length) {
        try {
            var n = Integer.parseInt(value);
            if (n < 0) {
                n += length;
            }
            return n;
        } catch (Exception ignored) {
            return 0;
        }
    }
}
