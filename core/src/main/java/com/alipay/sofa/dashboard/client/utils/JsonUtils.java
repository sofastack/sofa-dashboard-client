/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.dashboard.client.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 执行JSON序列化相关功能
 */
public final class JsonUtils {

    private static final Logger       LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 隐藏工具类构造器
     */
    private JsonUtils() {
    }

    /**
     * 将一个对象转化为JSON字符串
     *
     * @param obj 待序列化对象
     * @return JSON 字符串
     * @throws JsonSerializeError 序列化异常
     */
    public static String toJsonString(@Nullable Object obj) throws JsonSerializeError {
        if (obj == null) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonSerializeError(e);
        }
    }

    /**
     * 将一个对象转化为JSON字符串
     *
     * @param obj 待序列化对象
     * @return JSON 字符串
     * @throws JsonSerializeError 序列化异常
     */
    public static byte[] toJsonBytes(@Nullable Object obj) throws JsonSerializeError {
        if (obj == null) {
            return null;
        }

        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new JsonSerializeError(e);
        }
    }

    /**
     * 将一个JSON字符串转化为对象
     *
     * @param data          JSON字符串
     * @param serializeType 序列化类型
     * @param <T>           序列化泛型
     * @return 反序列化对象
     * @throws JsonSerializeError 反序列化异常
     */
    public static <T> T parseObject(@Nullable byte[] data, Class<T> serializeType)
                                                                                  throws JsonSerializeError {
        if (data != null) {
            try {
                return MAPPER.readValue(data, serializeType);
            } catch (IOException e) {
                throw new JsonSerializeError(e);
            }
        }
        return null;
    }

    /**
     * 将一个JSON字符串转化为对象
     *
     * @param data          JSON字符串
     * @param serializeType 序列化类型
     * @param <T>           序列化泛型
     * @return 反序列化对象
     * @throws JsonSerializeError 反序列化异常
     */
    public static <T> T parseObject(@Nullable String data, Class<T> serializeType)
                                                                                  throws JsonSerializeError {
        if (data == null) {
            return null;
        }

        try {
            return MAPPER.readValue(data, serializeType);
        } catch (IOException e) {
            throw new JsonSerializeError(e);
        }
    }

    /**
     * 将一个JSON字符串转化为列表对象
     *
     * @param data     JSON字符串
     * @param itemType 序列化元素类型
     * @param <T>      序列化泛型
     * @return 反序列化对象
     * @throws JsonSerializeError 反序列化异常
     */
    public static <T> List<T> parseList(
        @Nullable String data,
        Class<T> itemType
    ) throws JsonSerializeError {
        if (data == null) {
            return null;
        }
        try {
            //noinspection unchecked
            return ((List<Map<String, Object>>) MAPPER.readValue(data, List.class))
                .stream()
                .map(JsonUtils::toJsonString)
                .map(it -> JsonUtils.parseObject(it, itemType))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new JsonSerializeError(e);
        }
    }

    /**
     * 序列化/反序列化异常
     */
    public static final class JsonSerializeError extends RuntimeException {

        private JsonSerializeError(Exception err) {
            super(err);
        }
    }

    public static <T> T convertFromBytes(byte[] bytes, Class<T> valueType) {
        try {
            if (bytes == null) {
                return null;
            }

            return JSON.parseObject(bytes, valueType);
        } catch (Exception e) {
            LOGGER.error("Error to convert object from data bytes.", e);
        }
        return null;
    }

    public static byte[] convertFromObject(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            String jsonObj = JSON.toJSONString(obj);
            return jsonObj.getBytes();
        } catch (Exception e) {
            LOGGER.error("Error to convert bytes from data object.", e);
        }
        return null;

    }
}
