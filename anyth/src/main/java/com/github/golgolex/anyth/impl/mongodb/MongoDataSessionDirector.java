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

import com.mongodb.client.MongoDatabase;
import com.github.golgolex.anyth.annotations.Director;
import com.github.golgolex.anyth.repository.AbstractDataSessionDirector;
import lombok.Setter;

@Setter
@Director(repository = DefaultMongoRepository.class)
public class MongoDataSessionDirector extends AbstractDataSessionDirector<DefaultMongoRepository> {

    private final MongoDatabase mongoDatabase;

    public MongoDataSessionDirector(String key,
                                    MongoDatabase mongoDatabase) {
        super(key);
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public void close() {
        // nothing
    }

    @Override
    public DefaultMongoRepository getRepository(String name) {
        return this.getRepositories().getOrDefault(name.toUpperCase(), null);
    }

    @Override
    public DefaultMongoRepository createOrGetRepository(String name) {
        DefaultMongoRepository repository = this.getRepository(name);

        if (repository != null) {
            return repository;
        }

        repository = new DefaultMongoRepository(name, mongoDatabase.getCollection(name));
        this.getRepositories().put(name, repository);
        return repository;
    }

}
