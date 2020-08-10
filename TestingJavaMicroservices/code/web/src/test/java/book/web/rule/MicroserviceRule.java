package book.web.rule;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Assert;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class MicroserviceRule extends ExternalResource {

    private final Logger log = Logger.getLogger(MicroserviceRule.class.getName());

    private final ReentrantLock lock = new ReentrantLock();
    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicBoolean poll = new AtomicBoolean(true);
    private final AtomicReference<URL> url = new AtomicReference<>();
    private File file;
    private String[] args;
    private ResolutionStrategy strategy = new DefaultJavaResolutionStrategy();
    private long time = 30;
    private TimeUnit unit = TimeUnit.SECONDS;

    public MicroserviceRule(URL url) {
        this.url.set(url);
    }

    public MicroserviceRule(String url) {
        try {
            this.url.set(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL: " + url, e);
        }
    }

    public MicroserviceRule withExecutableJar(File file, String... args) { // <1>

        Assert.assertTrue("The file must exist and be readable: " + file, file.exists() && file.canRead());

        this.file = file;
        this.args = args;
        return this;
    }

    public MicroserviceRule withJavaResolutionStrategy(ResolutionStrategy strategy) {
        this.strategy = (null != strategy ? strategy : this.strategy);
        return this;
    }

    public MicroserviceRule withTimeout(int time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
        return this;
    }

    private Process process;

    @Override
    protected void before() throws Throwable {

        Assert.assertNotNull("The MicroserviceRule requires a valid jar file", this.file);
        Assert.assertNotNull("The MicroserviceRule requires a valid url", this.url.get());

        this.lock.lock();

        try {
            ArrayList<String> args = new ArrayList<>();
            args.add(this.strategy.getJavaExecutable().toString()); // <2>
            args.add("-jar");
            args.add(this.file.toString());

            if (null != this.args) {
                args.addAll(Arrays.asList(this.args));
            }

            ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[args.size()]));
            pb.directory(file.getParentFile());
            pb.inheritIO();
            process = pb.start(); // <3>

            log.info("Started " + this.file);

            final Thread t = new Thread(() -> {
                if (MicroserviceRule.this.connect(MicroserviceRule.this.url.get())) { // <4>
                    MicroserviceRule.this.latch.countDown();
                }
            }, "Connect thread :: " + this.url.get());

            t.start();

            if (!latch.await(this.time, this.unit)) { // <5>
                throw new RuntimeException("Failed to connect to server within timeout: "
                        + this.url.get());
            }

        } finally {
            this.poll.set(false);
            this.lock.unlock();
        }
    }

    @Override
    protected void after() {

        this.lock.lock(); // <6>

        try {
            if (null != process) {
                process.destroy();
                process = null;
            }
        } finally {
            this.lock.unlock();
        }
    }

    private boolean connect(final URL url) {

        do {
            try {
                Request request = new Request.Builder().url(url).build();

                if (new OkHttpClient().newCall(request).execute().isSuccessful()) { // <7>
                    return true;
                } else {
                    throw new Exception("Unexpected family");
                }
            } catch (Exception ignore) {

                if (poll.get()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        return false;
                    }
                }
            }
        } while (poll.get());

        return false;
    }


}
