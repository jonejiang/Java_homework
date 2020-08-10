package book.games.boundary;

import book.games.entity.Game;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonValue;

import static org.junit.Assert.assertTrue;

public class IgdbGatewayTest {

    private static IgdbGateway gateway = new IgdbGateway();

    @BeforeClass
    public static void beforeClass() {
        gateway.postConstruct();
    }

    @AfterClass
    public static void afterClass() {
        gateway.postConstruct();
    }

    @Test
    public void searchGameById() throws Exception {
        JsonArray json = gateway.searchGameById(7346);
        Game game = Game.fromJson(json);
        assertTrue(game.getTitle().contains("The Legend of Zelda: "
                + "Breath of the Wild"));
    }

    @Test
    public void searchGames() throws Exception {
        JsonArray games = gateway.searchGames("Zelda");
        boolean found = false;
        for (final JsonValue game : games) {
            if (game.toString().contains("7346")) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

}