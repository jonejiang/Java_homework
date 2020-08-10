package book.web;

import book.web.page.Detail;
import book.web.page.Index;
import book.web.page.List;
import book.web.rule.DefaultJavaResolutionStrategy;
import book.web.rule.MicroserviceRule;
import book.web.rule.MongodRule;
import book.web.rule.RedisRule;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ArquillianTooManyDeployment")
@RunWith(Arquillian.class)
public class EndToEndTest {

    // tag::rules[]
    @ClassRule
    public static RuleChain chain = RuleChain // <1>
            .outerRule(new MongodRule("localhost", 27017)) // <2>
            .around(new RedisRule(6379)) // <3>
            .around(new MicroserviceRule("http://localhost:8899/?videoId=5123&gameName=Zelda").withExecutableJar
                    (getFile("video/build/libs/video-service-0.1.0.jar"), "--server.port=8899")
                    .withJavaResolutionStrategy(new DefaultJavaResolutionStrategy()).withTimeout(1, TimeUnit.MINUTES)
            ) // <4>

            .around(new MicroserviceRule("http://localhost:8181?query=").withExecutableJar(getFile
                    ("game/target/gameservice-swarm.jar"), "-Dswarm" + ".http.port=8181").withJavaResolutionStrategy
                    (new DefaultJavaResolutionStrategy()).withTimeout(1, TimeUnit.MINUTES));
    // end::rules[]

    // tag::deployments[]
    @Deployment(name = "commentsservice", testable = false)
    @TargetsContainer("commentsservice")
    public static Archive commentsservice() throws Exception {

        // Could use the EmbeddedGradleImporter.class to import the
        // project output, this way we know we're using the real deal.
        // See book.aggr.CommentsGatewayTest#createCommentsDeployment

        return ShrinkWrap.create(ZipImporter.class, "commentsservice.war").importFrom(getFile
                ("comments/build/libs/commentsservice.war")).as(WebArchive.class).addAsLibraries(Maven.resolver()
                .resolve("org.mongodb:mongodb-driver:3.2.2").withTransitivity().as(JavaArchive.class)).addClass
                (MongoClientProvider.class).addAsWebInfResource("test-web.xml", "web.xml").addAsWebInfResource
                ("test-resources.xml", "resources.xml");
    }

    @Deployment(name = "gameaggregatorservice", testable = false)
    @TargetsContainer("gameaggregatorservice")
    public static Archive gameaggregatorservice() throws Exception {
        return ShrinkWrap.create(ZipImporter.class, "gameaggregatorservice.war").importFrom(getFile
                ("aggregator/build/libs/gameaggregatorservice.war")).as(WebArchive.class).addAsLibraries(Maven
                .resolver().resolve("org.mongodb:mongodb-driver:3.2.2").withTransitivity().as(JavaArchive.class))
                .addClass(MongoClientProvider.class).addAsWebInfResource("test-web.xml", "web.xml")
                .addAsWebInfResource("test-resources.xml", "resources.xml");
    }

    @Deployment(name = "gamerweb", testable = false) // <1>
    @TargetsContainer("gamerweb") // <2>
    public static Archive gamerWebService() throws Exception {
        //This is the parent project of this test
        return ShrinkWrap.create(MavenImporter.class).loadPomFromFile("pom.xml").importBuildOutput().as(WebArchive
                .class).addAsWebInfResource("test-web.xml", "web.xml");
    }
    // end::deployments[]

    // tag::operate[]
    private static final AtomicReference<URL> commentsservice = new AtomicReference<>();
    private static final AtomicReference<URL> gameaggregatorservice = new AtomicReference<>();
    private static final AtomicReference<URL> gamerweb = new AtomicReference<>();

    @Test
    @InSequence(1)
    @OperateOnDeployment("commentsservice")
    public void testRunningInCommentsService(@ArquillianResource final URL url) throws Exception {
        commentsservice.set(url);
        Assert.assertNotNull(commentsservice.get());
        assertThat(commentsservice.get().toExternalForm(), containsString("commentsservice"));
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment("gameaggregatorservice")
    public void testRunningInGameAggregatorService(@ArquillianResource final URL url) throws Exception {
        gameaggregatorservice.set(url);
        Assert.assertNotNull(gameaggregatorservice.get());
        assertThat(gameaggregatorservice.get().toExternalForm(), containsString("gameaggregatorservice"));
    }

    @Test
    @InSequence(4)
    @OperateOnDeployment("gamerweb")
    public void testRunningInGamerWeb(@ArquillianResource final URL url) throws Exception {
        gamerweb.set(url);
        Assert.assertNotNull(gamerweb.get());
        assertThat(gamerweb.get().toExternalForm(), containsString("gamerweb"));
    }
    // end::operate[]

    // tag::exes[]
    @Test
    @InSequence(5)
    public void testSpringBootVideoService() throws Exception {
        Assert.assertTrue(connect("http://localhost:8899/?videoId=5123&gameName" + "=Zelda"));
    }

    @Test
    @InSequence(6)
    public void testWildFlyGameService() throws Exception {
        Assert.assertTrue(connect("http://localhost:8181?query="));
    }
    //end::exes[]

    // tag::drone[]
    @Page
    @OperateOnDeployment("gamerweb")
    private Index page; // <1>

    @Test
    @InSequence(7)
    public void testTheUI() throws Exception {

        //Just nice to see we have access to this URL
        System.out.println("gameaggregatorservice = " + gameaggregatorservice.get().toExternalForm());

        // <2>
        page.navigateTo(gamerweb.get().toExternalForm());
        List list = page.searchFor("Zelda");

        Assert.assertEquals("", "Zelda", page.getSearchText());

        Assert.assertThat(list.getResults(), hasItems("The Legend of Zelda: Breath of the Wild"));

        Detail detail = list.getDetail(0);

        Assert.assertTrue(detail.getImageURL().startsWith("http"));

        // <3>
        //Just here for debugging & development :: curl
        // localhost:9999 to terminate
        if (null != System.getProperty("dev.hack")) {
            new ServerSocket(9999).accept();
        }
    }
    // end::drone[]

    /**
     * Simple utility method to locate the pre-built microservice
     * war files running in the context of either maven or the IDE
     *
     * @param path Path to war file
     * @return File
     */
    private static synchronized File getFile(final String path) {

        try {
            File f = new File("../code");
            if (f.exists()) {
                f = new File(f, path);
                return getFile(f);
            }

            f = new File(".").getCanonicalFile();

            if (f.getName().contains("web")) {
                f = new File(f.getParent(), path);
            }

            return getFile(f);
        } catch (final IOException ioe) {
            throw new RuntimeException("Failed to get war: " + path, ioe);
        }
    }

    /**
     * Ensures the file is accessible.
     *
     * @param f File to check
     * @return Checked file
     */
    private static File getFile(final File f) {
        Assert.assertTrue("Failed to find: " + f.toString(), f.exists());
        Assert.assertTrue("Please ensure that the service has been " + "" + "" + "built", f.exists());
        Assert.assertTrue("Unable to set readable", f.setReadable(true));
        Assert.assertTrue("File is not readable", f.canRead());
        Assert.assertTrue("Unable to set executable", f.setExecutable(true));
        Assert.assertTrue("File is not executable", f.canExecute());
        return f;
    }

    /**
     * Simple connect method to test for a known endpoint
     *
     * @param url A known endpoint
     * @return true if able to connect, else false
     */
    private static boolean connect(final String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return new OkHttpClient().newCall(request).execute().isSuccessful();
    }
}
