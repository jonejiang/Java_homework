package book.comments.boundary;

import book.comments.MongoClientProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.warp.api.HttpMethod;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// tag::test[]
@WarpTest // <1>
@RunWith(Arquillian.class)
public class CommentsWarpTest {

    @BeforeClass
    public static void beforeClass() { // <2>
        // initializes the rest easy client framework
        RegisterBuiltin.register(ResteasyProviderFactory
                .getInstance());
    }

    @Deployment
    @OverProtocol("Servlet 3.0") // <3>
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

    private CommentsResource resource;

    @ArquillianResource
    private URL contextPath; // <4>

    @Before
    public void before() { // <5>
        final ResteasyClient client = new ResteasyClientBuilder()
                .build();
        final ResteasyWebTarget target = client.target(contextPath
                .toExternalForm());
        resource = target.proxy(CommentsResource.class);
    }

    @Test
    @RunAsClient // <6>
    public void getCommentsOfGivenGame() {

        Warp.initiate(() -> { // <7>

            final Response commentsOfGivenGame = resource
                    .getCommentsOfGivenGame(1);
            Assert.assertNotNull(commentsOfGivenGame);

        }).inspect(new Inspection() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext; // <8>

            @AfterServlet // <9>
            public void testGetCommentsOfGivenGame() {

                assertEquals(HttpMethod.GET, restContext
                        .getHttpRequest().getMethod()); // <10>
                assertEquals(200, restContext.getHttpResponse()
                        .getStatusCode());
                assertEquals("application/json", restContext
                        .getHttpResponse().getContentType());
                assertNotNull(restContext.getHttpResponse()
                        .getEntity());
            }
        });
    }
}
// end::test[]