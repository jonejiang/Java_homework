package book.games.control;

import book.games.boundary.Games;
import book.games.boundary.IgdbGateway;
import book.games.entity.Game;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

// tag::test[]
@RunWith(MockitoJUnitRunner.class)
public class GamesServiceTest {

    @Mock
    Games games;

    @Mock
    IgdbGateway igdbGateway;

    @Test
    public void shouldReturnGameIfItIsCachedInInternalDatabase()
            throws IOException {

        final Game game = new Game();
        game.setId(123L);
        game.setTitle("Zelda");
        game.setCover("ZeldaCover");

        when(games.findGameById(123L)).thenReturn(Optional.of(game));

        final GamesService gamesService = new GamesService();
        gamesService.games = games;
        gamesService.igdbGateway = igdbGateway;

        final Game foundGame = gamesService.searchGameById(123L);
        assertThat(foundGame).isEqualToComparingFieldByField(game);
        // <1>
        verify(igdbGateway, times(0)).searchGameById(anyInt()); // <2>
        verify(games).findGameById(123L);

    }
    // end::test[]

    // tag::subtest[]
    @Test
    public void
    shouldReturnGameFromIgdbSiteIfGameIsNotInInternalDatabase()
            throws IOException {

        final JsonArray returnedGame = createTestJsonArray();

        when(games.findGameById(123L)).thenReturn(Optional.empty());
        when(igdbGateway.searchGameById(123L)).thenReturn
                (returnedGame);

        final GamesService gamesService = new GamesService();
        gamesService.games = games;
        gamesService.igdbGateway = igdbGateway;

        final Game foundGame = gamesService.searchGameById(123L);
        assertThat(foundGame.getTitle()).isEqualTo("Battlefield 4");


        Assertions.assertThat(foundGame.getReleaseDates()) // <1>
                .hasSize(1).extracting("platformName",
                "releaseDate").contains(tuple("PlayStation 3",
                LocalDate.of(2013, 10, 29)));

        assertThat(foundGame.getDevelopers()).hasSize(1).contains
                ("EA Digital Illusions CE");

        assertThat(foundGame.getPublishers()).hasSize(1).contains
                ("Electronic Arts");

        verify(games).create(anyObject()); // <2>
        verify(igdbGateway).searchGameById(123L); // <3>

    }

    // end::subtest[]

    private JsonArray createTestJsonArray() {
        final String content = "[\n" + "   {\n" + "      " +
                "\"id\":123,\n" + "      \"name\":\"Battlefield " +
                "4\",\n" + "      \"release_dates\":[\n" + "       " +
                "" + "  {\n" + "            " +
                "\"platform_name\":\"PlayStation 3\",\n" + "       " +
                "" + "     \"release_date\":\"2013-10-29\"\n" + "  " +
                "     " + "  }\n" + "      ],\n" + "      " +
                "\"companies\":[\n" + "         {\n" + "           " +
                " \"developer\":true," + "\n" + "            " +
                "\"publisher\":false,\n" + "    " + "        " +
                "\"name\":\"EA Digital Illusions CE\"\n" + "       " +
                "  },\n" + "         {\n" + "            " +
                "\"developer\":false,\n" + "            " +
                "\"publisher\":true,\n" + "            " +
                "\"name\":\"Electronic Arts\"\n" + "         }\n" +
                "      ]\n" + "   }\n" + "]";

        return Json.createReader(new StringReader(content))
                .readArray();
    }

    // tag::test[]
}
// end::test[]