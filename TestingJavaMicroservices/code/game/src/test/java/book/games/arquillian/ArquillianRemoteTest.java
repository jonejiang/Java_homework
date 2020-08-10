package book.games.arquillian;

import book.games.boundary.IgdbGateway;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;

// tag::test[]
@RunWith(Arquillian.class)
public class ArquillianRemoteTest extends ArquillianAbstractTest {
    // <1>

    // <2>
    @Deployment
    public static WebArchive createDeployment() {
        return createBaseDeployment(ArquillianRemoteTest.class
                .getName()).addClass(ArquillianRemoteTest.class)
                .addClass(ArquillianAbstractTest.class)
                .addAsLibrary(Maven.resolver().resolve("com.github"
                        + ".tomakehurst:wiremock-standalone:2.2.1")
                        .withoutTransitivity().asSingleFile());
    }

    // <3>
    @Inject
    private IgdbGateway gateway;

    // <4>
    @Test
    public void testSearchGames() throws Exception {
        Assert.assertNotNull(this.gateway);

        final JsonArray json = gateway.searchGames("The Legend of "
                + "Zelda: Breath of the Wild");
        Assert.assertNotNull(json);

        final JsonObject game = json.getJsonObject(0);

        Assert.assertEquals("Unexpected id", 7346, game
                .getJsonNumber("id").intValue());
        Assert.assertEquals("Unexpected name", "The Legend of " +
                "Zelda: Breath of the Wild", game.getString("name"));
    }
}
//end::test[]