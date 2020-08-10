package book.video.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

// tag::test[]
public class YouTubeVideoLinkCreator {

    private static final String EMBED_URL = "https://www.youtube" +
            ".com/embed/";

    public URL createEmbeddedUrl(final String videoId) {
        try {
            return URI.create(EMBED_URL + videoId).toURL();
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
// end::test[]
