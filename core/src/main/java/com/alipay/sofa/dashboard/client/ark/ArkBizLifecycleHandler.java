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
package com.alipay.sofa.dashboard.client.ark;

import com.alipay.sofa.ark.api.ClientResponse;
import com.alipay.sofa.ark.springboot2.endpoint.IntrospectBizEndpoint;
import com.alipay.sofa.dashboard.client.common.Constants;
import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import com.alipay.sofa.dashboard.client.zookeeper.LifecycleHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ArkBiz 相关业务注册逻辑
 *
 * @author chpengzh@didiglobal.com
 * @date 2019-08-29 09:40
 */
public class ArkBizLifecycleHandler implements LifecycleHandler {

    private static final Logger         LOGGER = LoggerFactory
                                                   .getLogger(ArkBizLifecycleHandler.class);

    private final IntrospectBizEndpoint introspectBizEndpoint;

    private final Application           application;

    public ArkBizLifecycleHandler(IntrospectBizEndpoint introspectBizEndpoint,
                                  Application application) {
        this.introspectBizEndpoint = introspectBizEndpoint;
        this.application = application;
    }

    @Override
    public String getName() {
        return "ArkBizLifecycleHandler";
    }

    @Override
    public void beforeStart(CuratorFramework client) {
        try {
            // just check ,if no ark env , it will be throw exception
            introspectBizEndpoint.bizState();
        } catch (Exception arkException) {
            return;
        }
    }

    @Override
    public void afterStarted(CuratorFramework client) {
        String bizPath = getBizPath();

        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(bizPath);

            NodeCache nodeCache = new NodeCache(client, getBizPath());
            NodeCacheListener listener = () -> {
                if (introspectBizEndpoint.bizState() instanceof ClientResponse) {
                    ClientResponse clientResponse = (ClientResponse) introspectBizEndpoint.bizState();
                    byte[] bytes = JsonUtils.toJsonBytes(clientResponse);
                    client.setData().forPath(bizPath, bytes);
                }
            };
            nodeCache.getListenable().addListener(listener);
            try {
                nodeCache.start();
            } catch (Exception e) {
                LOGGER.error("Error to start listener to biz path {}", bizPath, e);
            }

        } catch (Exception e) {
            LOGGER.error("Error to create to biz path {}", bizPath, e);
        }
    }

    private String getBizPath() {
        return Constants.SOFA_BOOT_CLIENT_ROOT + Constants.SOFA_BOOT_CLIENT_BIZ
               + Constants.SEPARATOR + application.getAppName() + Constants.SEPARATOR
               + application.getHostName();
    }
}
