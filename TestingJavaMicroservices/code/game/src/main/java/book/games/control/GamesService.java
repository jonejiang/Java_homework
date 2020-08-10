package book.games.control;

import book.games.boundary.Games;
import book.games.boundary.IgdbGateway;
import book.games.entity.Game;
import book.games.entity.SearchResult;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// tag::service[]
@Dependent // <1>
public class GamesService {

    @EJB // <2>
            Games games;

    @EJB
    IgdbGateway igdbGateway;
    // end::service[]

    public List<SearchResult> searchGames(final String query)
            throws IOException {

        final JsonArray games = igdbGateway.searchGames(query);

        final List<SearchResult> mappedGames = new ArrayList<>();

        if (games.size() > 0) {
            mappedGames.addAll(extractGameInformation(games));
        }

        return mappedGames;

    }

    // tag::service[]
    public Game searchGameById(final long gameId) throws IOException {

        final Optional<Game> foundGame = games.findGameById(gameId)
                ; // <3>
        if (isGameInSiteDatabase(foundGame)) {
            return foundGame.get();
        } else {
            final JsonArray jsonByGameId = igdbGateway
                    .searchGameById(gameId); // <4>
            final Game game = Game.fromJson(jsonByGameId);
            games.create(game);
            return game;
        }

    }
    // end::service[]

    private boolean isGameInSiteDatabase(final Optional<Game>
                                                 foundGame) {
        return foundGame.isPresent();
    }

    private List<SearchResult> extractGameInformation(final
                                                      JsonArray
                                                              games) {

        return games.stream().map(value -> {
            JsonObject game = (JsonObject) value;
            return new SearchResult(game.getInt("id"), game
                    .getString("name"));

        }).collect(Collectors.toList());
    }

    // tag::service[]
}
// end::service[]