import examples.Bunny;
import examples.Cornell;
import examples.Craft;
import examples.Cubes;
import examples.Cylinder;
import examples.Dragon;
import examples.Ellipsoid;
import examples.Example1;
import examples.Example2;
import examples.Example3;
import examples.Go;
import examples.Gopher;
import examples.HDRI;
import examples.Hello;
import examples.Hits;
import examples.Love;
import examples.Materials;
import examples.Maze;
import examples.Qbert;
import examples.Refraction;
import examples.Runway;
import examples.SDF;
import examples.Spheres;
import examples.Sponza;
import examples.Suzanne;
import examples.Teapot;
import examples.Texels;
import examples.Toybrick;
import examples.VeachScene;

public class Program {

    private static int iterations = 1;

    public static void main(String[] args) throws Exception {
        try {
            iterations = Integer.parseInt(args[0]);
        } catch (Exception ignore) {
            System.out.println("Iterations not specified, defaulting to: " + iterations);
        }
        new Bunny().run(iterations);
        new Cornell().run(iterations);
        new Craft().run(iterations);
        new Cubes().run(iterations);
        new Cylinder().run(iterations);
        new Dragon().run(iterations);
        new Ellipsoid().run(iterations);
        new Example1().run(iterations);
        new Example2().run(iterations);
        new Example3().run(iterations);
        new Go().run(iterations);
        new Gopher().run(iterations);
        new HDRI().run(iterations);
        new Hello().run(iterations);
        new Hits().run(iterations);
        new Love().run(iterations);
        new Materials().run(iterations);
        new Maze().run(iterations);
        new Qbert().run(iterations);
        new Refraction().run(iterations);
        new Runway().run(iterations);
        new SDF().run(iterations);
        new Spheres().run(iterations);
        new Sponza().run(iterations);
        new Suzanne().run(iterations);
        new Teapot().run(iterations);
        new Texels().run(iterations);
        new Toybrick().run(iterations);
        new VeachScene().run(iterations);
    }
}
