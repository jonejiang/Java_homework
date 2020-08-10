package book.video.boundary;

import book.video.controller.YouTubeVideoLinkCreator;
import book.video.entity.YoutubeLink;
import book.video.entity.YoutubeLinks;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class YouTubeGateway {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 3;

    private final YouTubeVideoLinkCreator youTubeVideoLinkCreator;

    private String apiKey = "";
    private YouTube youtube;

    @Autowired
    public YouTubeGateway(final YouTubeVideoLinkCreator
                                      youTubeVideoLinkCreator) {
        this.youTubeVideoLinkCreator = youTubeVideoLinkCreator;
    }

    @PostConstruct
    public void initClient() {
        this.apiKey = Optional.ofNullable(System.getenv
                ("YOUTUBE_API_KEY")).orElse(Optional.ofNullable
                (System.getProperty("YOUTUBE_API_KEY")).orElseGet(
                        () -> "AIzaSyDIQ0uq4ZpV-X4wBCmo4xea0aJRMoyG7kI"));
        this.youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), null).setApplicationName
                ("Gamer Video Microservice").build();
    }

    public YoutubeLinks findYoutubeLinks(final String gameName)
            throws IOException {
        final YouTube.Search.List search = youtube.search().list
                ("id,snippet");
        search.setKey(apiKey);
        search.setQ(gameName);

        search.setType("video");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        search.setFields("items(id/kind,id/videoId,snippet/title)");
        search.setOrder("rating");


        final SearchListResponse searchResponse = search.execute();
        final List<SearchResult> searchResultList = searchResponse
                .getItems();

        final Set<YoutubeLink> collect = searchResultList.stream()
                .filter(searchResult -> "youtube#video".equals
                        (searchResult.getId().getKind())).map
                        (converterToYoutubeLink).peek(youtubeLink
                        -> youtubeLink.setYouTubeVideoLinkCreator
                        (youTubeVideoLinkCreator::createEmbeddedUrl)).collect(Collectors.toSet());

        return new YoutubeLinks(collect);
    }

    private static final Function<SearchResult, YoutubeLink>
            converterToYoutubeLink = searchResult -> new
            YoutubeLink(searchResult.getId().getVideoId());

}
