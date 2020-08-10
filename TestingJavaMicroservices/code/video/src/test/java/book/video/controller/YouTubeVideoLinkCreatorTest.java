package book.video.controller;

import org.junit.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

// tag::test[]
public class YouTubeVideoLinkCreatorTest {

    @Test // <1>
    public void shouldReturnYouTubeEmbeddedUrlForGivenVideoId() {
        // <2>
        final YouTubeVideoLinkCreator youTubeVideoLinkCreator = new
                YouTubeVideoLinkCreator(); // <3>

        final URL embeddedUrl = youTubeVideoLinkCreator
                .createEmbeddedUrl("1234"); // <4>

        assertThat(embeddedUrl).hasHost("www.youtube.com").hasPath
                ("/embed/1234"); // <5>
    }
}
// end::test[]