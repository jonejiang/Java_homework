package book.video.boundary;

import book.video.controller.VideoServiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:8080",
        "http://localhost:8181", "http://localhost:8282",
        "http://localhost:8383"})

@RestController // <1>
public class VideosResource {

    @Autowired // <2>
    VideoServiceController videoServiceController;

    @RequestMapping(value = "/", produces = "application/json") // <3>
    public ResponseEntity<List<String>> getVideos(
                      @RequestParam ("videoId") final long videoId,
                      @RequestParam("gameName") final String gameName) {
        final List<String> linksFromGame = videoServiceController
                .getLinksFromGame(Long.toString(videoId), gameName);
        return ResponseEntity.ok(linksFromGame);
    }
}
