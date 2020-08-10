package book.video.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YouTubeVideos {

    @Autowired
    StringRedisTemplate redisTemplate;

    public void createYouTubeLinks(final String gameId, final
    List<String> youtubeLinks) {
        final ListOperations<String, String>
                stringStringListOperations = redisTemplate
                .opsForList();
        stringStringListOperations.leftPushAll(gameId, youtubeLinks);
    }

    public boolean isGameInserted(final String gameId) {
        final ListOperations<String, String>
                stringStringListOperations = redisTemplate
                .opsForList();
        return stringStringListOperations.size(gameId) > 0;
    }

    public List<String> getYouTubeLinks(final String gameId) {
        final ListOperations<String, String>
                stringStringListOperations = redisTemplate
                .opsForList();
        final Long size = stringStringListOperations.size(gameId);
        return stringStringListOperations.range(gameId, 0, size);

    }

}
