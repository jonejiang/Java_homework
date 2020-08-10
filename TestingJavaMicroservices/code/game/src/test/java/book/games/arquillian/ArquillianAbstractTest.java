package book.games.arquillian;

import book.games.boundary.ExecutorServiceProducer;
import book.games.boundary.Games;
import book.games.boundary.GamesResource;
import book.games.boundary.IgdbGateway;
import book.games.control.GamesService;
import book.games.entity.Game;
import book.games.entity.ReleaseDate;
import book.games.entity.SearchResult;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

// tag::test[]
public abstract class ArquillianAbstractTest {

    @Before
    public void before() {
        // <1>
        stubFor(get(anyUrl()).withQueryParam("search", equalTo
                ("The" + " Legend of Zelda: Breath of the Wild"))
                .willReturn(aResponse().withStatus(200).withHeader
                        ("Content-Type", "application/json")
                        .withBody("[{\"id\":7346,\"name\":\"The " +
                                "Legend of Zelda: Breath of the " +
                                "Wild\"}]")));
    }

    // <2>
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8071);

    // <3>
    public static WebArchive createBaseDeployment(final String name) {
        return ShrinkWrap.create(WebArchive.class, name + ".war")
                .addClasses(GamesResource.class,
                        ExecutorServiceProducer.class, GamesService
                                .class, IgdbGateway.class,
                        SearchResult.class, Games.class, Game
                                .class, ReleaseDate.class)
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans" +
                        ".xml");
    }
}
// end::test[]
