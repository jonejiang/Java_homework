package book.aggr;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.json.jaxrs.JsonStructureBodyReader;
import org.glassfish.json.jaxrs.JsonStructureBodyWriter;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class GameAggregatorApplication extends ResourceConfig {

    public GameAggregatorApplication() {
        packages("book.aggr");
        register(JsonStructureBodyWriter.class);
        register(JsonStructureBodyReader.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(GamesGateway.class).to(GamesGateway.class);
                bind(CommentsGateway.class).to(CommentsGateway.class);
            }
        });

    }



}
