package book.comments.boundary;


import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;

//DataMapper
@ApplicationScoped
public class DocumentToJsonObject {

    public JsonObject transform(final Document document) {

        if (document.containsKey("comments") && document
                .containsKey("rate")) {

            final JsonArrayBuilder messagesJsonBuilder = Json
                    .createArrayBuilder();
            final List<String> comments = (List<String>) document
                    .get("comments");
            comments.forEach(messagesJsonBuilder::add);

            final JsonObjectBuilder commentsJsonBuilder = Json
                    .createObjectBuilder().add("rate", document
                            .getDouble("rate")).add("total",
                            document.getInteger("count")).add
                            ("comments", messagesJsonBuilder);

            return commentsJsonBuilder.build();
        } else {
            return Json.createObjectBuilder().build();
        }
    }

    public Document transform(final JsonObject comment) {
        if (comment.containsKey("comment") && comment.containsKey
                ("rate") && comment.containsKey("gameId")) {

            return new Document("comment", comment.getString
                    ("comment")).append("rate", comment.getInt
                    ("rate")).append("gameId", comment.getInt
                    ("gameId"));
        } else {
            throw new IllegalArgumentException("A comments does not" +
                    " contains the mandatory fields comment and " +
                    "rate.");
        }
    }
}
