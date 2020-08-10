package book.web.rule;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.rules.ExternalResource;

import java.io.IOException;

public class MongodRule extends ExternalResource {

    private final MongodStarter starter
            = MongodStarter.getDefaultInstance();
    private MongodExecutable mongodExe;
    private MongodProcess mongodProcess;
    private String host;
    private int port;

    public MongodRule(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void before() throws Throwable { // <1>
        try {
            mongodExe = starter.prepare(new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(this
                    .host, this.port, Network.localhostIsIPv6())).build());
            mongodProcess = mongodExe.start();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void after() { // <2>
        //Stop MongoDB
        if (null != mongodProcess) {
            mongodProcess.stop();
        }
        if (null != mongodExe) {
            mongodExe.stop();
        }
    }
}
