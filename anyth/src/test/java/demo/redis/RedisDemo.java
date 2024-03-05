package demo.redis;

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

import com.github.golgolex.anyth.impl.redis.DefaultRedisRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDemo {

    private DefaultRedisRepository defaultRedisRepository;

    public RedisDemo(String host, int port, String auth) {
        JedisPool redisPool = new JedisPool(host, port);
        Jedis redisClient = redisPool.getResource();
        redisClient.auth(auth);

        defaultRedisRepository = new DefaultRedisRepository("test", redisClient);
    }

    public static void main(String[] args) {

        RedisDemo redisDemo = new RedisDemo(
                "",
                6379,
                ""
        );

        redisDemo.defaultRedisRepository.applyLayer(TestCommonRedisLayer.class);

        redisDemo.defaultRedisRepository.insert("test", new RedisTestClass(new RedisTestClass(new RedisTestClass())));

        RedisTestClass redisTestClass = redisDemo.defaultRedisRepository.query("test", RedisTestClass.class);

        System.out.println(redisTestClass.getTest());
        System.out.println(redisTestClass.getHurensohne());
        System.out.println(redisTestClass.isKey());

        System.out.println(redisTestClass.getMongoTestClass().getTest());
        System.out.println(redisTestClass.getMongoTestClass().getHurensohne());
        System.out.println(redisTestClass.getMongoTestClass().isKey());

    }

}
