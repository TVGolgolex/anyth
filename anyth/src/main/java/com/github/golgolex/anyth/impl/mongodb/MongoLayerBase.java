package com.github.golgolex.anyth.impl.mongodb;

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

import com.github.golgolex.anyth.layer.LayerBase;
import org.bson.Document;

public abstract class MongoLayerBase<TObject>
        implements LayerBase<TObject, Document> {

    @Override
    public TObject deserialize(Object serialized)
    {
        if (!(serialized instanceof Document document)) {
            return null;
        }
        return deserialize(document);
    }

    public abstract TObject deserialize(Document document);
}
