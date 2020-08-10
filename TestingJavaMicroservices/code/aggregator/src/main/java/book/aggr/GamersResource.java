package book.aggr;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Path("/")
public class GamersResource {

    // tag::aggr[]

    @Inject
    private GamesGateway gamesGateway; // <1>

    @Inject
    private CommentsGateway commentsGateway;

    private final Executor executor = Executors.newFixedThreadPool(8);

    @GET
    @Path("{gameId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getGameInfo(@Suspended final AsyncResponse
                                        asyncResponse, @PathParam
            ("gameId") final long gameId) {

        asyncResponse.setTimeoutHandler(ar -> ar.resume(Response
                .status(Response.Status.SERVICE_UNAVAILABLE).entity
                        ("TIME OUT !").build()));
        asyncResponse.setTimeout(15, TimeUnit.SECONDS);

        final CompletableFuture<JsonObject> gamesGatewayFuture =
                Futures.toCompletable(gamesGateway
                        .getGameFromGamesService(gameId), executor);
        final CompletableFuture<JsonObject> commentsGatewayFuture =
                Futures.toCompletable(commentsGateway
                        .getCommentsFromCommentsService(gameId),
                        executor);

        gamesGatewayFuture.thenCombine(commentsGatewayFuture, // <2>
                (g, c) -> Json.createObjectBuilder() // <3>
                        .add("game", g).add("comments", c).build())
                .thenApply(info -> asyncResponse.resume(Response.ok
                        (info).build()) // <4>
        ).exceptionally(asyncResponse::resume);

    }

    // end::aggr[]

}
