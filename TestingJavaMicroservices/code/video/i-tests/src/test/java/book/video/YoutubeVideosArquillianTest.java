package book.video;

import book.video.boundary.YouTubeVideos;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.spring.integration.test.annotation
        .SpringAnnotationConfiguration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis
        .JedisConnectionFactory;

// tag::test[]
// <1>
@RunWith(Arquillian.class)
@SpringAnnotationConfiguration(classes =
        {YoutubeVideosArquillianTest.class, ControllerConfiguration
                .class, ThreadExecutorConfiguration.class})
@Configuration
public class YoutubeVideosArquillianTest {

    // <2>
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        final JedisConnectionFactory jcf = new
                JedisConnectionFactory();
        jcf.setHostName("localhost");
        return jcf;
    }

    @Primary
    @Bean
    public YouTubeVideos getYouTubeVideos() {
        return new YouTubeVideos();
    }

    // <3>
    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "spring-test" +
                ".jar").addClasses(YouTubeVideos.class);
    }

    // <4>
    @Autowired
    private YouTubeVideos youtubeVideos;

    // <5>
    @Test
    public void test() {
        Assert.assertNotNull(this.youtubeVideos);
    }

}
// end::test[]
