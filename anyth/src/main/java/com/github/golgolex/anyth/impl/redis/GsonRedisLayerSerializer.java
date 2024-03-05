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

import com.google.gson.Gson;
import com.github.golgolex.anyth.layer.AbstractLayerSerializer;
import com.github.golgolex.anyth.layer.defaults.CommonBasedLayer;

import java.util.Collection;

public class GsonRedisLayerSerializer extends AbstractLayerSerializer<String, RedisLayerBase<Object>> {

    private final Gson gson = new Gson();

    @Override
    public String encode(Object tClass, Collection<RedisLayerBase<Object>> layers, Collection<CommonBasedLayer<?, ?>> commonBasedLayers) {
        return gson.toJson(tClass);
    }

    @Override
    public Object decode(String serialized, Collection<RedisLayerBase<Object>> layers, Class<?> clazz, Collection<CommonBasedLayer<?, ?>> commonBasedLayers) {
        return gson.fromJson(serialized, clazz);
    }

}
