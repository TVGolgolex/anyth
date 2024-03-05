package demo.mongo;

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

import demo.TestCommonClass;
import lombok.Getter;

import java.util.*;

@Getter
public class MongoTestClass {

    private String test = "Pascal";

    private boolean key = true;

    private int hurensohne = 234;

    private UUID uuid = UUID.randomUUID();

    private List<UUID> list = new ArrayList<>();

    private List<Integer> list2 = new ArrayList<>();

    private List<TestCommonClass> list3 = new ArrayList<>();

    private Map<UUID, String> uuidStringMap = new HashMap<>();

    private Map<UUID, List<String>> uuidListHashMap = new HashMap<>();

    private MongoTestClass mongoTestClass;

    private TestCommonClass testCommonClass;

    public MongoTestClass() {
    }

    public MongoTestClass(MongoTestClass mongoTestClass) {
        this.mongoTestClass = mongoTestClass;
        this.testCommonClass = new TestCommonClass(uuid + "-id");
        uuidStringMap.put(uuid, "id" + uuid);
        uuidListHashMap.put(uuid, new ArrayList<>(List.of("id" + uuid)));
        for (int i = 0; i < 5; i++) {
            list.add(UUID.randomUUID());
            list3.add(new TestCommonClass(uuid.toString()));
            list2.add(i);
        }
    }
}
