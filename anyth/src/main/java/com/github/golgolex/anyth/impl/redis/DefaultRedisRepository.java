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

import com.github.golgolex.anyth.repository.AbstractRepository;
import lombok.Getter;
import lombok.NonNull;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class DefaultRedisRepository extends AbstractRepository<RedisLayerBase<?>,
                                                        String,
                                                        String,
                                                        GsonRedisLayerSerializer> {

    private final Jedis jedis;

    public DefaultRedisRepository(String repositoryName, Jedis jedis) {
        super(repositoryName, new GsonRedisLayerSerializer());
        this.jedis = jedis;
    }

    @Override
    public String serializerObject(@NonNull Object object) {
        return this.getLayerSerializer()
                .encode(object,
                        this.getLayers()
                                .values()
                                .stream()
                                .map(redisLayerBase -> (RedisLayerBase<Object>) redisLayerBase)
                                .collect(Collectors.toList()),
                        this.getCommonLayer().values());
    }

    @Override
    public <T> T deserializeObject(@NonNull String serialized, @NonNull Class<T> clazz) {
        return (T) this.getLayerSerializer()
                .decode(serialized,
                        this.getLayers()
                                .values()
                                .stream()
                                .map(redisLayerBase -> (RedisLayerBase<Object>) redisLayerBase)
                                .collect(Collectors.toList()),
                        clazz,
                        this.getCommonLayer().values());
    }

    @Override
    public <T> T query(@NonNull String s, @NonNull Class<T> clazz) {
        String values = jedis.get(s);

        if (values == null) {
            return null;
        }

        return deserializeObject(values, clazz);
    }

    @Override
    public String query(@NonNull String s) {
        return jedis.get(s);
    }

    @Override
    public <T> T asyncQuery(@NonNull String s, @NonNull Class<T> clazz) {
        return query(s, clazz);
    }

    @Override
    public <T> boolean replace(@NonNull String string, @NonNull T object)
    {
        if (!exist(string))
        {
            return false;
        }
        jedis.set(string, serializerObject(object));
        return false;
    }

    @Override
    public <T> boolean asyncReplace(@NonNull String string, @NonNull T object)
    {
        return replace(string, object);
    }

    @Override
    public boolean replaceSerialized(@NonNull String string, @NonNull String string2)
    {
        if (!exist(string))
        {
            return false;
        }
        jedis.set(string, string2);
        return false;
    }

    @Override
    public boolean asyncReplaceSerialized(@NonNull String string, @NonNull String string2)
    {
        return replaceSerialized(string, string2);
    }

    @Override
    public Collection<String> queryAll(String s) {
        return jedis.keys("*")
                .stream()
                .filter(s1 -> s1.startsWith(s))
                .collect(Collectors.toList());
    }

    @Override
    public <T> Collection<T> queryAll(Class<T> clazz) {
        return jedis.keys("*")
                .stream()
                .map(key -> deserializeObject(key, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> queryAll() {
        return jedis.keys("*");
    }

    @Override
    public <T> boolean insert(String s, @NonNull T object) {
        if (s == null) {
            throw new NullPointerException("No key for " + object.getClass().getSimpleName() + " defined");
        }

        if (jedis.exists(s)) {
            return false;
        }

        jedis.set(s, serializerObject(object));
        return true;
    }

    @Override
    public <T> boolean asyncInsert(String s, @NonNull T object) {
        return insert(s, object);
    }

    @Override
    public boolean insertSerialized(String s, @NonNull String string)
    {
        if (jedis.exists(s))
        {
            return false;
        }

        jedis.set(s, string);
        return true;
    }

    @Override
    public boolean asyncInsertSerialized(String s, @NonNull String string)
    {
        return insertSerialized(s, string);
    }

    @Override
    public boolean delete(@NonNull String s) {
        return jedis.del(s) > 0;
    }

    @Override
    public boolean asyncDelete(@NonNull String s) {
        return delete(s);
    }

    @Override
    public <T> boolean applyChanges(@NonNull String s, @NonNull T object) {
        if (!exist(s)) {
            return false;
        }
        jedis.set(s, this.serializerObject(object));
        return true;
    }

    @Override
    public boolean applyChangesSerialized(@NonNull String s, @NonNull String string)
    {
        if (!exist(s))
        {
            return false;
        }
        jedis.set(s, this.serializerObject(string));
        return true;
    }

    @Override
    public boolean applyAsyncChangesSerialized(@NonNull String s, @NonNull String string)
    {
        return applyChangesSerialized(s, string);
    }

    @Override
    public <T> boolean applyAsyncChanges(@NonNull String s, @NonNull T object) {
        return applyChanges(s, object);
    }

    @Override
    public boolean exist(@NonNull String s) {
        return jedis.exists(s);
    }

    @Override
    public boolean asyncExist(@NonNull String s) {
        return exist(s);
    }

    @Override
    public boolean clear(@NonNull String s) {
        long deleted = 0L;
        for (String string : this.queryAll(s)) {
            deleted = deleted + jedis.del(string);
        }
        return deleted > 0;
    }

    @Override
    public boolean asyncClear(@NonNull String s) {
        return clear(s);
    }

    @Override
    public boolean clear() {
        return jedis.del("*") > 0;
    }

    @Override
    public boolean asyncClear() {
        return clear();
    }

    @Override
    public boolean isEmpty() {
        return jedis.dbSize() == 0;
    }
}
