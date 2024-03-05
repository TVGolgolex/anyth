package com.github.golgolex.anyth.connection;

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

import com.github.golgolex.anyth.repository.AbstractDataSessionDirector;
import com.github.golgolex.anyth.repository.AbstractRepository;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractConnectionInitializer<R extends AbstractRepository<?, ?, ?, ?>, D extends AbstractDataSessionDirector<R>> {

    private final Map<String, D> dataSessionDirectors = new HashMap<>();

    /**
     * Retrieves a data session director associated with the provided key.
     *
     * @param key The key associated with the desired data session director.
     * @return The data session director associated with the key, or null if not found.
     *
     * This method performs a case-insensitive lookup by converting the provided key to uppercase
     * and using it to retrieve the associated data session director from the map.
     */
    public D getDirector(String key) {
        return this.dataSessionDirectors.getOrDefault(key.toUpperCase(), null);
    }

    /**
     * Abstract method to terminate the data manager.
     *
     * This method, when implemented by subclasses, should define the logic to terminate
     * the data manager, which may involve releasing resources or performing cleanup operations.
     */
    public abstract void terminate();

}