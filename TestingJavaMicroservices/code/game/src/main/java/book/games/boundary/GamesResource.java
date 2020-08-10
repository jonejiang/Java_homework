package book.games.boundary;

import book.games.control.GamesService;
import book.games.entity.Game;
import book.games.entity.SearchResult;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;

// tag::jaxrs[]
@Path("/")
@javax.ejb.Singleton // <1>
@Lock(LockType.READ)
public class GamesResource {

    @Inject
    GamesService gamesService;

    @Inject // <2>
            ExecutorServiceProducer managedExecutorService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @javax.ejb.Asynchronous // <3>
    public void searchGames(@Suspended final AsyncResponse
                                        response, // <4>
                            @NotNull @QueryParam("query") final
                            String query) {

        response.setTimeoutHandler(asyncResponse -> asyncResponse
                .resume(Response.status(Response.Status
                        .SERVICE_UNAVAILABLE).entity("TIME OUT !")
                        .build()));
        response.setTimeout(15, TimeUnit.SECONDS);

        managedExecutorService.getManagedExecutorService().submit(
                () -> { // <5>
            try {

                final Collector<JsonObject, ?, JsonArrayBuilder>
                        jsonCollector = Collector.of
                        (Json::createArrayBuilder,
                                JsonArrayBuilder::add, (left,
                                                        right) -> {
                    left.add(right);
                    return left;
                });

                final List<SearchResult> searchResults =
                        gamesService.searchGames(query);

                final JsonArrayBuilder mappedGames = searchResults
                        .stream().map(SearchResult::convertToJson)
                        .collect(jsonCollector);

                final Response.ResponseBuilder ok = Response.ok
                        (mappedGames.build());
                response.resume(ok.build()); // <6>
            } catch (final Throwable e) {
                response.resume(e); // <7>
            }
        });
    }
    // end::jaxrs[]

    @GET
    @Path("{gameId}")
    @Produces(MediaType.APPLICATION_JSON)
    @javax.ejb.Asynchronous
    public void searchGameById(@Suspended final AsyncResponse
                                           response, @PathParam
            ("gameId") final long gameId) {

        response.setTimeoutHandler(asyncResponse -> asyncResponse
                .resume(Response.status(Response.Status
                        .SERVICE_UNAVAILABLE).entity("TIME OUT !")
                        .build()));
        response.setTimeout(15, TimeUnit.SECONDS);

        managedExecutorService.getManagedExecutorService().submit(
                () -> {
            try {
                final Game game = gamesService.searchGameById(gameId);
                if (game != null) {
                    response.resume(Response.ok(game.convertToJson
                            ()).build());
                } else {
                    response.resume(Response.status(404));
                }
            } catch (final Throwable e) {
                response.resume(e);
            }
        });
    }

    // tag::jaxrs[]
}
// end::jaxrs[]