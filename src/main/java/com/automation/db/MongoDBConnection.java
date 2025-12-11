package com.automation.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
public class MongoDBConnection implements DatabaseConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private boolean connected = false;

    @Override
    public void connect(String connectionString, Map<String, String> properties) {
        try {
            mongoClient = MongoClients.create(connectionString);
            String dbName = properties.getOrDefault("database", "testdb");
            database = mongoClient.getDatabase(dbName);
            connected = true;
            log.info("Successfully connected to MongoDB: {}", dbName);
        } catch (Exception e) {
            log.error("Failed to connect to MongoDB", e);
            throw new RuntimeException("MongoDB connection failed", e);
        }
    }

    @Override
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            connected = false;
            log.info("MongoDB connection closed");
        }
    }

    @Override
    public Object executeQuery(String collectionName) {
        if (!connected) {
            throw new IllegalStateException("Not connected to MongoDB");
        }
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find().into(new java.util.ArrayList<>());
    }

    @Override
    public void executeUpdate(String query) {
        log.info("Executing MongoDB update");
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

}
