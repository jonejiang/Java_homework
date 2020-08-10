package book.games.boundary;

import book.games.entity.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// tag::test[]
@RunWith(MockitoJUnitRunner.class) // <1>
public class GamesTest {

    private static final long GAME_ID = 123L;

    @Mock // <2>
    EntityManager entityManager;

    @Test
    public void shouldCreateAGame() {
        final Game game = new Game();
        game.setId(GAME_ID);
        game.setTitle("Zelda");

        final Games games = new Games();

        when(entityManager.merge(game)).thenReturn(game); // <3>
        games.em = entityManager; // <4>

        games.create(game); // <5>

        verify(entityManager).merge(game); // <6>

    }
    // end::test[]

    // tag::subtest[]
    @Test
    public void shouldFindAGameById() {
        final Game game = new Game();
        game.setId(GAME_ID);
        game.setTitle("Zelda");

        final Games games = new Games();
        when(entityManager.find(Game.class, GAME_ID)).thenReturn(game);
        games.em = entityManager;

        final Optional<Game> foundGame = games.findGameById(GAME_ID);
        // <1>

        verify(entityManager).find(Game.class, GAME_ID);
        assertThat(foundGame).isNotNull().hasValue(game)
                .usingFieldByFieldValueComparator(); // <2>
    }

    @Test
    public void shouldReturnAnEmptyOptionalIfElementNotFound() {
        final Game game = new Game();
        game.setId(GAME_ID);
        game.setTitle("Zelda");

        final Games games = new Games();
        when(entityManager.find(Game.class, GAME_ID)).thenReturn(null);
        games.em = entityManager;

        final Optional<Game> foundGame = games.findGameById(GAME_ID);

        verify(entityManager).find(Game.class, GAME_ID);
        assertThat(foundGame).isNotPresent(); // <3>
    }
    // end::subtest[]

    // tag::test[]
}
// end::test[]