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

import com.github.golgolex.anyth.annotations.Director;
import com.github.golgolex.anyth.repository.AbstractDataSessionDirector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Director(repository = DefaultRedisRepository.class)
public class RedisDataSessionDirector extends AbstractDataSessionDirector<DefaultRedisRepository> {

    private final JedisPool jedisPool;

    private final Jedis jedis;

    public RedisDataSessionDirector(String key,
                                    JedisPool jedisPool,
                                    Jedis jedis) {
        super(key);
        this.jedisPool = jedisPool;
        this.jedis = jedis;
    }

    @Override
    public void close() {
        if (this.jedisPool != null) {
            this.jedisPool.close();
        }
    }

    @Override
    public DefaultRedisRepository getRepository(String name) {
        return this.getRepositories().getOrDefault(name.toUpperCase(), null);
    }

    @Override
    public DefaultRedisRepository createOrGetRepository(String name) {
        DefaultRedisRepository repository = this.getRepository(name);

        if (repository != null) {
            return repository;
        }

        repository = new DefaultRedisRepository(name, jedis);
        this.getRepositories().put(name, repository);
        return repository;
    }
}
