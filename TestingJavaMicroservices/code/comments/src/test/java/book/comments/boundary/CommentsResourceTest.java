package book.comments.boundary;

import book.comments.MongoClientProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client
        .ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

// tag::test[]
@RunWith(Arquillian.class) // <1>
public class CommentsResourceTest {

    @Deployment(testable = false) // <2>
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive
                .class).addPackage(CommentsResource.class
                .getPackage()).addClass(MongoClientProvider.class)
                .addAsWebInfResource("test-resources.xml",
                        "resources.xml").addAsWebInfResource
                        (EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(Maven.resolver().resolve("org" +
                        ".mongodb:mongodb-driver:3.2.2")
                        .withTransitivity().as(JavaArchive.class));

        System.out.println("webArchive = " + webArchive.toString
                (true));

        return webArchive;
    }

    @Test
    public void getCommentsOfGivenGame(@ArquillianResteasyResource
                                           final CommentsResource
                                                   resource) throws
            Exception { // <3>
        Assert.assertNotNull(resource);

        final Response game = resource.getCommentsOfGivenGame(1);
        // <4>
        Assert.assertNotNull(game);
    }

}
// end::test[]