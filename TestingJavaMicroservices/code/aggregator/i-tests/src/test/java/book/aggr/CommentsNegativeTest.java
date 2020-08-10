package book.aggr;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.fest.assertions.Assertions.assertThat;

// tag::test[]
public class CommentsNegativeTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089); // <1>

    @Test
    public void
    shouldReturnAServerErrorInCaseOfStatus500WhenCreatingAComment()
            throws ExecutionException, InterruptedException {
        stubFor( // <2>
                post(urlEqualTo("/comments")).willReturn(aResponse
                        ().withStatus(500).withBody("Exception " +
                        "during creation of comment")));

        CommentsGateway commentsGateway = new CommentsGateway();
        commentsGateway.initRestClient("http://localhost:8089"); //
        // <3>

        final JsonObject commentObject = Json.createObjectBuilder()
                .add("comment", "This Game is Awesome").add("rate",
                        5).add("gameId", 1234).build();

        final Future<Response> comment = commentsGateway
                .createComment(commentObject);
        final Response response = comment.get();

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getStatusInfo().getReasonPhrase())
                .isEqualTo("Server Error"); // <4>
    }

    @Test
    public void shouldThrowAnExceptionInCaseOfTimeout() throws
            ExecutionException, InterruptedException {
        stubFor(post(urlEqualTo("/comments")).willReturn(aResponse
                ().withStatus(201).withHeader("Location",
                "http://localhost:8089/comments/12345")
                .withFixedDelay(1000) // <5>
        ));

        CommentsGateway commentsGateway = new CommentsGateway();
        commentsGateway.initRestClient("http://localhost:8089");

        final JsonObject commentObject = Json.createObjectBuilder()
                .add("comment", "This Game is Awesome").add("rate",
                        5).add("gameId", 1234).build();

        final Future<Response> comment = commentsGateway
                .createComment(commentObject);

        try {
            comment.get();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ExecutionException.class);
            // <6>
            final Throwable processingException = e.getCause();
            assertThat(processingException).isInstanceOf
                    (ProcessingException.class);
            assertThat(processingException.getCause()).isInstanceOf
                    (SocketTimeoutException.class);
        }

    }
}
// end::test[]
