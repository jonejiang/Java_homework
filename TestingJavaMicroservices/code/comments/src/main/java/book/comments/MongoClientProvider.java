package book.comments;

import com.mongodb.MongoClient;

/**
 * Provides the MongoClient
 */
public class MongoClientProvider {

    private final MongoClient mongoClient;
    private final String database;

    public MongoClientProvider(final String address, final int
            port, final String database) {
        this.mongoClient = new MongoClient(address, port);
        this.database = database;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    public String getDatabase() {
        return database;
    }
}
