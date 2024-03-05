package demo.mongo;

/*
 * Copyright 2023-2024 anyth contributors
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

import com.github.golgolex.anyth.annotations.ConvertLayer;
import com.github.golgolex.anyth.impl.mongodb.MongoLayerBase;
import demo.TestCommonClass;
import org.bson.Document;

@ConvertLayer(object = TestCommonClass.class)
public class TestCommonMongoLayer extends MongoLayerBase<TestCommonClass> {
    @Override
    public Document serialize(TestCommonClass object)
    {
        return new Document().append("id", object.getId());
    }

    @Override
    public TestCommonClass deserialize(Document document)
    {
        return new TestCommonClass(document.getString("id"));
    }
}
