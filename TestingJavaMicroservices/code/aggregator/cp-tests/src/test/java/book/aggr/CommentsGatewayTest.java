package book.aggr;

import io.specto.hoverfly.junit.rule.HoverflyRule;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

// tag::test[]
public class CommentsGatewayTest {

    @ClassRule
    public static HoverflyRule hoverfly = HoverflyRule
        .inCaptureOrSimulationMode("simulation.json"); // <1>

    @Test
    public void shouldInsertComments()
        throws ExecutionException, InterruptedException {

        final JsonObject commentObject = Json.createObjectBuilder()
            .add("comment", "This Game is Awesome").add("rate",
                5).add("gameId", 1234).build();

        final CommentsGateway commentsGateway = new CommentsGateway
            (); // <4>
        commentsGateway.initRestClient("http://comments.gamers.com")
        ; // <2>

        final Future<Response> comment = commentsGateway
            .createComment(commentObject);

        final Response response = comment.get();
        final URI location = response.getLocation();

        assertThat(location).isNotNull();
        final String id = extractId(location);
        assertThat(id).matches("[0-9a-f]+"); // <3>
    }

    // end::test[]
    private String extractId(final URI location) {
        final String commentLocation = location.getPath();
        return commentLocation.substring(commentLocation
            .lastIndexOf('/') + 1);
    }
    // tag::test[]

}
// end::test[]