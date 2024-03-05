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

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class RedisTestClass {

    private String test = "Pascal";

    private boolean key = true;

    private int hurensohne = 234;

    private UUID uuid = UUID.randomUUID();

    private List<UUID> list = new ArrayList<>();

    private List<Integer> list2 = new ArrayList<>();

    private List<String> list3 = new ArrayList<>();

    private RedisTestClass mongoTestClass;

    public RedisTestClass() {
    }

    public RedisTestClass(RedisTestClass mongoTestClass) {
        this.mongoTestClass = mongoTestClass;
        for (int i = 0; i < 5; i++) {
            list.add(UUID.randomUUID());
            list3.add("i");
            list2.add(i);
        }
    }
}
