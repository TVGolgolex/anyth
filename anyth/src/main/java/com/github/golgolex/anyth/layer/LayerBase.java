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

public interface LayerBase<TObject, TSerialize> {

    /**
     * Interface method to serialize an object of type TObject.
     *
     * @param object The object to be serialized.
     * @return An instance of TSerialize representing the serialized object.
     *
     * This method, when implemented by classes implementing the interface, should define the logic
     * to serialize the provided object of type TObject into a serialized format represented by TSerialize.
     *
     * @throws NullPointerException If the provided object is null.
     */
    TSerialize serialize(TObject object);

    /**
     * Interface method to deserialize a serialized object.
     *
     * @param serialized An instance of TSerialize representing the serialized object.
     * @return An object of type TObject representing the deserialized object.
     *
     * This method, when implemented by classes implementing the interface, should define the logic
     * to deserialize the provided serialized object of type TSerialize into an object of type TObject.
     *
     * @throws NullPointerException If the provided serialized object is null.
     */
    TObject deserialize(Object serialized);

    /**
     * Interface method to check if an object is an instance of the expected class.
     *
     * @param object The object to be checked.
     * @return True if the object is an instance of the expected class, false otherwise.
     *
     * This method, when implemented by classes implementing the interface, should define the logic
     * to check if the provided object is an instance of the expected class.
     *
     */
//    boolean isClass(Object object);

}
