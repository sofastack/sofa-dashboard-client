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
package com.alipay.sofa.dashboard.client.registry;

import com.alipay.sofa.dashboard.client.base.TestBase;
import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppPublisher;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppSubscriber;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperRegistryRecoverTest extends TestBase {

    private final ZookeeperRegistryConfig config = new ZookeeperRegistryConfig();

    @Test
    public void subscriberCacheTest() throws Exception {
        final Application app = Application.newBuilder().appName("test_app1").hostName("127.0.0.1")
            .port(8080).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();

        AppPublisher<?> publisher = new ZookeeperAppPublisher(config, app, zookeeperRegistryClient);
        publisher.start();
        publisher.register();

        // Create a subscriber after register
        AppSubscriber<?> subscriber = new ZookeeperAppSubscriber(config);
        subscriber.start();

        List<Application> query = subscriber.getByName(app.getAppName());
        Assert.assertEquals(query.size(), 1);
        Assert.assertEquals(query.get(0), app);
    }
}
