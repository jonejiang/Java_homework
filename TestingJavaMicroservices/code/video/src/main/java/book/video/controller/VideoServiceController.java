package book.video.controller;

import book.video.boundary.YouTubeGateway;
import book.video.boundary.YouTubeVideos;
import book.video.entity.YoutubeLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class VideoServiceController {

    private final YouTubeGateway youtubeGateway;

    private final YouTubeVideos youtubeVideos;

    @Autowired
    public VideoServiceController(final YouTubeGateway
                                              youtubeGateway, final
    YouTubeVideos youtubeVideos) {
        this.youtubeGateway = youtubeGateway;
        this.youtubeVideos = youtubeVideos;
    }

    public List<String> getLinksFromGame(final String gameId, final
    String gameName) {
        if (youtubeVideos.isGameInserted(gameId)) {
            return youtubeVideos.getYouTubeLinks(gameId);
        } else {
            try {
                final YoutubeLinks youtubeLinks = youtubeGateway
                        .findYoutubeLinks(gameName);
                final List<String> youtubeLinksAsString =
                        youtubeLinks.getYoutubeLinksAsString();
                youtubeVideos.createYouTubeLinks(gameId,
                        youtubeLinksAsString);
                return youtubeLinksAsString;
            } catch (final IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

}
