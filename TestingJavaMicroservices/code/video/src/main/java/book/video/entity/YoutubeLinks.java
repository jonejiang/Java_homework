package book.video.entity;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class YoutubeLinks {

    private Set<YoutubeLink> links;

    public YoutubeLinks(final Set<YoutubeLink> youtubeLinks) {
        this.links = new HashSet<>(youtubeLinks);
    }

    public void addYoutubeLink(final YoutubeLink youtubeLink)
            throws MalformedURLException {
        this.links.add(youtubeLink);
    }

    public List<String> getYoutubeLinksAsString() {
        return links.stream().map(youtubeLink -> youtubeLink
                .getEmbedUrl().toString()).collect(Collectors
                .toList());
    }

    public Set<YoutubeLink> getYoutubeLinks() {
        return Collections.unmodifiableSet(links);
    }

}
