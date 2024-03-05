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

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.github.golgolex.anyth.connection.AbstractConnectionInitializer;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoConnectionInitializer extends AbstractConnectionInitializer<
        DefaultMongoRepository,
        MongoDataSessionDirector> {

    private final Map<String, MongoClient> clients = new HashMap<>();

    @Override
    public void terminate() {
        this.getDataSessionDirectors().forEach((s, mongoDataSessionDirector) -> mongoDataSessionDirector.close());
        clients.forEach((s, mongoClient) -> mongoClient.close());
        clients.clear();
    }

    public MongoDataSessionDirector connect(String key,
                                            String host,
                                            int port,
                                            String username,
                                            String authDatabase,
                                            String password,
                                            String database) {
        if (getDataSessionDirectors().containsKey(key.toUpperCase())) {
            return getDataSessionDirectors().get(key.toUpperCase());
        }

        MongoCredential credential = MongoCredential.createCredential(username, authDatabase, password.toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder ->
                        builder.hosts(List.of(new ServerAddress(host, port))))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);

        MongoDataSessionDirector director = new MongoDataSessionDirector("key", mongoClient.getDatabase(database));
        this.getDataSessionDirectors().put(key.toUpperCase(), director);
        this.clients.put(key.toUpperCase(), mongoClient);
        return director;
    }

    public MongoDataSessionDirector connect(String key,
                                            String connectionString,
                                            String database) {
        if (getDataSessionDirectors().containsKey(key.toUpperCase())) {
            return getDataSessionDirectors().get(key.toUpperCase());
        }

        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .codecRegistry(fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build())
                ))
                .build())) {
            MongoDataSessionDirector director = new MongoDataSessionDirector("key", mongoClient.getDatabase(database));
            this.getDataSessionDirectors().put(key.toUpperCase(), director);
            this.clients.put(key.toUpperCase(), mongoClient);
            return director;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
