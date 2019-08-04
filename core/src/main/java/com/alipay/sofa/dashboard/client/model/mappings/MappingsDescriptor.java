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
package com.alipay.sofa.dashboard.client.model.mappings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/5/10 3:31 PM
 **/
public class MappingsDescriptor implements Serializable {

    private static final int                 serialVersionUID = 0x11;

    private final Map<String, MappingEntity> mappings         = new HashMap<>();

    public Map<String, MappingEntity> getMappings() {
        return mappings;
    }

    public static class MappingEntity implements Serializable {

        private static final int               serialVersionUID  = 0x11;

        private final List<ServletInfo>        servlets          = new ArrayList<>();

        private final List<HandlerFilterInfo>  servletFilters    = new ArrayList<>();

        private final List<HandlerMappingInfo> dispatcherServlet = new ArrayList<>();

        public List<ServletInfo> getServlets() {
            return servlets;
        }

        public List<HandlerFilterInfo> getServletFilters() {
            return servletFilters;
        }

        public List<HandlerMappingInfo> getDispatcherServlet() {
            return dispatcherServlet;
        }
    }

    public static class ServletInfo implements Serializable {

        private static final int serialVersionUID = 0x11;

        private String           mappings;

        private String           name;

        private String           className;

        public String getMappings() {
            return mappings;
        }

        public void setMappings(String mappings) {
            this.mappings = mappings;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class HandlerFilterInfo implements Serializable {

        private static final int serialVersionUID = 0x11;

        private String           urlPatternMappings;

        private String           servletNameMappings;

        private String           name;

        private String           className;

        public String getUrlPatternMappings() {
            return urlPatternMappings;
        }

        public void setUrlPatternMappings(String urlPatternMappings) {
            this.urlPatternMappings = urlPatternMappings;
        }

        public String getServletNameMappings() {
            return servletNameMappings;
        }

        public void setServletNameMappings(String servletNameMappings) {
            this.servletNameMappings = servletNameMappings;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class HandlerMappingInfo implements Serializable {

        private static final int serialVersionUID = 0x11;

        private String           predicate;

        private String           handler;

        private String           methods;

        private String           paramsType;

        private String           responseType;

        public String getPredicate() {
            return predicate;
        }

        public void setPredicate(String predicate) {
            this.predicate = predicate;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public String getMethods() {
            return methods;
        }

        public void setMethods(String methods) {
            this.methods = methods;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }

        public String getResponseType() {
            return responseType;
        }

        public void setResponseType(String responseType) {
            this.responseType = responseType;
        }
    }
}
