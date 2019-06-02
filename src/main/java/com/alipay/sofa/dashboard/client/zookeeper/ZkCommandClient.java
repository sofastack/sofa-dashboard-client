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
package com.alipay.sofa.dashboard.client.zookeeper;

import com.alipay.sofa.dashboard.client.config.SofaDashboardProperties;
import com.alipay.sofa.dashboard.client.model.ZkClientReconnectedEvent;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Provide an instance of the ZooKeeper client in the Spring application environment
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/15 11:59 AM
 * @since:
 **/
public class ZkCommandClient implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkCommandClient.class);

    private CuratorFramework    curatorClient;

    @Autowired
    SofaDashboardProperties     sofaDashboardProperties;

    @Autowired
    private ApplicationContext  context;

    @Override
    public void afterPropertiesSet() throws Exception {
        // custom policy
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                sofaDashboardProperties.getZookeeper().getBaseSleepTimeMs(),
                sofaDashboardProperties.getZookeeper().getMaxRetries());
        // to build curatorClient
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(sofaDashboardProperties.getZookeeper().getAddress())
                .sessionTimeoutMs(sofaDashboardProperties.getZookeeper().getSessionTimeoutMs())
                .connectionTimeoutMs(
                        sofaDashboardProperties.getZookeeper().getConnectionTimeoutMs())
                .retryPolicy(retryPolicy).build();

        // broadcast connection reconnected state for registry state recovering
        curatorClient.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.RECONNECTED) {
                LOGGER.info("Reconnect to Zookeeper, {} is called",
                        ZkClientReconnectedEvent.class.getSimpleName());
                context.publishEvent(new ZkClientReconnectedEvent());
            }
        });

        curatorClient.start();
    }

    public CuratorFramework getCuratorClient() {
        return curatorClient;
    }
}
