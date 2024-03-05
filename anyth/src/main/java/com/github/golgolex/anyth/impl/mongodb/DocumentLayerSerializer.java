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

import com.github.golgolex.anyth.annotations.ConvertLayer;
import com.github.golgolex.anyth.layer.AbstractLayerSerializer;
import com.github.golgolex.anyth.layer.LayerBase;
import com.github.golgolex.anyth.layer.defaults.CommonBasedLayer;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class DocumentLayerSerializer extends AbstractLayerSerializer<Document, MongoLayerBase<Object>> {

    @Override
    public Document encode(Object object,
                           Collection<MongoLayerBase<Object>> layers,
                           Collection<CommonBasedLayer<?, ?>> commonBasedLayers) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> !(Modifier.isTransient(field.getModifiers())
                        || Modifier.isFinal(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())
                        || field.getName().equals("hash")
                        || field.getName().equals("hashIsZero")))
                .peek(field -> {
                    if (Modifier.isPrivate(field.getModifiers())) {
                        field.setAccessible(true);
                    }
                })
                .collect(
                        Document::new,
                        (encoded, field) -> {
                            try {
                                Object fieldValue = field.get(object);
                                if (fieldValue != null) {
                                    if (fieldValue instanceof Collection<?> collection)
                                    {
                                        List<Object> encodedList = new ArrayList<>();

                                        for (Object item : collection)
                                        {
                                            if (item != null)
                                            {
                                                LayerBase<Object, Document> layer = layers.stream()
                                                        .filter(objectLayerBase -> {
                                                            ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                            return annotation != null && annotation.object().isAssignableFrom(item.getClass());
                                                        })
                                                        .findFirst()
                                                        .orElse(null);

                                                if (layer != null)
                                                {
                                                    encodedList.add(layer.serialize(item));
                                                } else
                                                {
                                                    CommonBasedLayer<Object, Object> commonBasedLayer = (CommonBasedLayer<Object, Object>) commonBasedLayers.stream()
                                                            .filter(objectLayerBase -> {
                                                                ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                                return annotation != null && annotation.object().isAssignableFrom(item.getClass());
                                                            })
                                                            .findFirst()
                                                            .orElse(null);

                                                    if (commonBasedLayer != null)
                                                    {
                                                        encodedList.add(commonBasedLayer.serialize(item));
                                                    } else
                                                    {
                                                        Document itemEncoded = encode(item, layers, commonBasedLayers);
                                                        encodedList.add(itemEncoded);
                                                    }
                                                }
                                            } else
                                            {
                                                encodedList.add(null);
                                            }
                                        }

                                        encoded.put(field.getName(), encodedList);
                                    } else
                                    {
                                        LayerBase<Object, Document> layer = layers.stream()
                                                .filter(objectLayerBase -> {
                                                    ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                    return annotation != null && annotation.object().isAssignableFrom(fieldValue.getClass());
                                                })
                                                .findFirst()
                                                .orElse(null);

                                        if (layer != null)
                                        {
                                            encoded.put(field.getName(), layer.serialize(fieldValue));
                                        } else
                                        {
                                            CommonBasedLayer<Object, Object> commonBasedLayer = (CommonBasedLayer<Object, Object>) commonBasedLayers.stream()
                                                    .filter(objectLayerBase -> {
                                                        ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                        return annotation != null && annotation.object().isAssignableFrom(fieldValue.getClass());
                                                    })
                                                    .findFirst()
                                                    .orElse(null);

                                            if (commonBasedLayer != null)
                                            {
                                                encoded.put(field.getName(), commonBasedLayer.serialize(fieldValue));
                                            } else
                                            {
                                                Document fieldEncoded = encode(fieldValue, layers, commonBasedLayers);
                                                encoded.put(field.getName(), fieldEncoded);
                                            }
                                        }
                                    }
                                } else {
                                    encoded.put(field.getName(), null);
                                }
                            } catch (IllegalAccessException ignored) {
                            }
                        },
                        Document::putAll
                );
    }

