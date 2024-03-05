package com.github.golgolex.anyth;

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

import com.github.golgolex.anyth.connection.AbstractConnectionInitializer;
import com.github.golgolex.anyth.layer.LayerBase;
import com.github.golgolex.anyth.repository.AbstractDataSessionDirector;
import com.github.golgolex.anyth.repository.AbstractRepository;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Anyth {

    public static Logger LOGGER = Logger.getLogger("Anyth");

    @Getter
    private static final List<LayerBase<?,?>> defaultLayers = new ArrayList<>();

    private static final Map<String, AbstractConnectionInitializer<?, ?>> connectionBuilders = new ConcurrentHashMap<>(0);

    /**
     * Initializes a connection initializer and adds it to the manager.
     *
     * @param initializer The connection initializer to be initialized and added.
     * @param <R>         The type of repository associated with the connection initializer.
     * @param <D>         The type of data session director associated with the connection initializer.
     * @param <C>         The type of connection initializer.
     *
     * This method logs the initialization of the provided connection initializer and adds it
     * to the manager's map, using the class name as the key.
     */
    public static <R extends AbstractRepository<?, ?, ?, ?>,
            D extends AbstractDataSessionDirector<R>,
            C extends AbstractConnectionInitializer<R, D>> void initConnectionInitializer(C initializer) {
        LOGGER.log(Level.INFO, "Initialized ConnectionInitializer [initializer=" + initializer
                .getClass()
                .getSimpleName() + "]");
        connectionBuilders.put(initializer
                .getClass()
                .getName(), initializer);
    }

    /**
     * Retrieves a connection initializer based on its class.
     *
     * @param connectionInitializer The class type of the connection initializer to be retrieved.
     * @param <R>     The type of repository associated with the connection initializer.
     * @param <D>     The type of data session director associated with the connection initializer.
     * @param <C>     The type of connection initializer.
     * @return The connection initializer instance of the specified class, or null if not found.
     *
     * This method checks if a connection initializer with the provided class name exists in the
     * manager's map and is assignable from the specified class type. If true, it returns the
     * connection initializer instance; otherwise, it returns null.
     */
    public static <R extends AbstractRepository<?, ?, ?, ?>,
            D extends AbstractDataSessionDirector<R>,
            C extends AbstractConnectionInitializer<R, D>> C getConnectionInitializer(Class<C> connectionInitializer) {
        if (connectionBuilders
                .containsKey(connectionInitializer.getName()) &&
                connectionBuilders.get(connectionInitializer.getName())
                                                        .getClass()
                                                        .isAssignableFrom(connectionInitializer)) {
            return connectionInitializer.cast(connectionBuilders.get(connectionInitializer.getName()));
        } else {
            return null;
        }
    }
}