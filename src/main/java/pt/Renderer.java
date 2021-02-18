package pt;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static pt.Channel.COLOR_CHANNEL;
import static pt.Util.clamp;
import static pt.Util.durationString;
import static pt.Util.numberString;

public class Renderer {

    private final Scene scene;
    private final Camera camera;
    private final Sampler sampler;
    private final Buffer buffer;
    private final int numCPU;
    public final int samplesPerPixel;
    public final boolean stratifiedSampling;
    public int adaptiveSamples;
    public final double adaptiveThreshold;
    public final double adaptiveExponent;
    public int fireflySamples;
    public final double fireflyThreshold;
    public final boolean verbose;

    public Renderer(Scene scene, Camera camera, Sampler sampler, int w, int h) {
        this.scene = scene;
        this.camera = camera;
        this.sampler = sampler;
        buffer = new Buffer(w, h);
        samplesPerPixel = 1;
        stratifiedSampling = false;
        adaptiveSamples = 0;
        adaptiveThreshold = 1.0;
        adaptiveExponent = 1.0;
        fireflySamples = 0;
        fireflyThreshold = 1.0;
        numCPU = Runtime.getRuntime().availableProcessors();
        verbose = true;
    }

    public void run() {
        var r = this;
        var scene = r.scene;
        var camera = r.camera;
        var sampler = r.sampler;
        var buf = r.buffer;
        var w = buf.width();
        var h = buf.height();
        var spp = r.samplesPerPixel;
        var sppRoot = (int)Math.sqrt(r.samplesPerPixel);
        var ncpu = r.numCPU;

        scene.compile();

        r.printf("%d x %d pixels, %d spp, %d cores\n", w, h, spp, ncpu);

        var start = Instant.now();
        scene.resetRayCount();

        var counter = new AtomicInteger();

        var service = Executors.newFixedThreadPool(ncpu);

        for (var c = 0; c < ncpu; c++) {
            var localI = c;
            service.submit(() -> {
                var rnd = ThreadLocalRandom.current();
                for (var y = localI; y < h; y += ncpu) {
                    for (var x = 0; x < w; x++) {
                        if (r.stratifiedSampling) {
                            // stratified subsampling
                            for (var u = 0; u < sppRoot; u++) {
                                for (var v = 0; v < sppRoot; v++) {
                                    var fu = ((double)u + 0.5) / (double)sppRoot;
                                    var fv = ((double)v + 0.5) / (double)sppRoot;
                                    var ray = camera.castRay(x, y, w, h, fu, fv);
                                    var sample = sampler.sample(scene, ray);
                                    buf.addSample(x, y, sample);
                                }
                            }
                        } else {
                            // random subsampling
                            for (var i = 0; i < spp; i++) {
                                var fu = rnd.nextDouble();
                                var fv = rnd.nextDouble();
                                var ray = camera.castRay(x, y, w, h, fu, fv);
                                var sample = sampler.sample(scene, ray);
                                buf.addSample(x, y, sample);
                            }
                        }
                        // adaptive sampling
                        if (r.adaptiveSamples > 0) {
                            var v = buf.standardDeviation(x, y).maxComponent();
                            v = clamp(v / r.adaptiveThreshold, 0.0, 1.0);
                            v = StrictMath.pow(v, r.adaptiveExponent);
                            var samples = (int)(v * (double)r.adaptiveSamples);
                            for (var i = 0; i < samples; i++) {
                                var fu = rnd.nextDouble();
                                var fv = rnd.nextDouble();
                                var ray = camera.castRay(x, y, w, h, fu, fv);
                                var sample = sampler.sample(scene, ray);
                                buf.addSample(x, y, sample);
                            }
                        }
                        // firefly reduction
                        if (r.fireflySamples > 0) {
                            if (buf.standardDeviation(x, y).maxComponent() > r.fireflyThreshold) {
                                for (var i = 0; i < r.fireflySamples; i++) {
                                    var fu = rnd.nextDouble();
                                    var fv = rnd.nextDouble();
                                    var ray = camera.castRay(x, y, w, h, fu, fv);
                                    var sample = sampler.sample(scene, ray);
                                    buf.addSample(x, y, sample);
                                }
                            }
                        }
                    }
                    r.showProgress(start, scene.rayCount(), counter.incrementAndGet(), h);
                }
            });
        }

        try {
            service.shutdown();
            service.awaitTermination(1L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        r.printf("\n");
    }

    private void printf(String format, Object... args) {
        if (verbose) {
            System.out.printf(format, args);
        }
    }

    private void showProgress(Instant start, long rays, int i, int h) {
        if (!verbose) {
            return;
        }
        var pct = (int)(100.0 * (double)i / (double)h);
        var elapsed = Duration.between(start, Instant.now());
        var rps = elapsed.getSeconds() > 0L ? (double)rays / (double)elapsed.getSeconds() : 0.0;
        System.out.printf("\r%4d / %d (%3d%%) [", i, h, pct);
        for (var p = 0; p < 100; p += 3) {
            System.out.print(pct > p ? "=" : " ");
        }
        System.out.printf("] %s %s ", durationString(elapsed), numberString(rps));
    }

    private void writeImage(String path, Buffer buf, Channel channel) throws IOException {
        ImageIO.write(buf.image(channel), "png", new File(path));
    }

    public BufferedImage render() {
        run();
        return buffer.image(COLOR_CHANNEL);
    }

    public void iterativeRender(String pathTemplate, int iterations) throws IOException {
        for (var i = 1; i <= iterations; i++) {
            printf("\n[Iteration %d of %d]\n", i, iterations);
            run();
            var path = pathTemplate;
            if (path.contains("%")) {
                path = String.format(pathTemplate, i);
            }
            writeImage(path, buffer, COLOR_CHANNEL);
        }
        buffer.image(COLOR_CHANNEL);
    }

    public void channelRender() {
        throw new UnsupportedOperationException();
    }

    public void frameRender(String path, int iterations) throws IOException {
        for (var i = 1; i <= iterations; i++) {
            run();
        }
        writeImage(path, buffer, COLOR_CHANNEL);
    }

    public Image timedRender(Duration duration) {
        var start = Instant.now();
        do {
            run();
        } while (Duration.between(start, Instant.now()).compareTo(duration) <= 0);
        return buffer.image(COLOR_CHANNEL);
    }
}
