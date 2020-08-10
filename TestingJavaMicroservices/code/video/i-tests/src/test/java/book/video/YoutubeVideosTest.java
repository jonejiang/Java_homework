package book.video;

import book.video.boundary.YouTubeVideos;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context
        .junit4.SpringJUnit4ClassRunner;
import redis.embedded.RedisServer;

import java.util.Collections;

// tag::test[]
// <1>
@RunWith(SpringJUnit4ClassRunner.class)
// <2>
@SpringBootTest(classes = {Main.class})
public class YoutubeVideosTest {

    private static RedisServer redisServer;

    @BeforeClass
    public static void beforeClass() throws Exception {
        redisServer = new RedisServer();
        redisServer.start();
    }

    @AfterClass
    public static void afterClass() {
        redisServer.stop();
    }

    //    @Rule
    //    public RedisRule redisRule = new RedisRule
    // (newManagedRedisConfiguration().build());

    // <3>
    @Autowired
    YouTubeVideos youtubeVideos;

    @Test
    // <4>
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    @ShouldMatchDataSet(location = "expected-videos.json")
    public void shouldCacheGamesInRedis() {
        youtubeVideos.createYouTubeLinks("123", Collections
                .singletonList("https://www.youtube.com/embed/7889"));
    }
}
// end::test[]