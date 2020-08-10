package book.comments.boundary;

import book.comments.MongoClientProvider;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.ManagedMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.arquillian.algeron.pact.provider.assertj
        .PactProviderAssertions;
import org.arquillian.algeron.pact.provider.spi.Provider;
import org.arquillian.algeron.pact.provider.spi.State;
import org.arquillian.algeron.pact.provider.spi.Target;
import org.arquillian.algeron.provider.core.retriever.ContractsFolder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;

import static com.lordofthejars.nosqlunit.mongodb.ManagedMongoDb
        .MongoServerRuleBuilder.newManagedMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb
        .MongoDbConfigurationBuilder.mongoDb;

// tag::test[]

@RunWith(Arquillian.class)
@ContractsFolder(value = "/tmp/mypacts") // <1>
@Provider("comments_service") // <2>
public class CommentsProviderTest {

    static { // <3>
        System.setProperty("MONGO_HOME",
                "/mongodb-osx-x86_64-3.2.7");
    }

    @ClassRule // <4>
    public static ManagedMongoDb managedMongoDb =
            newManagedMongoDbRule().build();

    @Rule // <5>
    public MongoDbRule remoteMongoDbRule = new MongoDbRule(mongoDb
            ().databaseName("test").host("localhost").build());

    @Deployment(testable = false) // <6>
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

        return webArchive;
    }

    private static final String commentObject = "{" + "  'comment' " +
            ": '%s'," + "  'rate' : %d," + "  'gameId': %d" + "}";

    @State("A game with id (\\d+) with rate (\\d+) and message (.+)")
    public void insertGame(int gameId, int rate, String message)
            throws MalformedURLException { // <7>

        RestAssured.given().body(toJson(String.format
                (commentObject, message, rate, gameId)))
                .contentType(ContentType.JSON).post(new URL
                (commentsService, "comments")).then().statusCode(201);

    }

    @ArquillianResource
    URL commentsService;

    @ArquillianResource // <8>
    Target target;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL) // <9>
    public void should_provide_valid_answers() {
        PactProviderAssertions.assertThat(target).withUrl
                (commentsService).satisfiesContract();
    }

    // end::test[]

    public static String toJson(String jsonString) {
        StringBuilder builder = new StringBuilder();
        boolean single_context = false;
        for (int i = 0; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);
            if (ch == '\\') {
                i = i + 1;
                if (i < jsonString.length()) {
                    ch = jsonString.charAt(i);
                    if (!(single_context && ch == '\'')) {
                        // unescape ' inside single quotes
                        builder.append('\\');
                    }
                }
            } else if (ch == '\'') {
                // Turn ' into ", for proper JSON string
                ch = '"';
                single_context = !single_context;
            }
            builder.append(ch);
        }

        return builder.toString();
    }

    // tag::test[]
}
// end::test[]