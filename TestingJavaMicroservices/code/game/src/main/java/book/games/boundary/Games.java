package book.games.boundary;


import book.games.entity.Game;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

// tag::repo[]
@Stateless // <1>
public class Games {

    @PersistenceContext // <2>
            EntityManager em;

    public Long create(final Game request) {
        final Game game = em.merge(request); // <3>
        return game.getId();
    }

    public Optional<Game> findGameById(final Long gameId) {
        Optional<Game> g = Optional.ofNullable(em.find(Game.class,
                gameId));

        if (g.isPresent()) {
            //Force load of lazy collections before detach
            Game game = g.get();
            game.getReleaseDates().size();
            game.getPublishers().size();
            game.getDevelopers().size();
            em.detach(game);
        }

        return g;
    }

}
// end::repo[]