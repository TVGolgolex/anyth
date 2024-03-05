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

@ConvertLayer(object = Character.class)
public class CharLayer implements CommonBasedLayer<Character, Character> {
    @Override
    public Character serialize(Character object) {
        return object;
    }

    @Override
    public Character deserialize(Object serialized) {
        if (!(serialized instanceof Character character)) {
            return null;
        }
        return character;
    }

    @Override
    public String serializeToString(Character serialized)
    {
        return String.valueOf(serialized);
    }

    @Override
    public Character deserializeFromString(String string) {
        if (string != null && !string.isEmpty()) {
            return string.charAt(0);
        }
        return null;
    }
}
