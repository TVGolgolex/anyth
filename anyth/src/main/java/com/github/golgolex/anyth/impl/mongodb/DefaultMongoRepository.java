package com.github.golgolex.anyth.impl.mongodb;

/*
 * Copyright 2024 anyth contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.github.golgolex.anyth.repository.AbstractRepository;
import lombok.Getter;
import lombok.NonNull;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class DefaultMongoRepository extends AbstractRepository<MongoLayerBase<?>,
                                                        Bson,
                                                        Document,
        DocumentLayerSerializer> {

    private final MongoCollection<Document> mongoCollection;

    public DefaultMongoRepository(String repositoryName,
                                  MongoCollection<Document> mongoCollection) {
        super(repositoryName, new DocumentLayerSerializer());
        this.mongoCollection = mongoCollection;
    }

    @Override
    public Document serializerObject(@NonNull Object object) {
        return this.getLayerSerializer()
                .encode(object,
                        this.getLayers()
                                .values()
                                .stream()
                                .map(mongoLayerBase -> (MongoLayerBase<Object>) mongoLayerBase)
                                .collect(Collectors.toList()),
                        this.getCommonLayer().values());
    }

    @Override
    public <T> T deserializeObject(@NonNull Document serialized, @NonNull Class<T> clazz) {
        return (T) this.getLayerSerializer()
                .decode(serialized,
                        this.getLayers()
                                .values()
                                .stream()
                                .map(mongoLayerBase -> (MongoLayerBase<Object>) mongoLayerBase)
                                .collect(Collectors.toList()),
                        clazz,
                        this.getCommonLayer().values());
    }

    @Override
    public <T> T query(@NonNull Bson bson, @NonNull Class<T> clazz) {
        Document document = query(bson);
        if (document == null) {
            return null;
        }
        return deserializeObject(document, clazz);
    }

    @Override
    public Document query(@NonNull Bson bson)
    {
        Document document = this.mongoCollection.find(bson).first();

        if (document == null)
        {
            for (Document d : this.mongoCollection.find())
            {
                if (d.containsKey("bson_filter"))
                {
                    for (Document document1 : this.mongoCollection.find(new Document("bson_filter", new Document("$exists", true)))) {
                        if (document1.get("bson_filter", Document.class).toJson().equals(bson.toBsonDocument(this.mongoCollection.getDocumentClass(), this.mongoCollection.getCodecRegistry()).toJson())) {
                            document = document1;
                        }
                    }
                    break;
                }
            }
        }

        return document;
    }

    @Override
    public <T> T asyncQuery(@NonNull Bson bson, @NonNull Class<T> clazz) {
        return this.query(bson, clazz);
    }

    @Override
    public <T> boolean replace(@NonNull Bson bson, @NonNull T object)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = mongoCollection.replaceOne(query(bson), this.serializerObject(object));
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    @Override
    public <T> boolean asyncReplace(@NonNull Bson bson, @NonNull T object)
    {
        return replace(bson, object);
    }

    @Override
    public boolean replaceSerialized(@NonNull Bson bson, @NonNull Document document)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = mongoCollection.replaceOne(query(bson), document);
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean asyncReplaceSerialized(@NonNull Bson bson, @NonNull Document document)
    {
        return replaceSerialized(bson, document);
    }

    /* ======================================================================================= */

    /**
     * Replaces an object in the data source based on the specified BSON filter and options synchronously.
     *
     * @param bson           The BSON filter to determine the object to be replaced.
     * @param object         The object to replace the existing one.
     * @param replaceOptions The options for the replace operation.
     * @param <T>            The generic type of the object.
     * @return true if the replacement was successful, false otherwise.
     */
    public <T> boolean replace(@NonNull Bson bson, @NonNull T object, @NonNull ReplaceOptions replaceOptions)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = mongoCollection.replaceOne(query(bson), this.serializerObject(object), replaceOptions);
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    /**
     * Replaces an object in the data source based on the specified BSON filter and options asynchronously.
     *
     * @param bson           The BSON filter to determine the object to be replaced.
     * @param object         The object to replace the existing one asynchronously.
     * @param replaceOptions The options for the replace operation.
     * @param <T>            The generic type of the object.
     * @return true if the asynchronous replacement was successful, false otherwise.
     */
    public <T> boolean asyncReplace(@NonNull Bson bson, @NonNull T object, @NonNull ReplaceOptions replaceOptions)
    {
        return replace(bson, object, replaceOptions);
    }

    /**
     * Replaces a serialized object in the data source based on the specified BSON filter and options.
     *
     * @param bson           The BSON filter to determine the location of the serialized object to be replaced.
     * @param document       The new serialized object to replace the existing one.
     * @param replaceOptions The options for the replace operation.
     * @return true if the replacement was successful, false otherwise.
     */
    public boolean replaceSerialized(@NonNull Bson bson, @NonNull Document document, @NonNull ReplaceOptions replaceOptions)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = mongoCollection.replaceOne(query(bson), document, replaceOptions);
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    /**
     * Replaces a serialized object in the data source based on the specified BSON filter and options asynchronously.
     *
     * @param bson           The BSON filter to determine the location of the serialized object to be replaced.
     * @param document       The new serialized object to replace the existing one asynchronously.
     * @param replaceOptions The options for the replace operation.
     * @return true if the asynchronous replacement was successful, false otherwise.
     */
    public boolean asyncReplaceSerialized(@NonNull Bson bson, @NonNull Document document, @NonNull ReplaceOptions replaceOptions)
    {
        return replaceSerialized(bson, document, replaceOptions);
    }

    /* ======================================================================================= */

    @Override
    public Collection<Document> queryAll(Bson bson)
    {
        ArrayList<Document> documents = new ArrayList<>();

        for (Document document : this.mongoCollection.find(bson))
        {
            documents.add(document);
        }

        for (Document document : this.mongoCollection.find())
        {
            if (document.containsKey("bson_filter"))
            {
                for (Document d : this.mongoCollection.find(new Document("bson_filter", new Document("$exists", true)))) {
                    if (d.get("bson_filter", Document.class).toJson().equals(bson.toBsonDocument(this.mongoCollection.getDocumentClass(), this.mongoCollection.getCodecRegistry()).toJson())) {
                        documents.add(document);
                    }
                }
            }
        }

        return documents;
    }

    @Override
    public <T> Collection<T> queryAll(Class<T> clazz) {
        ArrayList<Document> documents = new ArrayList<>();

        for (Document document : this.mongoCollection.find()) {
            documents.add(document);
        }

        return documents
                .stream()
                .map(document -> this.deserializeObject(document, clazz))
                .toList();
    }

    @Override
    public Collection<Document> queryAll()
    {
        Collection<Document> collection = new ArrayList<>();
        for (Document document : this.mongoCollection.find()) {
            collection.add(document);
        }
        return collection;
    }

    @Override
    public <T> boolean insert(Bson bson, @NonNull T object) {
        Document document = this.serializerObject(object);
        if (document != null) {
            if (bson != null) {
                if (exist(bson)) {
                    return false;
                }
                document.put("bson_filter", bson);
            }
            this.mongoCollection.insertOne(document);
        }
        return document != null;
    }

    @Override
    public <T> boolean asyncInsert(Bson bson, @NonNull T object) {
        return insert(bson, object);
    }

    @Override
    public boolean insertSerialized(Bson bson, @NonNull Document document)
    {
        if (bson != null)
        {
            if (exist(bson))
            {
                return false;
            }
            document.put("bson_filter", bson);
        }
        this.mongoCollection.insertOne(document);
        return true;
    }

    @Override
    public boolean asyncInsertSerialized(Bson bson, @NonNull Document document)
    {
        return insertSerialized(bson, document);
    }

    @Override
    public boolean delete(@NonNull Bson bson) {
        DeleteResult deleteResult = this.mongoCollection.deleteOne(bson);
        return deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() > 0;
    }

    @Override
    public boolean asyncDelete(@NonNull Bson bson) {
        return delete(bson);
    }

    @Override
    public <T> boolean applyChanges(@NonNull Bson bson, @NonNull T object)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = this.mongoCollection.updateOne(query(bson), new Document("$set",
                this.serializerObject(object)));
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean applyChangesSerialized(@NonNull Bson bson, @NonNull Document document)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = this.mongoCollection.updateOne(query(bson), new Document("$set", document));
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean applyAsyncChangesSerialized(@NonNull Bson bson, @NonNull Document document)
    {
        return applyChangesSerialized(bson, document);
    }

    @Override
    public <T> boolean applyAsyncChanges(@NonNull Bson bson, @NonNull T object)
    {
        return applyChanges(bson, object);
    }

    /* ======================================================================================= */

    public <T> boolean applyChanges(@NonNull Bson bson, @NonNull T object, @NonNull UpdateOptions updateOptions)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = this.mongoCollection.updateOne(query(bson), new Document("$set", this.serializerObject(object)), updateOptions);
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    public boolean applyChangesSerialized(@NonNull Bson bson, @NonNull Document document, @NonNull UpdateOptions updateOptions)
    {
        if (!exist(bson))
        {
            return false;
        }
        UpdateResult updateResult = this.mongoCollection.updateOne(query(bson), new Document("$set", document), updateOptions);
        return updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0;
    }

    public boolean applyAsyncChangesSerialized(@NonNull Bson bson, @NonNull Document document, @NonNull UpdateOptions updateOptions)
    {
        return applyChangesSerialized(bson, document, updateOptions);
    }

    public <T> boolean applyAsyncChanges(@NonNull Bson bson, @NonNull T object, @NonNull UpdateOptions updateOptions)
    {
        return applyChanges(bson, object, updateOptions);
    }

    /* ======================================================================================= */

    @Override
    public boolean exist(Bson bson) {
        Document document = this.mongoCollection.find(bson).first();

        if (document != null) {
            return true;
        }

        for (Document d : this.mongoCollection.find(new Document("bson_filter", new Document("$exists", true)))) {
            if (d.get("bson_filter", Document.class).toJson().equals(bson.toBsonDocument(this.mongoCollection.getDocumentClass(), this.mongoCollection.getCodecRegistry()).toJson())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean asyncExist(@NonNull Bson bson)
    {
        return exist(bson);
    }

    @Override
    public boolean clear(@NonNull Bson bson)
    {
        if (!this.exist(bson)) {
            return false;
        }

        long deleted = 0;

        for (Document document : this.queryAll(bson))
        {
            DeleteResult deleteResult = this.mongoCollection.deleteOne(document);
            if (deleteResult.wasAcknowledged())
            {
                deleted = deleted + deleteResult.getDeletedCount();
            }
        }

        return deleted > 0;
    }

    @Override
    public boolean asyncClear(@NonNull Bson bson)
    {
        return clear(bson);
    }

    @Override
    public boolean clear()
    {
        long deleted = 0;

        for (Document document : this.queryAll())
        {
            DeleteResult deleteResult = this.mongoCollection.deleteOne(document);
            if (deleteResult.wasAcknowledged())
            {
                deleted = deleted + deleteResult.getDeletedCount();
            }
        }
        return deleted > 0;
    }

    @Override
    public boolean asyncClear()
    {
        return clear();
    }

    @Override
    public boolean isEmpty() {
        return this.mongoCollection.countDocuments() < 1;
    }

    private boolean areEqual(BsonValue value1, BsonValue value2) {
        if (value1 == null || value2 == null) {
            return value1 == value2;
        }

        if (value1.getBsonType() != value2.getBsonType()) {
            return false;
        }

        switch (value1.getBsonType()) {
            case NULL:
                return true;

            case BOOLEAN:
                return ((BsonBoolean) value1).getValue() == ((BsonBoolean) value2).getValue();

            case INT32:
                return ((BsonInt32) value1).getValue() == ((BsonInt32) value2).getValue();

            case INT64:
                return ((BsonInt64) value1).getValue() == ((BsonInt64) value2).getValue();

            case DOUBLE:
                return ((BsonDouble) value1).getValue() == ((BsonDouble) value2).getValue();

            case STRING:
                return ((BsonString) value1).getValue().equals(((BsonString) value2).getValue());

            case OBJECT_ID:
                return ((BsonObjectId) value1).getValue().equals(((BsonObjectId) value2).getValue());

            case BINARY:
                return ((BsonBinary) value1).getData().equals(((BsonBinary) value2).getData());

            case DOCUMENT:
                return areEqual((BsonDocument) value1, (BsonDocument) value2);

            case ARRAY:
                return areEqual((BsonArray) value1, (BsonArray) value2);

            default:
                return false;
        }
    }

    private boolean areEqual(BsonDocument doc1, BsonDocument doc2) {
        return doc1.equals(doc2);
    }

    private boolean areEqual(BsonArray arr1, BsonArray arr2) {
        return arr1.equals(arr2);
    }
}