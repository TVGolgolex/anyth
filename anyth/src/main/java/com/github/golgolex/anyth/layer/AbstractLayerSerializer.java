package com.github.golgolex.anyth.layer;

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

import com.github.golgolex.anyth.layer.defaults.CommonBasedLayer;

import java.util.Collection;

public abstract class AbstractLayerSerializer<TSerializer, TLayerBase extends LayerBase<Object, ?>> {

    /**
     * Abstract method to encode an object along with associated layers and FromToLayers.
     *
     * @param tClass        The object to be encoded.
     * @param layers        A collection of layers associated with the object.
     * @param commonBasedLayers  A collection of FromToLayers associated with the object.
     * @return An instance of TSerializer representing the encoded object.
     *
     * This method, when implemented by subclasses, should define the logic
     * to encode the provided object, along with its associated layers and FromToLayers,
     * into a serialized format represented by an instance of TSerializer.
     *
     * @throws NullPointerException If the provided object, layers, or FromToLayers is null.
     * @throws IllegalArgumentException If there is an issue with the encoding process.
     */
    public abstract TSerializer encode(Object tClass,
                                       Collection<TLayerBase> layers,
                                       Collection<CommonBasedLayer<?, ?>> commonBasedLayers);


    /**
     * Abstract method to decode a serialized object along with associated layers and FromToLayers.
     *
     * @param serialized    An instance of TSerializer representing the serialized object.
     * @param layers        A collection of layers associated with the object.
     * @param clazz         The class type to which the object should be decoded.
     * @param commonBasedLayers  A collection of FromToLayers associated with the object.
     * @return The decoded object of the specified class type.
     *
     * This method, when implemented by subclasses, should define the logic
     * to decode the provided serialized object, along with its associated layers
     * and FromToLayers, into an object of the specified class type.
     *
     * @throws NullPointerException If the provided serialized object, layers, class type, or FromToLayers is null.
     * @throws IllegalArgumentException If there is an issue with the decoding process.
     */
    public abstract Object decode(TSerializer serialized,
                                  Collection<TLayerBase> layers,
                                  Class<?> clazz,
                                  Collection<CommonBasedLayer<?, ?>> commonBasedLayers);
}
