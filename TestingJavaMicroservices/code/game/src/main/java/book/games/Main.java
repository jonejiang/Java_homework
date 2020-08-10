package book.games;

import book.games.boundary.ExecutorServiceProducer;
import book.games.boundary.Games;
import book.games.boundary.GamesResource;
import book.games.boundary.IgdbGateway;
import book.games.control.GamesService;
import book.games.entity.Game;
import book.games.entity.LocalDatePersistenceConverter;
import book.games.entity.ReleaseDate;
import book.games.entity.SearchResult;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.jpa.JPAFraction;

public class Main {

    public static Swarm createSwarm() throws Exception {

        final Swarm swarm = new Swarm();

        // Prevent JPA Fraction from installing it's default
        // datasource fraction
        swarm.fraction(new JPAFraction().defaultDatasource
                ("jboss/datasources/games"));

        return swarm;
    }

    public static JAXRSArchive createJAXRSArchive() throws Exception {
        final JAXRSArchive deployment = ShrinkWrap.create
                (JAXRSArchive.class);
        deployment.addClass(GamesResource.class);
        deployment.addClass(Games.class);
        deployment.addClass(Game.class);
        deployment.addClass(GamesService.class);
        deployment.addClass(LocalDatePersistenceConverter.class);
        deployment.addClass(ReleaseDate.class);
        deployment.addClass(IgdbGateway.class);
        deployment.addClass(SearchResult.class);
        deployment.addClass(ExecutorServiceProducer.class);
        deployment.addAsWebInfResource(new ClassLoaderAsset
                ("META-INF/persistence.xml", Main.class
                        .getClassLoader()),
                "classes/META-INF/persistence.xml");
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans"
                + ".xml");

        deployment.addAllDependencies();

        return deployment;
    }

    public static void main(final String... args) throws Exception {

        final Swarm swarm = createSwarm();
        final JAXRSArchive deployment = createJAXRSArchive();

        swarm.start().deploy(deployment);

    }
}
