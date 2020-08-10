package book.video;

import book.video.controller.YouTubeVideoLinkCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection
        .RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class ControllerConfiguration {

    @Bean
    public YouTubeVideoLinkCreator createYouTubeVideoLinkCreator() {
        return new YouTubeVideoLinkCreator();
    }

    @Bean
    StringRedisTemplate template(final RedisConnectionFactory
                                         connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

}
