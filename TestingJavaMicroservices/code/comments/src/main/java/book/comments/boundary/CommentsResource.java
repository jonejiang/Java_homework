package book.comments.boundary;

import org.bson.Document;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

// tag::jaxrs[]
@Path("/comments") // <1>
@Singleton
@Lock(LockType.READ)
public class CommentsResource {

    @Inject
    private Comments comments;

    @Inject
    private DocumentToJsonObject transformer;

    @GET // <2>
    @Path("/{gameId}")
    @Produces(MediaType.APPLICATION_JSON) // <3>
    public Response getCommentsOfGivenGame(@PathParam("gameId")
                                               final Integer
                                                       gameId) { //
        // <4>

        final Optional<Document> commentsAndRating = comments
                .getCommentsAndRating(gameId);

        final JsonObject json = transformer.transform
                (commentsAndRating.orElse(new Document()));
        return Response.ok(json).build(); // <5>
    }
    // end::jaxrs[]

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createComment(final JsonObject jsonObject) {

        final Document commentDocument = transformer.transform
                (jsonObject);
        comments.createComment(commentDocument);

        return Response.created(URI.create(commentDocument
                .getObjectId("_id").toHexString())).build();
    }

    // tag::jaxrs[]
}
// end::jaxrs[]