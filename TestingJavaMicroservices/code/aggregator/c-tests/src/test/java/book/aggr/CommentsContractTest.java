package book.aggr;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import java.net.URL;
import org.arquillian.algeron.consumer.StubServer;
import org.arquillian.algeron.pact.consumer.spi.Pact;
import org.arquillian.algeron.pact.consumer.spi.PactVerification;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

// tag::test[]
@RunWith(Arquillian.class) // <1>
@Pact(provider = "comments_service", consumer =
        "games_aggregator_service")
// <2>
public class CommentsContractTest {


    private static final String commentObject = "{" + "  'comment' " +
            ": 'This Game is Awesome'," + "  'rate' : 5," + "  " +
            "'gameId': 1234" + "}";

    private static final String commentResult = "{" + "   'rate': " +
            "5.0," + "   'total': 1," + "   'comments': ['This Game" +
            " is Awesome']" + "}";

    public PactFragment putCommentFragment(PactDslWithProvider
                                                   builder) { // <3>
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder.uponReceiving("User creates a new comment")
                .path("/comments").method("POST").headers(headers)
                .body(toJson(commentObject)) // <4>
                .willRespondWith().status(201).matchHeader
                        ("Location", ".*/[0-9a-f]+",
                                "/comments/1234").toFragment();
    }
    // end::test[]

    //TODO beta3 makes it better. header thing

    // tag::test2[]

    @StubServer
    URL url;

    public PactFragment getCommentsFragment(PactDslWithProvider
                                                    builder) {

        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder.given("A game with id 12 with rate 5 and " +
                "message This Game is Awesome") // <1>
                .uponReceiving("User gets comments for given Game")
                .matchPath("/comments/12").method("GET")
                .willRespondWith().status(200).headers(headers)
                .body(toJson(commentResult)).toFragment();

    }

    @Test
    @PactVerification(fragment = "getCommentsFragment")
    public void shouldGetComentsFromCommentsService() throws
            ExecutionException, InterruptedException {
        final CommentsGateway commentsGateway = new CommentsGateway();
        commentsGateway.initRestClient(url.toString());

        final Future<JsonObject> comments = commentsGateway
                .getCommentsFromCommentsService(12);
        final JsonObject commentsResponse = comments.get();

        assertThat(commentsResponse.getJsonNumber("rate")
                .doubleValue()).isEqualTo(5); // <2>
        assertThat(commentsResponse.getInt("total")).isEqualTo(1);
        assertThat(commentsResponse.getJsonArray("comments"))
                .hasSize(1);

    }

    // end::test2[]

    // tag::test[]
    @Test
    @PactVerification(fragment = "putCommentFragment") // <5>
    public void shouldInsertCommentsInCommentsService() throws
            ExecutionException, InterruptedException {

        final CommentsGateway commentsGateway = new CommentsGateway();
        commentsGateway.initRestClient(url.toString()); //
        // <6>

        JsonReader jsonReader = Json.createReader(new StringReader
                (toJson(commentObject)));
        JsonObject commentObject = jsonReader.readObject();
        jsonReader.close();

        final Future<Response> comment = commentsGateway
                .createComment(commentObject);

        final Response response = comment.get();
        final URI location = response.getLocation();

        assertThat(location).isNotNull();
        final String id = extractId(location);

        assertThat(id).matches("[0-9a-f]+");
        assertThat(response.getStatus()).isEqualTo(201);

    }

    // end::test[]

    private String extractId(final URI location) {
        final String commentLocation = location.getPath();
        return commentLocation.substring(commentLocation
                .lastIndexOf('/') + 1);
    }

    // TODO in beta3 of pact consumer a method is providing for this
    public static String toJson(String jsonString) {
        StringBuilder builder = new StringBuilder();
        boolean single_context = false;
        for (int i = 0; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);
            if (ch == '\\') {
                i = i + 1;
                if (i < jsonString.length()) {
                    ch = jsonString.charAt(i);
                    if (!(single_context && ch == '\'')) {
                        // unescape ' inside single quotes
                        builder.append('\\');
                    }
                }
            } else if (ch == '\'') {
                // Turn ' into ", for proper JSON string
                ch = '"';
                single_context = !single_context;
            }
            builder.append(ch);
        }

        return builder.toString();
    }

    // tag::test[]
}
// end::test[]