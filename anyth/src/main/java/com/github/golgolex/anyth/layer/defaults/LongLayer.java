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

@ConvertLayer(object = Long.class)
public class LongLayer implements CommonBasedLayer<Long, Long> {

    @Override
    public Long serialize(Long object) {
        return object;
    }

    @Override
    public Long deserialize(Object serialized) {
        if (!(serialized instanceof Long l)) {
            return null;
        }
        return l;
    }

    @Override
    public String serializeToString(Long serialized)
    {
        return String.valueOf(serialized);
    }

    @Override
    public Long deserializeFromString(String string)
    {
        return Long.parseLong(string);
    }
}
