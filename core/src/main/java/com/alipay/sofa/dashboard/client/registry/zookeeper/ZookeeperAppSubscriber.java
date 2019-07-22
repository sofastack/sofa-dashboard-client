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
import com.alipay.sofa.dashboard.client.registry.AppSubscriber;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.KeeperException;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperAppSubscriber extends AppSubscriber<ZookeeperRegistryConfig> {

    private static final Logger                    LOGGER       = LoggerFactory
                                                                    .getLogger(ZookeeperAppSubscriber.class);

    /**
     * In-memory copy of zookeeper session information
     */
    private volatile Map<String, Set<Application>> applications = new ConcurrentHashMap<>();

    private final ZookeeperRegistryClient          client;

    public ZookeeperAppSubscriber(ZookeeperRegistryConfig config) {
        super(config);
        this.client = new ZookeeperRegistryClient(config);
    }

    @Override
    public boolean start() {
        boolean startFlag = client.doStart((curatorFramework -> {
            // Add listeners to manage local cache
            TreeCache cache = new TreeCache(curatorFramework,
                ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE);
            TreeCacheListener listener = (client, event) -> {
                String dataPath = event.getData() == null ? null : event.getData().getPath();
                LOGGER
                    .info("Dashboard client event type = {}, path= {}", event.getType(), dataPath);
                switch (event.getType()) {
                    case NODE_ADDED:
                    case NODE_UPDATED:
                        runInSafe(() -> doCreateOrUpdateApplications(event));
                        break;
                    case NODE_REMOVED:
                    case CONNECTION_LOST:
                        runInSafe(() -> doRemoveApplications(event));
                        break;
                    case CONNECTION_RECONNECTED: // Try to recover data while reconnected
                        runInSafe(this::doRebuildCache);
                        break;
                    default:
                        break;
                }
            };
            cache.getListenable().addListener(listener);
            try {
                cache.start();
            } catch (Exception e) {
                LOGGER.error("Start cache error.", e);
            }
        }));
        if (startFlag) {
            runInSafe(this::doRebuildCache);
        }
        return startFlag;
    }

    @Override
    public void shutdown() {
        client.doShutdown();
    }

    @Override
    public List<Application> getAll() {
        // Get a readonly copy
        Map<String, Set<Application>> copy = Collections.unmodifiableMap(this.applications);

        Set<Application> apps = copy.values().stream().reduce((a, b) -> {
            Set<Application> collector = new HashSet<>(a);
            collector.addAll(b);
            return collector;
        }).orElse(new HashSet<>());
        return new ArrayList<>(apps);
    }

    @Override
    public List<Application> getByName(@Nullable String appName) {
        // Get a readonly copy
        Map<String, Set<Application>> copy = Collections.unmodifiableMap(this.applications);

        Set<Application> apps = copy.get(appName);
        return apps == null ? new ArrayList<>() : new ArrayList<>(apps);
    }

    @Override
    public List<String> getAllNames() {
        // Get a readonly copy
        Set<String> copy = Collections.unmodifiableSet(this.applications.keySet());

        return new ArrayList<>(copy);
    }

    @Override
    public Map<String, Integer> summaryCounts() {
        // Get a readonly copy
        Map<String, Set<Application>> copy = Collections.unmodifiableMap(this.applications);

        return copy.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            it -> it.getValue() == null ? 0 : it.getValue().size()));
    }

    /**
     * Fetch all instance information from zookeeper.
     *
     * @throws Exception Zookeeper client exception
     */
    private void doRebuildCache() throws Exception {
        List<String> appNames;
        try {
            appNames = client.getCuratorClient().getChildren()
                .forPath(ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE);
            if (appNames == null || appNames.isEmpty()) {
                return;
            }
        } catch (KeeperException.NoNodeException ignore) {
            return;
        }

        final Map<String, Set<Application>> newCacheInstance = new ConcurrentHashMap<>();
        appNames.forEach((item) -> {
            String instancePath = String.format("%s/%s",
                ZookeeperConstants.SOFA_BOOT_CLIENT_INSTANCE, item);
            try {
                Set<Application> instanceList = new ConcurrentSkipListSet<>();

                List<String> instances = client.getCuratorClient().getChildren()
                    .forPath(instancePath);
                instances.forEach(instance -> {
                    String appInstance = String.format("%s/%s", instancePath, instance);
                    try {
                        byte[] bytes = client.getCuratorClient().getData().forPath(appInstance);
                        Application application = JsonUtils.parseObject(bytes, Application.class);
                        instanceList.add(application);
                    } catch (Throwable e) {
                        LOGGER.error("Error to get app instance from Zookeeper.", e);
                    }
                });
                newCacheInstance.put(item, instanceList);

            } catch (Throwable e) {
                LOGGER.error("Error to get instances from Zookeeper.", e);
            }
        });
        this.applications = newCacheInstance;
        LOGGER.info("Dashboard client init success, current app count is {}",
            newCacheInstance.size());
    }

    /**
     * Update cached application instance according to zookeeper node event.
     *
     * @param event zookeeper node changed event
     */
    private void doCreateOrUpdateApplications(TreeCacheEvent event) {
        ChildData chileData = event.getData();
        Application app = client.parseSessionNode(chileData.getPath());
        if (app != null) {
            applications.compute(app.getAppName(), (key, value) -> {
                Set<Application> group = value == null ? new ConcurrentSkipListSet<>() : value;
                group.remove(app); // remove if exists
                group.add(app);
                return group;
            });
        }
    }

    /**
     * Remove cached application instance according to zookeeper node event.
     *
     * @param event zookeeper node changed event
     */
    private void doRemoveApplications(TreeCacheEvent event) {
        ChildData chileData = event.getData();
        if (chileData == null) {
            return; // Maybe null if session is timeout
        }

        Application app = client.parseSessionNode(chileData.getPath());
        if (app != null) {
            applications.computeIfPresent(app.getAppName(), (key, value) -> {
                value.remove(app); // Always remove whatever if it's exists
                return value;
            });
        }
    }

    /**
     * A tool function to simplify try/cache logic
     *
     * @param task decorated task
     */
    private void runInSafe(ThrowableRunnable task) {
        try {
            task.run();
        } catch (Throwable err) {
            LOGGER.warn("Unexpected ZookeeperAppSubscriber error.", err);
        }
    }

    private interface ThrowableRunnable {
        void run() throws Throwable;
    }
}
