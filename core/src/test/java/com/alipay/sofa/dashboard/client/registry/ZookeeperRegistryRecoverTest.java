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

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppPublisher;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppSubscriber;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryConfig;
import org.apache.curator.test.TestingServer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperRegistryRecoverTest {

    private final ZookeeperRegistryConfig config = new ZookeeperRegistryConfig();

    public ZookeeperRegistryRecoverTest() {
        config.setAddress("127.0.0.1:22181");
    }

    @Test
    public void subscriberCacheTest() throws Exception {
        final Application app = Application.newBuilder().appName("test_app1").hostName("127.0.0.1")
            .port(8080).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();

        TestingServer testServer = new TestingServer(22181, true);
        testServer.start();

        AppPublisher<?> publisher = new ZookeeperAppPublisher(config, app);
        publisher.start();
        publisher.register();

        // Create a subscriber after register
        AppSubscriber<?> subscriber = new ZookeeperAppSubscriber(config);
        subscriber.start();

        // Shutdown server, query cache will be used in a short while
        testServer.stop();
        Thread.sleep(200);

        List<Application> query = subscriber.getByName(app.getAppName());
        Assert.assertEquals(query.size(), 1);
        Assert.assertEquals(query.get(0), app);
    }

    @Test
    public void sessionRecoverTest() throws Exception {
        String appName = "test_app";
        final Application app1 = Application.newBuilder().appName(appName).hostName("10.1.1.1")
            .port(8080).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();
        final Application app2 = Application.newBuilder().appName(appName).hostName("10.1.1.2")
            .port(8080).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();

        // Start up zookeeper
        TestingServer testServer = new TestingServer(22181, true);
        testServer.start();

        // Publish app1
        AppPublisher<?> publisher1 = new ZookeeperAppPublisher(config, app1);
        publisher1.start();
        publisher1.register();

        AppPublisher<?> publisher2 = new ZookeeperAppPublisher(config, app2);
        publisher2.start();

        // Create a subscriber after register
        AppSubscriber<?> subscriber = new ZookeeperAppSubscriber(config);
        subscriber.start();

        // Query as {app1}
        List<Application> query = subscriber.getByName(appName);
        Assert.assertEquals(query.size(), 1);
        Assert.assertEquals(query.get(0), app1);

        // Reboot zookeeper, tear down app1 and set up app2
        testServer.restart();
        publisher1.shutdown();
        publisher2.register();

        // Expected:
        //  - app1: session timeout
        //  - app2: create success
        //  - Subscriber Even: SUSPENDED -> RECONNECTED
        Thread.sleep(config.getSessionTimeoutMs() + 2_000);

        // Query as {app2}
        query = subscriber.getByName(appName);
        Assert.assertEquals(query.size(), 1);
        Assert.assertEquals(query.get(0), app2);

        testServer.stop();
    }
}
