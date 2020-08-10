package book.comments;

import book.comments.boundary.Comments;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.bson.Document;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static com.lordofthejars.nosqlunit.mongodb
        .MongoDbConfigurationBuilder.mongoDb;

//tag::test[]
@RunWith(Arquillian.class) // <1>
public class CommentsTest {

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive
                .class).addClasses(Comments.class,
                MongoClientProvider.class).addAsWebInfResource
                (EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(
                        // <2>
                Maven.resolver().resolve("org" +
                        ".mongodb:mongodb-driver:3.2.2", "com" +
                        ".lordofthejars:nosqlunit-mongodb:0.10.0")
                        .withTransitivity().as(JavaArchive.class))
                .addAsWebInfResource("resources-test.xml",
                        "resources.xml") // <3>
                .addAsWebInfResource("expected-insert-comments" +
                        ".json",
                        "classes/book/comments/expected-insert" +
                                "-comments.json"); // <4>


        return webArchive;
    }

    @Rule //<5>
    public MongoDbRule remoteMongoDbRule = new MongoDbRule(mongoDb
            ().databaseName("test").host("localhost").build());


    @Inject // <6>
    private Comments comments;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL) // <7>
    @ShouldMatchDataSet(location = "expected-insert-comments.json")
    // <8>
    public void shouldInsertAComment() {
        final Document document = new Document("comment", "This " +
                "Game is Awesome").append("rate", 5).append
                ("gameId", 1);

        comments.createComment(document);
    }

}
//end::test[]
