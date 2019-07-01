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
package com.alipay.sofa.dashboard.client.dimension.actuator;

import com.alipay.sofa.dashboard.client.dimension.ApplicationDimension;
import com.alipay.sofa.dashboard.client.model.io.RecordName;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint.ApplicationMappings;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ActuatorMappingsDimension implements ApplicationDimension<MappingsDescriptor> {

    private static final String    SPIT = ", ";

    private final MappingsEndpoint endpoint;

    public ActuatorMappingsDimension(MappingsEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getName() {
        return RecordName.MAPPINGS;
    }

    @Override
    public Class<MappingsDescriptor> getType() {
        return MappingsDescriptor.class;
    }

    @Override
    public MappingsDescriptor currentValue() {
        ApplicationMappings mappingsInfo = endpoint.mappings();

        final Map<String, MappingsDescriptor.MappingEntity> result = new HashMap<>();
        mappingsInfo.getContexts().forEach((key, value) -> {
            // It is easier to parse a complex map instead, while some inner objects can be null
            //noinspection unchecked
            Map<String, Object> valueMap = JsonUtils.parseObject(
                JsonUtils.toJsonString(value), Map.class);

            MappingsDescriptor.MappingEntity info = new MappingsDescriptor.MappingEntity();

            List<Map<String, Object>> dispatchServlets = readDict(valueMap,
                "mappings", "dispatcherServlets", "dispatcherServlet");
            info.getDispatcherServlet().addAll(parseDispatchServlet(
                Optional.ofNullable(dispatchServlets).orElse(new ArrayList<>())));

            List<Map<String, Object>> servletFilters = readDict(valueMap,
                "mappings", "servletFilters");
            info.getServletFilters().addAll(parseServletFilter(
                Optional.ofNullable(servletFilters).orElse(new ArrayList<>())));

            List<Map<String, Object>> servlets = readDict(valueMap,
                "mappings", "servlets");
            info.getServlets().addAll(parseServletInfo(
                Optional.ofNullable(servlets).orElse(new ArrayList<>())));

            result.put(key, info);
        });

        MappingsDescriptor descriptor = new MappingsDescriptor();
        descriptor.getMappings().putAll(result);
        return descriptor;
    }

    /**
     * Parse dispatch servlet from data list
     *
     * @param data data list
     * @return Handler Mapping Info list
     */
    private List<MappingsDescriptor.HandlerMappingInfo> parseDispatchServlet(
        @NonNull List<Map<String, Object>> data
    ) {
        return data.stream().map(map -> {
            // Read meta info from map
            String handler = readDict(map, "handler");
            String predicate = readDict(map, "predicate");
            List<String> methods = readDict(map,
                "details", "requestMappingConditions", "methods");
            List<Map<String, Object>> paramsType = readDict(map,
                "details", "requestMappingConditions", "consumes");
            List<Map<String, Object>> returnType = readDict(map,
                "details", "requestMappingConditions", "produces");

            // Map to description text
            String methodsDesc = mapToDesc(methods);
            String paramTypeDesc = Optional.ofNullable(paramsType).orElse(new ArrayList<>())
                .stream()
                .map(it -> (String) it.getOrDefault("mediaType", ""))
                .reduce((a, b) -> a + SPIT + b)
                .orElse("");
            String returnTypeDesc = Optional.ofNullable(returnType).orElse(new ArrayList<>())
                .stream()
                .map(it -> (String) it.getOrDefault("mediaType", ""))
                .reduce((a, b) -> a + SPIT + b)
                .orElse("");

            // Generate mapping info
            MappingsDescriptor.HandlerMappingInfo info = new MappingsDescriptor.HandlerMappingInfo();
            info.setHandler(Optional.ofNullable(handler).orElse(""));
            info.setPredicate(Optional.ofNullable(predicate).orElse(""));
            info.setMethods(methodsDesc);
            info.setParamsType(paramTypeDesc);
            info.setResponseType(returnTypeDesc);
            return info;

        }).collect(Collectors.toList());
    }

    /**
     * Parse servlet filter from data list
     *
     * @param data data list
     * @return Handler Filter info list
     */
    private List<MappingsDescriptor.HandlerFilterInfo> parseServletFilter(
        @NonNull List<Map<String, Object>> data
    ) {
        return data.stream().map(map -> {
            // Read meta info from map
            String name = readDict(map, "name");
            String className = readDict(map, "className");
            List<String> servletNameMappings = readDict(map, "servletNameMappings");
            List<String> urlPatternMappings = readDict(map, "urlPatternMappings");

            // Generate mapping info
            MappingsDescriptor.HandlerFilterInfo info = new MappingsDescriptor.HandlerFilterInfo();
            info.setName(Optional.ofNullable(name).orElse(""));
            info.setClassName(Optional.ofNullable(className).orElse(""));
            info.setServletNameMappings(mapToDesc(servletNameMappings));
            info.setUrlPatternMappings(mapToDesc(urlPatternMappings));
            return info;

        }).collect(Collectors.toList());
    }

    /**
     * Parse servlet from data list
     *
     * @param data data list
     * @return Servlet info list
     */
    private List<MappingsDescriptor.ServletInfo> parseServletInfo(
        @NonNull List<Map<String, Object>> data
    ) {
        return data.stream().map(map -> {
            // Read meta info from map
            List<String> mappings = readDict(map, "mappings");
            String name = readDict(map, "name");
            String className = readDict(map, "className");

            // Generate mapping info
            MappingsDescriptor.ServletInfo info = new MappingsDescriptor.ServletInfo();
            info.setMappings(mapToDesc(mappings));
            info.setName(Optional.ofNullable(name).orElse(""));
            info.setClassName(Optional.ofNullable(className).orElse(""));
            return info;

        }).collect(Collectors.toList());
    }

    /**
     * A util function to read dictionary type
     *
     * @param instance map instance
     * @param path     dict path
     * @return path value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private <T> T readDict(@Nullable Map<String, Object> instance, String... path) {
        if (instance == null) {
            return null;
        } else if (path.length == 1) {
            return (T) instance.get(path[0]);
        }

        Object nextInstance = instance.get(path[0]);
        String[] nextPath = Arrays.copyOfRange(path, 1, path.length);
        return nextInstance instanceof Map ? readDict((Map<String, Object>) nextInstance, nextPath)
            : null;
    }

    /**
     * A util function to map list into text
     *
     * @param listStr text list
     * @return mapped text
     */
    private String mapToDesc(@Nullable Collection<?> listStr) {
        return Optional.ofNullable(listStr).orElse(new ArrayList<>())
            .stream()
            .map(String::valueOf)
            .reduce((a, b) -> a + SPIT + b)
            .orElse("");
    }
}
