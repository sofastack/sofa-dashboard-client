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
package com.alipay.sofa.dashboard.client.registry.zookeeper;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import com.alipay.sofa.dashboard.client.model.common.Application;

/**
 * @author chpengzh@didiglobal.com
 * @date 2019-08-29 09:18
 */
final class ZookeeperRegistryUtils {

    private static final Logger LOGGER              = LoggerFactory
                                                        .getLogger(ZookeeperRegistryUtils.class);

    private static final String INSTANCE_PREFIX     = ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE
                                                      + ZookeeperConstants.SEPARATOR;

    private static final int    INSTANCE_PREFIX_LEN = INSTANCE_PREFIX.length();

    /**
     * Convert an instance definition into session node name
     *
     * @param instance
     *            application instance
     * @return session node name
     */
    @NonNull
    static String toSessionNode(Application instance) {
        String appId = String.format("%s:%d?internalHost=%s&startTime=%d&lastRecover=%d&state=%s",
            instance.getHostName(), instance.getPort(), instance.getInternalHost(),
            instance.getStartTime(), instance.getLastRecover(), instance.getAppState());
        String appName = instance.getAppName();
        return String.format("%s/%s/%s", ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE, appName,
            appId);
    }

    /**
     * Parse session node to application instance.
     *
     * @param sessionNode
     *            session node path
     * @return {@code null}, if this session path is not a legal one
     */
    @Nullable
    static Application parseSessionNode(String sessionNode) {
        try {
            if (sessionNode == null || !sessionNode.startsWith(INSTANCE_PREFIX)) {
                return null;
            }

            String nameAndIns = sessionNode.substring(INSTANCE_PREFIX_LEN);
            if (!nameAndIns.contains(ZookeeperConstants.SEPARATOR)) {
                return null;
            }

            String[] segments = nameAndIns.split(ZookeeperConstants.SEPARATOR);
            if (segments.length != 2 || !segments[1].contains(ZookeeperConstants.QUERY)
                || !segments[1].contains(ZookeeperConstants.COLON)) {
                return null;
            }

            String appName = segments[0];
            String instancePart = segments[1];
            // Use a dummy schema for uri parsing
            URI instanceUri = URI.create("dummy://" + instancePart);
            Map<String, String> query = splitQuery(instanceUri.getQuery());

            Application application = new Application();
            application.setAppName(appName);
            application.setInternalHost(query.get("internalHost"));
            application.setHostName(instanceUri.getHost());
            application.setPort(instanceUri.getPort());
            application.setAppState(query.get("state"));
            application.setStartTime(Long.parseLong(query.get("startTime")));
            application.setLastRecover(Long.parseLong(query.get("lastRecover")));
            return application;

        } catch (Throwable err) {
            LOGGER.warn("Ignore parse err of path " + sessionNode, err);
            return null;
        }
    }

    private static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> result = new LinkedHashMap<>();
        String[] pairs = query.split(ZookeeperConstants.AND);
        for (String pair : pairs) {
            int idx = pair.indexOf(ZookeeperConstants.EQUAL);
            String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
            String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            result.put(key, value);
        }
        return result;
    }
}
