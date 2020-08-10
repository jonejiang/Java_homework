package integrationtests;

import book.games.Main;
import book.games.boundary.Games;
import book.games.entity.Game;
import book.games.entity.ReleaseDate;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class GamesTest {

    @Deployment
    public static Archive createDeployment() throws Exception {
        final WARArchive warArchive = ShrinkWrap.create(WARArchive
                .class);
        warArchive.addClasses(Games.class, Game.class, ReleaseDate
                .class, Main.class);

        warArchive.addAsWebInfResource(new ClassLoaderAsset
                ("test-persistence.xml", GamesTest.class
                        .getClassLoader()),
                "classes/META-INF/persistence.xml");

        warArchive.addAllDependencies();

        return warArchive;
    }

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return Main.createSwarm();
    }


    @EJB
    Games games;

    @Test
    public void test() {
        System.out.println(games);
    }

}
