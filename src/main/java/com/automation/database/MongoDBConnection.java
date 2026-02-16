package com.automation.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB Connection Implementation
 *
 * Query Format (JSON):
 * {
 *   "collection": "collectionName",
 *   "filter": { "field": "value" }  // optional, defaults to {}
 * }
 *
 * Update Format (JSON):
 * {
 *   "collection": "collectionName",
 *   "operation": "insert|update|delete",
 *   "document": { ... },          // for insert
 *   "filter": { ... },            // for update/delete
 *   "update": { "$set": { ... } } // for update
 * }
 *
 * @author Victor Grozev
 */
@Slf4j
public class MongoDBConnection implements DatabaseConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private boolean connected = false;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    /**
     * Execute a MongoDB find query.
     *
     * Query format (JSON):
     * {
     *   "collection": "collectionName",
     *   "filter": { "field": "value" }
     * }
     *
     * For backward compatibility, a simple collection name is also supported.
     *
     * @param query JSON query string or collection name
     * @return List of Documents matching the query
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to MongoDB");
        }

        try {
            String collectionName;
            Document filter = new Document();

            // Check if query is JSON or simple collection name
            if (query.trim().startsWith("{")) {
                // Parse JSON query
                Map<String, Object> queryMap = objectMapper.readValue(query, Map.class);
                collectionName = (String) queryMap.get("collection");
                if (collectionName == null) {
                    throw new IllegalArgumentException("Query must specify 'collection'");
                }

                Object filterObj = queryMap.get("filter");
                if (filterObj instanceof Map) {
                    filter = new Document((Map<String, Object>) filterObj);
                }
            } else {
                // Backward compatibility: treat as collection name
                collectionName = query;
            }

            MongoCollection<Document> collection = database.getCollection(collectionName);
            List<Document> results = collection.find(filter).into(new ArrayList<>());

            log.debug("Query on collection '{}' returned {} documents", collectionName, results.size());
            return results;

        } catch (Exception e) {
            log.error("Error executing MongoDB query: {}", query, e);
            throw new RuntimeException("MongoDB query failed", e);
        }
    }

    /**
     * Execute a MongoDB update operation (insert, update, or delete).
     *
     * Update format (JSON):
     * {
     *   "collection": "collectionName",
     *   "operation": "insert|update|delete",
     *   "document": { ... },          // for insert
     *   "filter": { ... },            // for update/delete
     *   "update": { "$set": { ... } } // for update
     * }
     *
     * @param updateCommand JSON update command
     */
    @Override
    @SuppressWarnings("unchecked")
    public void executeUpdate(String updateCommand) {
        if (!connected) {
            throw new IllegalStateException("Not connected to MongoDB");
        }

        try {
            Map<String, Object> commandMap = objectMapper.readValue(updateCommand, Map.class);
            String collectionName = (String) commandMap.get("collection");
            String operation = (String) commandMap.get("operation");

            if (collectionName == null || operation == null) {
                throw new IllegalArgumentException("Update command must specify 'collection' and 'operation'");
            }

            MongoCollection<Document> collection = database.getCollection(collectionName);

            switch (operation.toLowerCase()) {
                case "insert" -> {
                    Map<String, Object> docMap = (Map<String, Object>) commandMap.get("document");
                    Document doc = new Document(docMap);
                    InsertOneResult result = collection.insertOne(doc);
                    log.info("Inserted document with ID: {}", result.getInsertedId());
                }
                case "update" -> {
                    Map<String, Object> filterMap = (Map<String, Object>) commandMap.get("filter");
                    Map<String, Object> updateMap = (Map<String, Object>) commandMap.get("update");
                    Document filter = new Document(filterMap);
                    Document update = new Document(updateMap);
                    UpdateResult result = collection.updateMany(filter, update);
                    log.info("Updated {} documents", result.getModifiedCount());
                }
                case "delete" -> {
                    Map<String, Object> filterMap = (Map<String, Object>) commandMap.get("filter");
                    Document filter = new Document(filterMap);
                    DeleteResult result = collection.deleteMany(filter);
                    log.info("Deleted {} documents", result.getDeletedCount());
                }
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            }

        } catch (Exception e) {
            log.error("Error executing MongoDB update: {}", updateCommand, e);
            throw new RuntimeException("MongoDB update failed", e);
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Get the underlying MongoDB database instance.
     * Use this for advanced MongoDB operations not covered by the interface.
     *
     * @return MongoDatabase instance
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Get a collection by name.
     * Convenience method for direct collection access.
     *
     * @param collectionName Collection name
     * @return MongoCollection instance
     */
    public MongoCollection<Document> getCollection(String collectionName) {
        if (!connected) {
            throw new IllegalStateException("Not connected to MongoDB");
        }
        return database.getCollection(collectionName);
    }

    /**
     * Find all documents in a collection.
     *
     * @param collectionName Collection name
     * @return List of all documents
     */
    public List<Document> findAll(String collectionName) {
        return getCollection(collectionName).find().into(new ArrayList<>());
    }

    /**
     * Find documents matching a filter.
     *
     * @param collectionName Collection name
     * @param filter Filter criteria
     * @return List of matching documents
     */
    public List<Document> find(String collectionName, Bson filter) {
        return getCollection(collectionName).find(filter).into(new ArrayList<>());
    }

    /**
     * Insert a document into a collection.
     *
     * @param collectionName Collection name
     * @param document Document to insert
     * @return Insert result
     */
    public InsertOneResult insertOne(String collectionName, Document document) {
        return getCollection(collectionName).insertOne(document);
    }

    /**
     * Update documents matching a filter.
     *
     * @param collectionName Collection name
     * @param filter Filter criteria
     * @param update Update operations
     * @return Update result
     */
    public UpdateResult updateMany(String collectionName, Bson filter, Bson update) {
        return getCollection(collectionName).updateMany(filter, update);
    }

    /**
     * Delete documents matching a filter.
     *
     * @param collectionName Collection name
     * @param filter Filter criteria
     * @return Delete result
     */
    public DeleteResult deleteMany(String collectionName, Bson filter) {
        return getCollection(collectionName).deleteMany(filter);
    }
}
