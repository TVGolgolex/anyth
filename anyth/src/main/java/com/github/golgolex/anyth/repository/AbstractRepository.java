package com.github.golgolex.anyth.repository;

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
import com.github.golgolex.anyth.exception.LayerAlreadyExistException;
import com.github.golgolex.anyth.exception.LayerApplyException;
import com.github.golgolex.anyth.exception.LayerNotExistException;
import com.github.golgolex.anyth.layer.AbstractLayerSerializer;
import com.github.golgolex.anyth.layer.LayerBase;
import com.github.golgolex.anyth.layer.defaults.*;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractRepository<TLayer extends LayerBase<?, ?>,
        TFilters,
        TSerializerObject,
        TLayerSerializer extends AbstractLayerSerializer<TSerializerObject, ?>> {

    private final String repositoryName;

    private final Map<Class<?>, TLayer> layers = new ConcurrentHashMap<>(0);

    private final Map<Class<?>, CommonBasedLayer<?, ?>> commonLayer = new ConcurrentHashMap<>(0);

    private final TLayerSerializer layerSerializer;

    public AbstractRepository(String repositoryName,
                              TLayerSerializer layerSerializer) {
        this.repositoryName = repositoryName;
        this.layerSerializer = layerSerializer;

        this.applyCommonLayer(BooleanLayer.class);
        this.applyCommonLayer(CharLayer.class);
        this.applyCommonLayer(IntegerLayer.class);
        this.applyCommonLayer(StringLayer.class);
        this.applyCommonLayer(LongLayer.class);
        this.applyCommonLayer(UUIDLayer.class);
    }

    /**
     * Applies a layer for the specified class.
     *
     * @param layer The class for which the layer is applied.
     * @param <L>   The generic type of the layer.
     *              <p>
     *              This method checks if the provided class is annotated with @ConvertLayer.
     *              If so, it creates an instance of the specified layer class using reflection,
     *              ensuring accessibility to the constructor. It then checks if a layer for the
     *              corresponding object class already exists in the layers map. If it does,
     *              a LayerAlreadyExistException is thrown. Otherwise, the newly created layer
     *              is added to the layers map with the object class as the key.
     * @throws LayerAlreadyExistException If a layer for the specified object class already exists.
     * @throws LayerApplyException        If there is an issue with applying the layer, such as
     *                                    constructor invocation or instantiation.
     */
    public <L> void applyLayer(@NonNull Class<L> layer) {
        // Check if the provided class is annotated with @ConvertLayer
        if (layer.isAnnotationPresent(ConvertLayer.class)) {
            ConvertLayer convertLayerAnnotation = layer.getAnnotation(ConvertLayer.class);
            // Retrieve the object class specified in the annotation
            Class<?> objectClass = convertLayerAnnotation.object();

            try {
                // Create an instance of the layer using reflection
                Constructor<?> constructor = layer.getDeclaredConstructor();
                constructor.setAccessible(true);
                TLayer constructedLayer = (TLayer) constructor.newInstance();

                // Check if a layer for the specified object class already exists
                if (layers.containsKey(objectClass)) {
                    throw new LayerAlreadyExistException("A layer serializer for class [" + objectClass.getSimpleName() + "] already exists.");
                }

                // Add the newly created layer to the layers map
                this.layers.put(objectClass, constructedLayer);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException exception) {
                // Throw an exception if there is an issue with applying the layer
                throw new LayerApplyException(exception.getMessage());
            }
        }
    }

    /**
     * Applies a FromToLayer for the specified class.
     *
     * @param layer The class for which the FromToLayer is applied.
     * @param <L>   The generic type of the FromToLayer.
     *              <p>
     *              This method checks if the provided class is annotated with @ConvertLayer.
     *              If so, it creates an instance of the specified layer class using reflection,
     *              ensuring accessibility to the constructor. It then checks if a FromToLayer for
     *              the corresponding object class already exists in the fromToLayers map. If it does,
     *              a LayerAlreadyExistException is thrown. Otherwise, the newly created FromToLayer
     *              is added to the fromToLayers map with the object class as the key.
     * @throws LayerAlreadyExistException If a FromToLayer for the specified object class already exists.
     * @throws LayerApplyException        If there is an issue with applying the FromToLayer, such as
     *                                    constructor invocation or instantiation.
     */
    public <L> void applyCommonLayer(@NonNull Class<L> layer) {
        // Check if the provided class is annotated with @ConvertLayer
        if (layer.isAnnotationPresent(ConvertLayer.class)) {
            ConvertLayer convertLayerAnnotation = layer.getAnnotation(ConvertLayer.class);
            // Retrieve the object class specified in the annotation
            Class<?> objectClass = convertLayerAnnotation.object();

            try {
                // Create an instance of the FromToLayer using reflection
                Constructor<?> constructor = layer.getDeclaredConstructor();
                constructor.setAccessible(true);
                CommonBasedLayer<?, ?> constructedLayer = (CommonBasedLayer<?, ?>) constructor.newInstance();

                // Check if a FromToLayer for the specified object class already exists
                if (commonLayer.containsKey(objectClass)) {
                    throw new LayerAlreadyExistException("A layer serializer for class [" + objectClass.getSimpleName() + "] already exists.");
                }

                // Add the newly created FromToLayer to the fromToLayers map
                this.commonLayer.put(objectClass, constructedLayer);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException exception) {
                // Throw an exception if there is an issue with applying the FromToLayer
                throw new LayerApplyException(exception.getMessage());
            }
        }
    }

    /**
     * Removes a layer and its corresponding FromToLayer for the specified class.
     *
     * @param objectClass The class for which the layer and FromToLayer are removed.
     *
     * This method checks if a layer or FromToLayer exists for the specified object class.
     * If neither exists, a LayerNotExistException is thrown. Otherwise, the layer and
     * FromToLayer are removed from their respective maps.
     *
     * @throws LayerNotExistException If a layer or FromToLayer for the specified object class couldn't be found.
     */
    public void removeLayer(@NonNull Class<?> objectClass) {
        // Check if a layer or FromToLayer exists for the specified object class
        if (!layers.containsKey(objectClass) && !commonLayer.containsKey(objectClass)) {
            // Throw an exception if a layer or FromToLayer couldn't be found
            throw new LayerNotExistException("A layer serializer for class [" + objectClass.getSimpleName() + "] couldn't be found");
        }

        // Remove the layer and FromToLayer from their respective maps
        layers.remove(objectClass);
        commonLayer.remove(objectClass);
    }

    /**
     * Abstract method to convert an object to its serialized form.
     *
     * @param object The object to be serialized.
     * @return The serialized representation of the object.
     *
     * This method, when implemented by subclasses, should define the logic
     * to convert the provided object into a serialized format, represented
     * by the generic type TSerializerObject.
     *
     * @throws NullPointerException If the provided object is null.
     */
    public abstract TSerializerObject serializerObject(@NonNull Object object);

    /**
     * Abstract method to deserialize a serialized object back to its original form.
     *
     * @param serialized The serialized representation of the object.
     * @param clazz      The class type to which the object should be deserialized.
     * @param <T>        The generic type of the deserialized object.
     * @return The deserialized object of type T.
     *
     * This method, when implemented by subclasses, should define the logic
     * to convert the provided serialized object (of type TSerializerObject)
     * back to its original form, represented by the specified class type.
     *
     * @throws NullPointerException If the provided serialized object or class type is null.
     * @throws IllegalArgumentException If the deserialization process encounters an error.
     */
    public abstract <T> T deserializeObject(@NonNull TSerializerObject serialized, @NonNull Class<T> clazz);

    /**
     * Abstract method to query and retrieve an object based on specified filters.
     *
     * @param filters The filters used for querying.
     * @param clazz   The class type of the object to be queried.
     * @param <T>     The generic type of the queried object.
     * @return The queried object of type T.
     *
     * This method, when implemented by subclasses, should define the logic
     * to query and retrieve an object based on the provided filters, and return
     * an object of the specified class type.
     *
     * @throws NullPointerException      If the provided filters or class type is null.
     * @throws IllegalArgumentException  If the query process encounters an error.
     */
    public abstract <T> T query(@NonNull TFilters filters, @NonNull Class<T> clazz);

   /**
     * Queries and retrieves a single TSerializerObject instance based on the specified filters.
     *
     * @param filters The filters to apply to the query. Must not be null.
     * @return A TSerializerObject instance that matches the specified filters.
     */
    public abstract TSerializerObject query(@NonNull TFilters filters);

    /**
     * Abstract method to asynchronously query and retrieve an object based on specified filters.
     *
     * @param filters The filters used for asynchronous querying.
     * @param clazz   The class type of the object to be asynchronously queried.
     * @param <T>     The generic type of the asynchronously queried object.
     * @return The asynchronously queried object of type T.
     *
     * This method, when implemented by subclasses, should define the asynchronous logic
     * to query and retrieve an object based on the provided filters, and return
     * an object of the specified class type.
     *
     * @throws NullPointerException      If the provided filters or class type is null.
     * @throws IllegalArgumentException  If the asynchronous query process encounters an error.
     */
    public abstract <T> T asyncQuery(@NonNull TFilters filters, @NonNull Class<T> clazz);

    /**
     * Replaces an object in the data source based on the specified filters synchronously.
     *
     * @param filters The filters to determine the object to be replaced.
     * @param object  The object to replace the existing one.
     * @param <T>     The generic type of the object.
     * @return        The replaced object.
     */
    public abstract <T> boolean replace(@NonNull TFilters filters, @NonNull T object);

    /**
     * Replaces an object in the data source based on the specified filters asynchronously.
     *
     * @param filters The filters to determine the object to be replaced.
     * @param object  The object to replace the existing one asynchronously.
     * @param <T>     The generic type of the object.
     * @return        The replaced object.
     */
    public abstract <T> boolean asyncReplace(@NonNull TFilters filters, @NonNull T object);

    /**
     * Replaces a serialized object in the data source based on the specified filters.
     *
     * @param filters          The filters to determine the location of the serialized object to be replaced.
     * @param serializerObject The new serialized object to replace the existing one.
     */
    public abstract boolean replaceSerialized(@NonNull TFilters filters, @NonNull TSerializerObject serializerObject);

    /**
     * Replaces a serialized object in the data source based on the specified filters asynchronously.
     *
     * @param filters          The filters to determine the location of the serialized object to be replaced.
     * @param serializerObject The new serialized object to replace the existing one asynchronously.
     */
    public abstract boolean asyncReplaceSerialized(@NonNull TFilters filters, @NonNull TSerializerObject serializerObject);

    /**
     * Queries and retrieves a collection of TSerializerObject instances based on the specified filters.
     *
     * @param filters The filters to apply to the query.
     * @return A collection of TSerializerObject instances that match the specified filters.
     */
    public abstract Collection<TSerializerObject> queryAll(TFilters filters);

    /**
     * Queries and retrieves all instances of a given class from the data source.
     *
     * @param clazz The class type of the objects to be queried.
     * @param <T>   The generic type of the objects to be queried.
     * @return      A collection containing all instances of the specified class.
     */
    public abstract <T> Collection<T> queryAll(Class<T> clazz);

    /**
     * Queries and retrieves all serialized objects from the data source.
     *
     * @return A collection containing all serialized objects.
     */
    public abstract Collection<TSerializerObject> queryAll();

    /**
     * Abstract method to insert an object into the repository based on specified filters.
     *
     * @param filters The filters used for insertion.
     * @param object  The object to be inserted.
     * @param <T>     The generic type of the inserted object.
     * @return True if the insertion is successful, false otherwise.
     *
     * This method, when implemented by subclasses, should define the logic
     * to insert the provided object into the repository based on the specified filters.
     *
     * @throws NullPointerException      If the provided filters or object is null.
     * @throws IllegalArgumentException  If the insertion process encounters an error.
     */
    public abstract <T> boolean insert(TFilters filters, @NonNull T object);

    /**
     * Abstract method to asynchronously insert an object into the repository based on specified filters.
     *
     * @param filters The filters used for asynchronous insertion.
     * @param object  The object to be asynchronously inserted.
     * @param <T>     The generic type of the asynchronously inserted object.
     * @return True if the asynchronous insertion is successful, false otherwise.
     *
     * This method, when implemented by subclasses, should define the asynchronous logic
     * to insert the provided object into the repository based on the specified filters.
     *
     * @throws NullPointerException      If the provided filters or object is null.
     * @throws IllegalArgumentException  If the asynchronous insertion process encounters an error.
     */
    public abstract <T> boolean asyncInsert(TFilters filters, @NonNull T object);

    /**
     * Inserts a serialized object into the data source based on the specified filters.
     *
     * @param filters          The filters to determine the insertion location.
     * @param serializerObject The serialized object to be inserted.
     * @return                 true if the insertion was successful, false otherwise.
     */
    public abstract boolean insertSerialized(TFilters filters, @NonNull TSerializerObject serializerObject);

    /**
     * Inserts a serialized object into the data source based on the specified filters asynchronously.
     *
     * @param filters          The filters to determine the insertion location.
     * @param serializerObject The serialized object to be inserted.
     * @return                 true if the asynchronous insertion was successful, false otherwise.
     */
    public abstract boolean asyncInsertSerialized(TFilters filters, @NonNull TSerializerObject serializerObject);

    /**
     * Abstract method to delete objects from the repository based on specified filters.
     *
     * @param filters The filters used for deletion.
     * @return True if the deletion is successful, false otherwise.
     *
     * This method, when implemented by subclasses, should define the logic
     * to delete objects from the repository based on the specified filters.
     *
     * @throws NullPointerException      If the provided filters is null.
     * @throws IllegalArgumentException  If the deletion process encounters an error.
     */
    public abstract boolean delete(@NonNull TFilters filters);

    /**
     * Abstract method to asynchronously delete objects from the repository based on specified filters.
     *
     * @param filters The filters used for asynchronous deletion.
     * @return True if the asynchronous deletion is successful, false otherwise.
     *
     * This method, when implemented by subclasses, should define the asynchronous logic
     * to delete objects from the repository based on the specified filters.
     *
     * @throws NullPointerException      If the provided filters is null.
     * @throws IllegalArgumentException  If the asynchronous deletion process encounters an error.
     */
    public abstract boolean asyncDelete(@NonNull TFilters filters);

    /**
     * Applies changes to the specified object based on the provided filters synchronously.
     *
     * @param filters The filters to determine the changes to be applied.
     * @param object  The object to which changes should be applied.
     * @param <T>     The generic type of the object.
     * @return        true if changes were successfully applied, false otherwise.
     */
    public abstract <T> boolean applyChanges(@NonNull TFilters filters, @NonNull T object);

    /**
     * Applies changes to a serialized object in the data source based on the specified filters.
     *
     * @param filters          The filters to determine the location of the serialized object.
     * @param serializerObject The serialized object to which changes should be applied.
     * @return                 true if the changes were successfully applied, false otherwise.
     */
    public abstract boolean applyChangesSerialized(@NonNull TFilters filters, @NonNull TSerializerObject serializerObject);

    /**
     * Applies changes to a serialized object in the data source based on the specified filters asynchronously.
     *
     * @param filters          The filters to determine the location of the serialized object.
     * @param serializerObject The serialized object to which changes should be applied asynchronously.
     * @return                 true if the asynchronous changes were successfully applied, false otherwise.
     */
    public abstract boolean applyAsyncChangesSerialized(@NonNull TFilters filters, @NonNull TSerializerObject serializerObject);

    /**
     * Applies changes to the specified object based on the provided filters asynchronously.
     *
     * @param filters The filters to determine the changes to be applied.
     * @param object  The object to which changes should be applied asynchronously.
     * @param <T>     The generic type of the object.
     * @return        true if changes were successfully applied, false otherwise.
     */
    public abstract <T> boolean applyAsyncChanges(@NonNull TFilters filters, @NonNull T object);

    /**
     * Checks if an object exists in the data source based on the specified filters synchronously.
     *
     * @param filters The filters to determine the existence of an object.
     * @return        true if an object exists, false otherwise.
     */
    public abstract boolean exist(@NonNull TFilters filters);

    /**
     * Checks if an object exists in the data source based on the specified filters asynchronously.
     *
     * @param filters The filters to determine the existence of an object.
     * @return        true if an object exists, false otherwise.
     */
    public abstract boolean asyncExist(@NonNull TFilters filters);

    /**
     * Clears data from the data source based on the specified filters synchronously.
     *
     * @param filters The filters to determine which data to clear.
     * @return        true if the clear operation was successful, false otherwise.
     */
    public abstract boolean clear(@NonNull TFilters filters);

    /**
     * Clears data from the data source based on the specified filters asynchronously.
     *
     * @param filters The filters to determine which data to clear.
     * @return        true if the clear operation was successful, false otherwise.
     */
    public abstract boolean asyncClear(@NonNull TFilters filters);

    /**
     * Clears all data from the data source synchronously.
     *
     * @return true if the clear operation was successful, false otherwise.
     */
    public abstract boolean clear();

    /**
     * Clears all data from the data source asynchronously.
     *
     * @return true if the clear operation was successful, false otherwise.
     */
    public abstract boolean asyncClear();

    /**
     * Abstract method to check if the repository is empty.
     *
     * @return True if the repository is empty, false otherwise.
     *
     * This method, when implemented by subclasses, should define the logic
     * to check whether the repository is empty or not.
     *
     * @implNote The specific criteria for determining emptiness may vary based on the
     *           repository implementation, and it should be clearly documented by subclasses.
     */
    public abstract boolean isEmpty();

}
