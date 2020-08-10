package book.web.rule;

import org.junit.rules.ExternalResource;
import redis.embedded.RedisServer;

public class RedisRule extends ExternalResource {

    private RedisServer redisServer;
    private int port;

    public RedisRule(int port) {
        this.port = port;
    }

    @Override
    protected void before() throws Throwable {
        try {
            redisServer = new RedisServer(this.port);
            redisServer.start();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void after() {
        //Stop Redis
        if (null != redisServer) {
            redisServer.stop();
        }
    }
}
