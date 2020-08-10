package book.comments.boundary;

import book.comments.MongoClientProvider;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import java.util.Arrays;
import java.util.Optional;

// tag::test[]
@Dependent
public class Comments {

    private static final String COMMENTS_COLLECTION = "comments";
    private static final String MATCH = "{$match:{gameId: %s}}";
    private static final String GROUP = "{ $group : " + "          " +
            "  { _id : \"$gameId\", " + "            comments: { " +
            "$push: \"$comment\" }, " + "            rate: { $avg: " +
            "\"$rate\"} " + "            count: { $sum: 1 } " + "  " +
            "          }" + "}";

    @Resource(name = "mongodb")
    private MongoClientProvider mongoDbProvider;

    private MongoCollection<Document> commentsCollection;

    @PostConstruct
    public void initComentsCollection() {
        commentsCollection = mongoDbProvider.getMongoClient()
                .getDatabase(mongoDbProvider.getDatabase())
                .getCollection(COMMENTS_COLLECTION);
    }

    public String createComment(final Document comment) {
        commentsCollection.insertOne(comment);
        return comment.getObjectId("_id").toHexString();
    }

    public Optional<Document> getCommentsAndRating(final int gameId) {
        final AggregateIterable<Document> result =
                commentsCollection.aggregate
                        (createAggregationExpression(gameId));
        return Optional.ofNullable(result.first());
    }

    private java.util.List<Document> createAggregationExpression
            (final int gameId) {
        return Arrays.asList(Document.parse(String.format(MATCH,
                gameId)), Document.parse(GROUP));
    }

}
// end::test[]
