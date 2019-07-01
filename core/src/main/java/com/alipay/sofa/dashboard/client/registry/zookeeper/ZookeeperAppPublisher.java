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

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperAppPublisher extends AppPublisher<ZookeeperRegistryConfig> {

    private final ZookeeperRegistryClient client;

    private static final Logger           LOGGER = LoggerFactory
                                                     .getLogger(ZookeeperAppPublisher.class);

    public ZookeeperAppPublisher(ZookeeperRegistryConfig config, Application application) {
        super(application, config);
        this.client = new ZookeeperRegistryClient(config);
    }

    @Override
    public boolean start() {
        return client.doStart((curatorFramework) ->
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
        client.doShutdown();
    }

    @Override
    public synchronized void register() throws Exception {
        Application app = getApplication();
        app.setLastRecover(System.currentTimeMillis()); // Change recover time

        if (client.isRunning()) {
            Stat stat = client.getCuratorClient().checkExists()
                .forPath(ZookeeperConstants.SOFA_BOOT_CLIENT_ROOT);
            if (stat == null) {
                client.getCuratorClient().create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(ZookeeperConstants.SOFA_BOOT_CLIENT_ROOT);
            }
            byte[] bytes = JsonUtils.toJsonBytes(app);
            String sessionNode = client.toSessionNode(app);
            client.getCuratorClient().create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(sessionNode, bytes);
        }
    }

    @Override
    public void unRegister() throws Exception {
        Application app = getApplication();

        if (client.isRunning()) {
            String sessionNode = client.toSessionNode(app);
            client.getCuratorClient().delete().forPath(sessionNode);
        }
    }

}
