import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class YouTubeSearchTest {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 3;
    private static final String KEY = Optional.ofNullable(System
            .getenv("YOUTUBE_API_KEY")).orElse(Optional.ofNullable
            (System.getProperty("YOUTUBE_API_KEY")).orElseGet(() ->
            "dummy"));

    private YouTube youtube;

    //@Ignore //Until we have a valid API key
    @Test
    public void connect() throws IOException {

        youtube = new YouTube.Builder(new NetHttpTransport(), new
                JacksonFactory(), null).setApplicationName("Gamer " +
                "Video Microservice").build();
        final YouTube.Search.List search = youtube.search().list
                ("id,snippet");
        search.setKey(KEY);
        search.setQ("Zelda");

        search.setType("video");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        search.setFields("items(id/kind,id/videoId,snippet/title)");
        search.setOrder("rating");

        final SearchListResponse searchResponse = search.execute();
        final List<SearchResult> searchResultList = searchResponse
                .getItems();
        if (searchResultList != null) {
            for (final SearchResult singleVideo : searchResultList) {
                final ResourceId rId = singleVideo.getId();
                if (rId.getKind().equals("youtube#video")) {

                    System.out.println(" Video Id  " + rId
                            .getVideoId());
                    System.out.println(" Title " + singleVideo
                            .getSnippet().getTitle());

                    System.out.println
                            ("\n-------------------------------------------------------------\n");

                }
            }
        }

    }

}
