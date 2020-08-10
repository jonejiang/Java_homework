package book.video.entity;

import book.video.controller.YouTubeVideoLinkCreator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

// tag::test[]
public class YouTubeLinkTest {

    @Test
    public void shouldCalculateEmbedYouTubeLink() {
        final YoutubeLink youtubeLink = new YoutubeLink("1234");

        final YouTubeVideoLinkCreator youTubeVideoLinkCreator = new
                YouTubeVideoLinkCreator(); // <1>
        youtubeLink.setYouTubeVideoLinkCreator
                (youTubeVideoLinkCreator::createEmbeddedUrl); // <2>

        assertThat(youtubeLink.getEmbedUrl()).hasHost("www.youtube" +
                ".com").hasPath("/embed/1234");
    }

}
// end::test[]