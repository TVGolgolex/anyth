package com.github.golgolex.anyth.layer.defaults;

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

import com.github.golgolex.anyth.annotations.ConvertLayer;

import java.util.UUID;

@ConvertLayer(object = UUID.class)
public class UUIDLayer implements CommonBasedLayer<UUID, String> {
    @Override
    public String serialize(UUID object) {
        return object.toString();
    }

    @Override
    public UUID deserialize(Object serialized) {
        if (!(serialized instanceof String s)) {
            return null;
        }
        return UUID.fromString(s);
    }

    @Override
    public String serializeToString(UUID serialized)
    {
        return serialized.toString();
    }

    @Override
    public UUID deserializeFromString(String string)
    {
        return UUID.fromString(string);
    }
}
