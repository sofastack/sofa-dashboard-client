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

import com.alipay.sofa.dashboard.client.model.common.Application;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperRegistryClient {

    private static final Logger    LOGGER              = LoggerFactory
                                                           .getLogger(ZookeeperRegistryClient.class);

    private final AtomicBoolean    start               = new AtomicBoolean(false);

    private final AtomicBoolean    shutdown            = new AtomicBoolean(false);

    private final String           INSTANCE_PREFIX     = ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE
                                                         + ZookeeperConstants.SEPARATOR;

    private final int              INSTANCE_PREFIX_LEN = INSTANCE_PREFIX.length();

    private final CuratorFramework curatorClient;

    public ZookeeperRegistryClient(ZookeeperRegistryConfig config) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(config.getBaseSleepTimeMs(),
            config.getMaxRetries());
        // to build curatorClient
        curatorClient = CuratorFrameworkFactory.builder().connectString(config.getAddress())
            .sessionTimeoutMs(config.getSessionTimeoutMs())
            .connectionTimeoutMs(config.getConnectionTimeoutMs()).retryPolicy(retryPolicy).build();
    }

    boolean isRunning() {
        return start.get() && !shutdown.get();
    }

    public CuratorFramework getCuratorClient() {
        return curatorClient;
    }

    boolean doStart(Consumer<CuratorFramework> onStart) {
        if (start.compareAndSet(false, true)) {
            onStart.accept(curatorClient);
            curatorClient.start();
            return true;
        }
        // Since registry is already started
        return false;
    }

    void doShutdown() {
        if (shutdown.compareAndSet(false, true)) {
            curatorClient.close();
        }
    }

    /**
     * Convert an instance definition into session node name
     *
     * @param instance application instance
     * @return session node name
     */
    @NonNull
    String toSessionNode(Application instance) {
        String appId = String.format("%s:%d?startTime=%d&lastRecover=%d&state=%s",
            instance.getHostName(), instance.getPort(), instance.getStartTime(),
            instance.getLastRecover(), instance.getAppState());
        String appName = instance.getAppName();
        return String.format("%s/%s/%s", ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE, appName,
            appId);
    }

    /**
     * Parse session node to application instance.
     *
     * @param sessionNode session node path
     * @return {@code null}, if this session path is not a legal one
     */
    @Nullable
    Application parseSessionNode(String sessionNode) {
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

    private Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
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
