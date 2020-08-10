package book.aggr;

import org.glassfish.json.jaxrs.JsonStructureBodyReader;
import org.glassfish.json.jaxrs.JsonStructureBodyWriter;
import org.jvnet.hk2.annotations.Service;

import javax.annotation.PreDestroy;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.Future;

// tag::test[]
@Service // <1>
public class CommentsGateway {

    public static final String COMMENTS_SERVICE_URL =
            "COMMENTS_SERVICE_URL";
    private Client client;
    private WebTarget comments;

    public CommentsGateway() { // <2>

        // tag::env[]
        String commentsHost = Optional.ofNullable(System.getenv
                (COMMENTS_SERVICE_URL)) // <1>
                .orElse(Optional.ofNullable(System.getProperty
                        ("COMMENTS_SERVICE_URL")).orElse
                        ("http://localhost:8282/comments-service/"));
        // end::env[]
        initRestClient(commentsHost);
    }

    void initRestClient(final String host) {
        this.client = ClientBuilder.newClient().property("jersey" +
                ".config.client.connectTimeout", 2000).property
                ("jersey.config.client.readTimeout", 2000);
        this.comments = this.client.target(host);
    }

    public Future<Response> createComment(final JsonObject comment)
    { // <3>
        return this.comments.path("comments").register
                (JsonStructureBodyWriter.class) // <4>
                .request(MediaType.APPLICATION_JSON).async() // <5>
                .post(Entity.entity(comment, MediaType
                        .APPLICATION_JSON_TYPE)); // <6>
    }

    public Future<JsonObject> getCommentsFromCommentsService(final
                                                             long gameId) {
        return this.comments.path("comments/{gameId}")
                .resolveTemplate("gameId", gameId).register
                        (JsonStructureBodyReader.class).request
                        (MediaType.APPLICATION_JSON).async().get
                        (JsonObject.class);
    }

    @PreDestroy
    public void preDestroy() {
        if (null != client) {
            client.close();
        }
    }
}
// end:test[]

