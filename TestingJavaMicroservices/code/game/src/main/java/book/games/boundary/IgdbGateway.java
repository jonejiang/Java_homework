package book.games.boundary;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Singleton
@Lock(LockType.READ)
public class IgdbGateway {

    private Client client;
    private String apiKey;

    private WebTarget igdb;

    @PostConstruct
    public void postConstruct() {

        this.apiKey = Optional.ofNullable(System.getenv
                ("IGDB_API_KEY")).orElse(Optional.ofNullable(System
                .getProperty("IGDB_API_KEY")).orElse("dummy"));

        final String host = Optional.ofNullable(System.getenv
                ("IGDB_HOST")).orElse(Optional.ofNullable(System
                .getProperty("IGDB_HOST")).orElse
                ("https://igdbcom-internet-game-database-v1.p" + "" +
                        ".mashape.com"));

        this.client = ClientBuilder.newClient();
        this.igdb = this.client.target(host);
    }

    @PreDestroy
    private void preDestroy() {
        if (null != this.client) {
            try {
                this.client.close();
            } catch (final Exception e) {
                //no-op
            }
        }
    }

    public JsonArray searchGameById(final long gameId) throws
            IOException {
        try (InputStream content = igdb.path("games/{gameId}")
                .queryParam("fields", "*").resolveTemplate
                        ("gameId", gameId)

                .request(MediaType.APPLICATION_JSON_TYPE).header
                        ("X-Mashape-Key", apiKey).get(InputStream
                        .class)) {

            return Json.createReader(content).readArray();
        }
    }

    public JsonArray searchGames(final String q) throws IOException {

        WebTarget webTarget = igdb.path("games/").queryParam
                ("fields", "name").queryParam("limit", 50)
                .queryParam("offset", 0).queryParam("order",
                        "release_dates.date%3Adesc");

        webTarget = safeQuery(webTarget, q);

        Invocation.Builder builder = webTarget.request(MediaType
                .APPLICATION_JSON_TYPE).header("X-Mashape-Key",
                apiKey).accept("Accept", MediaType.APPLICATION_JSON);

        try (InputStream content = builder.get(InputStream.class)) {
            return Json.createReader(content).readArray();
        }

    }

    private WebTarget safeQuery(WebTarget webTarget, String q) {
        return null != q && q.length() > 0 ? webTarget.queryParam
                ("search", q) : webTarget;
    }

}
