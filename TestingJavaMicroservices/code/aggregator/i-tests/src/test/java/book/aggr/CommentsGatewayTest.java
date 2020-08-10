package book.aggr;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded
        .EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.fest.assertions.Assertions.assertThat;

// tag::test[]
@RunWith(Arquillian.class)
public class CommentsGatewayTest {

    @Deployment(testable = false) // <1>
    public static WebArchive createCommentsDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class,
                CommentsGatewayTest.class.getName() + ".war") // <2>
                .forProjectDirectory("../../comments")
                .importBuildOutput().as(WebArchive.class);
    }

    @ArquillianResource // <3>
    private URL url;

    @Test
    public void shouldInsertCommentsInCommentsService() throws
            ExecutionException, InterruptedException {

        final JsonObject commentObject = Json.createObjectBuilder()
                .add("comment", "This Game is Awesome").add("rate",
                        5).add("gameId", 1234).build();

        final CommentsGateway commentsGateway = new CommentsGateway
                (); // <4>
        commentsGateway.initRestClient(url.toString());

        final Future<Response> comment = commentsGateway
                .createComment(commentObject);

        final Response response = comment.get();
        final URI location = response.getLocation();

        assertThat(location).isNotNull();
        final String id = extractId(location);

        assertThat(id).matches("[0-9a-f]+"); // <5>


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
