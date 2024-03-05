package demo.mongo;

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

import com.github.golgolex.anyth.impl.mongodb.DefaultMongoRepository;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import demo.TestCommonClass;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class MongoDemo {

    private final DefaultMongoRepository defaultMongoRepository;

    public MongoDemo(String host, int port, String username, String authDatabase, String password, String database) {

        // Create credentials using the provided username, authentication database, and password
        MongoCredential credential = MongoCredential.createCredential(username, authDatabase, password.toCharArray());

        // Configure MongoClientSettings with the provided credentials and connection settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder ->
                        builder.hosts(List.of(new ServerAddress(host, port))))
                .build();

        // Create a MongoClient using the settings
        MongoClient mongoClient = MongoClients.create(settings);

        // Access the specified database and collection
        MongoCollection<Document> mongoCollection = mongoClient.getDatabase(database).getCollection("test");

        // Assuming MongoRepository is a class that interacts with the MongoDB collection
        this.defaultMongoRepository = new DefaultMongoRepository("test", mongoCollection);
    }

    public static void main(String[] args) {
        MongoDemo mongoDemo = new MongoDemo("",
                27017,
                "admin",
                "admin",
                "",
                "test");

        mongoDemo.defaultMongoRepository.applyLayer(TestCommonMongoLayer.class);

        mongoDemo.defaultMongoRepository.insert(Filters.eq("testy", "Pascal"), new MongoTestClass(new MongoTestClass(new MongoTestClass(new MongoTestClass()))));

        MongoTestClass mongoTestClass = mongoDemo.defaultMongoRepository.query(Filters.eq("testy", "Pascal"), MongoTestClass.class);

        System.out.println(mongoTestClass.getTest());
        System.out.println(mongoTestClass.getHurensohne());
        System.out.println(mongoTestClass.isKey());
        System.out.println(mongoTestClass.getUuid().toString());
        System.out.println(mongoTestClass.getTestCommonClass().getId());
        for (UUID uuid : mongoTestClass.getList())
        {
            System.out.println("list: " + uuid.toString());
        }
        for (Integer uuid : mongoTestClass.getList2())
        {
            System.out.println("list2: " + uuid.toString());
        }
        for (TestCommonClass uuid : mongoTestClass.getList3())
        {
            System.out.println(uuid.getId());
        }
        mongoTestClass.getUuidStringMap().forEach((uuid, string) -> {
            System.out.println(uuid.toString() + ": " + string);
        });

        mongoTestClass.getUuidListHashMap().forEach((uuid, strings) -> {
            System.out.println(uuid.toString());
            for (String string : strings)
            {
                System.out.println(uuid.toString() + string);
            }
        });

        System.out.println(mongoTestClass.getMongoTestClass().getTest());
        System.out.println(mongoTestClass.getMongoTestClass().getHurensohne());
        System.out.println(mongoTestClass.getMongoTestClass().isKey());
        System.out.println(mongoTestClass.getMongoTestClass().getUuid().toString());
        System.out.println(mongoTestClass.getMongoTestClass().getTestCommonClass().getId());
        for (UUID uuid : mongoTestClass.getMongoTestClass()
                .getList())
        {
            System.out.println("list: " + uuid.toString());
        }
        for (Integer uuid : mongoTestClass.getMongoTestClass().getList2())
        {
            System.out.println("list2: " + uuid.toString());
        }
        for (TestCommonClass uuid : mongoTestClass.getMongoTestClass().getList3())
        {
            System.out.println(uuid.getId());
        }
        mongoTestClass.getMongoTestClass().getUuidStringMap().forEach((uuid, string) -> {
            System.out.println(uuid.toString() + ": " + string);
        });

        mongoTestClass.getMongoTestClass().getUuidListHashMap().forEach((uuid, strings) -> {
            System.out.println(uuid.toString());
            for (String string : strings)
            {
                System.out.println(uuid.toString() + string);
            }
        });

    }

}
