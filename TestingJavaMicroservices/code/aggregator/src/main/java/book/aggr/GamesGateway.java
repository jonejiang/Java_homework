package book.aggr;

import org.glassfish.json.jaxrs.JsonStructureBodyReader;
import org.jvnet.hk2.annotations.Service;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
// tag::gateway[]
public class GamesGateway {

    private final Client client;
    private final WebTarget games;
    private final String gamesHost;

    public GamesGateway() {
        this.client = ClientBuilder.newClient();

        this.gamesHost = Optional.ofNullable(System.getenv
                ("GAMES_SERVICE_URL")).orElse(Optional.ofNullable
                (System.getProperty("GAMES_SERVICE_URL")).orElse
                ("http://localhost:8181/"));

        this.games = this.client.target(gamesHost); // <1>
    }

    public Future<JsonObject> getGameFromGamesService(final long
                                                              gameId) {
        return this.games.path("{gameId}").resolveTemplate
                ("gameId", gameId) // <2>
                .register(JsonStructureBodyReader.class) // <3>
                .request(MediaType.APPLICATION_JSON).async() // <4>
                .get(JsonObject.class);
    }

}
// end::gateway[]