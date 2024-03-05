package com.github.golgolex.anyth.impl.redis;

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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionInitializer extends AbstractConnectionInitializer<
        DefaultRedisRepository,
        RedisDataSessionDirector> {

    @Override
    public void terminate() {
        this.getDataSessionDirectors().forEach((s, redisDataSessionDirector) -> {
            redisDataSessionDirector.close();
        });
    }

    public RedisDataSessionDirector connect(String key, String host,
                        int port,
                        String auth) {

        if (getDataSessionDirectors().containsKey(key.toUpperCase())) {
            return getDataSessionDirectors().get(key.toUpperCase());
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        try (JedisPool jedisPool = new JedisPool(poolConfig, host, port, 2000, auth)) {

            try (Jedis jedis = jedisPool.getResource()) {
                RedisDataSessionDirector redisDataSessionDirector = new RedisDataSessionDirector(key, jedisPool, jedis);
                getDataSessionDirectors().put(key.toUpperCase(), redisDataSessionDirector);
                return redisDataSessionDirector;
            }
        }

    }
}
