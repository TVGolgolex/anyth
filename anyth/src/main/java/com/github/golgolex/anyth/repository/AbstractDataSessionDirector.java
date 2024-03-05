package com.github.golgolex.anyth.repository;

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

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractDataSessionDirector<TRepository extends AbstractRepository<?, ?, ?, ?>> {

    private final String key;

    private final Map<String, TRepository> repositories = new ConcurrentHashMap<>(0);

    public AbstractDataSessionDirector(String key)
    {
        this.key = key;
    }

    /**
     * Abstract method to close resources associated with the repository.
     *
     * This method, when implemented by subclasses, should define the logic
     * to release any resources or perform cleanup operations associated with
     * the repository. It is called when the repository is no longer needed.
     */
    public abstract void close();

    /**
     * Abstract method to retrieve a specific repository by name.
     *
     * @param name The name of the repository to retrieve.
     * @return An instance of the specified repository.
     *
     * This method, when implemented by subclasses, should define the logic
     * to retrieve a repository based on the provided name.
     *
     * @throws IllegalArgumentException If the provided name is null or invalid.
     */
    public abstract TRepository getRepository(String name);

    /**
     * Abstract method to create or retrieve a repository by name.
     *
     * @param name The name of the repository to create or retrieve.
     * @return An instance of the specified repository.
     *
     * This method, when implemented by subclasses, should define the logic
     * to create a new repository if it doesn't exist or retrieve an existing
     * repository based on the provided name.
     *
     * @throws IllegalArgumentException If the provided name is null or invalid.
     */
    public abstract TRepository createOrGetRepository(String name);

}
