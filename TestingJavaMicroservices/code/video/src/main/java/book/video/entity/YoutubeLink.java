package book.video.entity;

import java.net.URL;
import java.util.function.Function;

// tag::test[]
public class YoutubeLink {

    private final String videoId;
    Function<String, URL> youTubeVideoLinkCreator; // <1>

    public YoutubeLink(final String videoId) {
        this.videoId = videoId;
    }

    public void setYouTubeVideoLinkCreator(final Function<String,
            URL> youTubeVideoLinkCreator) {
        this.youTubeVideoLinkCreator = youTubeVideoLinkCreator;
    }

    public URL getEmbedUrl() {
        if (youTubeVideoLinkCreator != null) {
            return youTubeVideoLinkCreator.apply(this.videoId);
        } else {
            throw new IllegalStateException
                    ("YouTubeVideoLinkCreator not set");
        }
    }

    public String getVideoId() {
        return videoId;
    }

}
// tag::test[]