/*    @Override
    public Document encode(Object object,
                           Collection<MongoLayerBase<Object>> layers,
                           Collection<CommonBasedLayer<?, ?>> commonBasedLayers) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> !(Modifier.isTransient(field.getModifiers())
                        || Modifier.isFinal(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())
                        || field.getName().equals("hash")
                        || field.getName().equals("hashIsZero")))
                .collect(
                        Document::new,
                        (encoded, field) -> {
                            try {
                                field.setAccessible(true);
                                Object fieldValue = field.get(object);

                                if (fieldValue != null) {
                                    if (fieldValue instanceof Collection<?>) {
                                        List<Object> encodedList = new ArrayList<>();

                                        for (Object item : (Collection<?>) fieldValue) {
                                            if (item != null) {
                                                encodedList.add(encode(item, layers, commonBasedLayers));
                                            } else {
                                                encodedList.add(null);
                                            }
                                        }

                                        encoded.put(field.getName(), encodedList);
                                    } else if (fieldValue instanceof Map<?, ?>) {
                                        Map<?, ?> mapValue = (Map<?, ?>) fieldValue;
                                        Map<Document, Document> encodedMap = new HashMap<>();

                                        for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                                            Object key = entry.getKey();
                                            Object value = entry.getValue();

                                            if (key != null && value != null) {

                                                Document document = new Document();

                                                if (value instanceof Collection<?> collection) {
                                                    List<Document> items  = new ArrayList<>();
                                                    for (Object item : collection)
                                                    {
                                                        items.add(encode(item, layers, commonBasedLayers));
                                                    }
                                                    CommonBasedLayer<Object, Object> commonBasedLayer = (CommonBasedLayer<Object, Object>) commonBasedLayers.stream()
                                                            .filter(objectLayerBase -> {
                                                                ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                                return annotation != null && annotation.object().isAssignableFrom(key.getClass());
                                                            })
                                                            .findFirst()
                                                            .orElse(null);

                                                    if (commonBasedLayer != null) {
                                                        document.append(commonBasedLayer.serializeToString(key), items);
                                                    }

                                                    encodedMap.put(encode(key, layers, commonBasedLayers), document);
                                                } else {

                                                }

                                                // Adjust the encoding based on the types
                                                encodedMap.put(encode(key, layers, commonBasedLayers), document);
                                            }
                                        }

                                        encoded.put(field.getName(), encodedMap);
                                    } else {
                                        encoded.put(field.getName(), null);
                                    }
                                } else {
                                    encoded.put(field.getName(), null);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } finally {
                                field.setAccessible(false);
                            }
                        },
                        Document::putAll
                );
    }*/

    @Override
    public Object decode(Document serialized,
                         Collection<MongoLayerBase<Object>> layers,
                         Class<?> object,
                         Collection<CommonBasedLayer<?, ?>> commonBasedLayers) {
        try {
            Object result = object.getDeclaredConstructor().newInstance();

            Arrays.stream(object.getDeclaredFields())
                    .filter(field -> !(Modifier.isTransient(field.getModifiers())
                            || Modifier.isFinal(field.getModifiers())
                            || Modifier.isStatic(field.getModifiers())
                            || field.getName().equals("hash")
                            || field.getName().equals("hashIsZero")))
                    .peek(field -> {
                        if (Modifier.isPrivate(field.getModifiers())) {
                            field.setAccessible(true);
                        }
                    })
                    .forEach(field -> {
                        try {
                            Object declaredField = field.get(result);
                            Object fieldValue = serialized.get(field.getName());

                            if (fieldValue != null) {
                                if (Collection.class.isAssignableFrom(field.getType())) {
                                    field.set(result, decodeCollection((List<Object>) fieldValue, layers, field, commonBasedLayers));
                                } else if (fieldValue instanceof Map<?, ?> map) {
                                    Map<Object, Object> decodedMap = new HashMap<>();

                                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                                        Object key = entry.getKey();
                                        Object value = entry.getValue();

                                        if (key != null && value != null) {
                                            Object decodedKey = decode((Document) key, layers, key.getClass(), commonBasedLayers);
                                            Object decodedValue = decode((Document) value, layers, value.getClass(), commonBasedLayers);
                                            decodedMap.put(decodedKey, decodedValue);
                                        }
                                    }

                                    field.set(result, decodedMap);
                                } else {
                                    LayerBase<Object, Document> layer = layers.stream()
                                            .filter(objectLayerBase -> {
                                                ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                return annotation != null && Objects.requireNonNullElse(declaredField, fieldValue).getClass() == annotation.object();
                                            })
                                            .findFirst()
                                            .orElse(null);

                                    if (layer != null) {
                                        field.set(result, layer.deserialize(fieldValue));
                                    } else {
                                        CommonBasedLayer<Object, Object> commonBasedLayer = (CommonBasedLayer<Object, Object>) commonBasedLayers.stream()
                                                .filter(objectLayerBase -> {
                                                    ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                                    return annotation != null && Objects.requireNonNullElse(declaredField, fieldValue).getClass() == annotation.object();
                                                })
                                                .findFirst()
                                                .orElse(null);

                                        if (commonBasedLayer != null) {
                                            field.set(result, commonBasedLayer.deserialize(fieldValue));
                                        } else {
                                            Object decodedField = decode((Document) fieldValue, layers, field.getType(), commonBasedLayers);
                                            field.set(result, decodedField);
                                        }
                                    }
                                }
                            } else {
                                field.set(result, null);
                            }
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace(); // Handle the exception according to your needs
                        }
                    });

            return result;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return null;
        }
    }

    private Collection<Object> decodeCollection(List<Object> collectionList,
                                                Collection<MongoLayerBase<Object>> layers,
                                                Field field,
                                                Collection<CommonBasedLayer<?, ?>> commonBasedLayers) throws ReflectiveOperationException {
        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArguments = fieldType.getActualTypeArguments();

        if (actualTypeArguments.length > 0) {
            Class<?> elementType = (Class<?>) actualTypeArguments[0];

            List<Object> decodedList = new ArrayList<>();

            for (Object item : collectionList) {
                if (item != null) {
                    if (elementType.equals(Document.class)) {
                        decodedList.add(decode((Document) item, layers, elementType, commonBasedLayers));
                    } else {
                        MongoLayerBase<Object> elementLayer = layers.stream()
                                .filter(objectLayerBase -> {
                                    ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                    return annotation != null && annotation.object().equals(elementType);
                                })
                                .findFirst()
                                .orElse(null);

                        if (elementLayer != null) {
                            decodedList.add(elementLayer.deserialize(item));
                        } else {
                            CommonBasedLayer<Object, Object> elementCommonBasedLayer = (CommonBasedLayer<Object, Object>) commonBasedLayers.stream()
                                    .filter(objectLayerBase -> {
                                        ConvertLayer annotation = objectLayerBase.getClass().getAnnotation(ConvertLayer.class);
                                        return annotation != null && annotation.object().equals(elementType);
                                    })
                                    .findFirst()
                                    .orElse(null);

                            if (elementCommonBasedLayer != null) {
                                decodedList.add(elementCommonBasedLayer.deserialize(item));
                            } else {
                                decodedList.add(item);
                            }
                        }
                    }
                } else {
                    decodedList.add(null);
                }
            }

            return new ArrayList<>(decodedList);
        }

        return null;
    }


}
