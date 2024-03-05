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

import com.github.golgolex.anyth.impl.mongodb.MongoConnectionInitializer;
import com.github.golgolex.anyth.impl.redis.RedisConnectionInitializer;

public class DefaultAnyth {

    /**
     * This constructor initializes Anyth with connection initializers.
     * <p>
     * MongoConnectionInitializer
     * RedisConnectionInitializer.
     */
    public DefaultAnyth() {
        // Initialize Anyth with a MongoConnectionInitializer
        Anyth.initConnectionInitializer(new MongoConnectionInitializer());

        // Initialize Anyth with a RedisConnectionInitializer
        Anyth.initConnectionInitializer(new RedisConnectionInitializer());
    }

    /**
     * Main method to demonstrate the usage of the DefaultAnyth class.
     *
     * This method creates an instance of DefaultAnyth, triggering the initialization
     * of Anyth with the specified connection initializers (MongoConnectionInitializer
     * and RedisConnectionInitializer).
     *
     * @param args The command-line arguments (not used in this example).
     */
    public static void main(String[] args) {
        new DefaultAnyth();
    }
}
