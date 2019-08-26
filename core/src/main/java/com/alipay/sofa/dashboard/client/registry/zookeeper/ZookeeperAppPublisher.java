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
import com.alipay.sofa.dashboard.client.registry.AppPublisher;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperAppPublisher extends AppPublisher<ZookeeperRegistryConfig> {

    private static final Logger           LOGGER = LoggerFactory
                                                     .getLogger(ZookeeperAppPublisher.class);

    private final ZookeeperRegistryClient zookeeperRegistryClient;

    private final ReentrantLock           lock   = new ReentrantLock(true);

    private volatile String               currentSession;

    public ZookeeperAppPublisher(ZookeeperRegistryConfig config, Application application,
                                 ZookeeperRegistryClient zookeeperRegistryClient) {
        super(application, config);
        this.zookeeperRegistryClient = zookeeperRegistryClient;
    }

    @Override
    public boolean start() {
        return zookeeperRegistryClient.doStart((curatorFramework) ->
            curatorFramework.getConnectionStateListenable().addListener((cli, newState) -> {
                if (newState == ConnectionState.RECONNECTED) {
                    LOGGER.info("Try to recover session node while reconnected");
                    try {
                        register();
                    } catch (Exception e) {
                        LOGGER.error("Recover session error", e);
                    }
                }
            }));
    }

    @Override
    public void shutdown() {
        zookeeperRegistryClient.doShutdown();
    }

    @Override
    public void register() throws Exception {
        lock.lock();
        try {
            Application app = getApplication();
            app.setLastRecover(System.currentTimeMillis()); // Change recover time

            if (!zookeeperRegistryClient.isRunning()) {
                return;
            }

            if (!StringUtils.isEmpty(currentSession)) {
                try {
                    zookeeperRegistryClient.getCuratorClient().delete().forPath(currentSession);
                    currentSession = null;
                } catch (Exception e) {
                    LOGGER.warn("Error while recycle old session node {}", currentSession, e);
                }
            }

            Stat stat = zookeeperRegistryClient.getCuratorClient().checkExists()
                .forPath(ZookeeperConstants.SOFA_BOOT_CLIENT_ROOT);
            if (stat == null) {
                zookeeperRegistryClient.getCuratorClient().create()
                    .creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(ZookeeperConstants.SOFA_BOOT_CLIENT_ROOT);
            }
            byte[] bytes = JsonUtils.toJsonBytes(app);
            String sessionNode = zookeeperRegistryClient.toSessionNode(app);
            currentSession = zookeeperRegistryClient.getCuratorClient().create()
                .creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(sessionNode, bytes);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unRegister() throws Exception {
        lock.lock();
        try {
            Application app = getApplication();

            if (zookeeperRegistryClient.isRunning()) {
                String sessionNode = zookeeperRegistryClient.toSessionNode(app);
                zookeeperRegistryClient.getCuratorClient().delete().forPath(sessionNode);
            }
        } finally {
            lock.unlock();
        }
    }

}
