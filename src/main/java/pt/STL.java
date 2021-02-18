package pt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class STL {

    private static class STLHeader {

        final byte[] ignore = new byte[80];
        int count;

        public STLHeader(DataInputStream stream) throws IOException {
            var read = stream.read(ignore);
            count = Integer.reverseBytes(stream.readInt());
        }
    }

    private static class STLTriangle {

        final float[] n = new float[3];
        final float[] v1 = new float[3];
        final float[] v2 = new float[3];
        final float[] v3 = new float[3];
        short ignore;

        public STLTriangle(DataInputStream stream) throws IOException {
            n[0] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            n[1] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            n[2] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v1[0] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v1[1] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v1[2] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v2[0] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v2[1] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v2[2] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v3[0] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v3[1] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            v3[2] = Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
            ignore = stream.readShort();
        }
    }

    public static Mesh loadSTL(String path, Material material) throws IOException {
        System.out.printf("Loading STL: %s\n", path);

        // open file
        var file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }

        // get file size
        var size = file.length();

        // read header, get expected binary size
        try (var binary = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            binary.mark(84);

            var header = new STLHeader(binary);
            var expectedSize = (long)header.count * 50L + 84L;

            // rewind to start of file
            binary.reset();

            // parse ascii or binary stl
            if (size == expectedSize) {
                return loadSTLB(binary, material);
            } else {
                return loadSTLA(binary, material);
            }
        }
    }

    private static Mesh loadSTLA(DataInputStream stream, Material material) throws IOException {
        List<Vector> vertexes = new ArrayList<>();
        var reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            var fields = line.trim().split("\\s+");
            if (fields.length == 4 && fields[0].equals("vertex")) {
                var f0 = Float.parseFloat(fields[1]);
                var f1 = Float.parseFloat(fields[2]);
                var f2 = Float.parseFloat(fields[3]);
                vertexes.add(new Vector(f0, f1, f2));
            }
        }
        List<Triangle> triangles = new ArrayList<>();
        for (var i = 0; i < vertexes.size(); i += 3) {
            var t = new Triangle();
            t.material = material;
            t.v1 = vertexes.get(i);
            t.v2 = vertexes.get(i + 1);
            t.v3 = vertexes.get(i + 2);
            t.fixNormals();
            triangles.add(t);
        }
        return new Mesh(triangles.toArray(new Triangle[0]));
    }

    private static Mesh loadSTLB(DataInputStream stream, Material material) throws IOException {
        var header = new STLHeader(stream);
        var triangles = new Triangle[header.count];
        for (var i = 0; i < header.count; i++) {
            var d = new STLTriangle(stream);
            var t = new Triangle();
            t.material = material;
            t.v1 = new Vector(d.v1[0], d.v1[1], d.v1[2]);
            t.v2 = new Vector(d.v2[0], d.v2[1], d.v2[2]);
            t.v3 = new Vector(d.v3[0], d.v3[1], d.v3[2]);
            t.fixNormals();
            triangles[i] = t;
        }
        return new Mesh(triangles);
    }

    public static void saveSTL(String path, Mesh mesh) {
        throw new UnsupportedOperationException("Pending implementation!");
    }
}
