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
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperRegistryBasicTest extends TestBase {

    private static final Logger           LOGGER = LoggerFactory
                                                     .getLogger(ZookeeperRegistryBasicTest.class);

    private final ZookeeperRegistryConfig config = new ZookeeperRegistryConfig();

    @Before
    public void clearData() {
        if (testServer.getTempDirectory().delete()) {
            LOGGER.info("Clear data for next test case");
        }
    }

    @Test
    public void queryNoneApplication() throws Exception {
        Application app = Application.newBuilder().appName("test_app1").hostName("127.0.0.1")
            .port(8080).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();

        AppPublisher<?> publisher = new ZookeeperAppPublisher(config, app, zookeeperRegistryClient);
        publisher.start();
        publisher.register();

        AppSubscriber<?> subscriber = new ZookeeperAppSubscriber(config);
        subscriber.start();

        List<Application> query = subscriber.getByName("no_such_app");
        Assert.assertNotNull(query);
        Assert.assertTrue(query.isEmpty());

        // -- do recycle --
        publisher.shutdown();
        subscriber.shutdown();
    }

    @Test
    public void registerMultiAppsAndQuery() throws Exception {
        Application app1 = Application.newBuilder().appName("test_app1").hostName("127.0.0.1")
            .port(8080).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();
        Application app2 = Application.newBuilder().appName("test_app2").hostName("127.0.0.1")
            .port(8081).startTime(System.currentTimeMillis())
            .lastRecover(System.currentTimeMillis()).appState("UP").build();

        // Register two different applications
        AppPublisher<?> publisher1 = new ZookeeperAppPublisher(config, app1,
            zookeeperRegistryClient);
        publisher1.start();
        publisher1.register();

        AppPublisher<?> publisher2 = new ZookeeperAppPublisher(config, app2,
            zookeeperRegistryClient);
        publisher2.start();
        publisher2.register();

        // Create a subscriber after register
        AppSubscriber<?> subscriber = new ZookeeperAppSubscriber(config);
        subscriber.start();

        // Query applications
        List<Application> allApps = subscriber.getAll();
        Assert.assertEquals(allApps.size(), 2);

        List<Application> queryApp1 = subscriber.getByName(app1.getAppName());
        Assert.assertEquals(queryApp1.size(), 1);
        Assert.assertEquals(queryApp1.get(0), app1);

        List<Application> queryApp2 = subscriber.getByName(app2.getAppName());
        Assert.assertEquals(queryApp2.size(), 1);
        Assert.assertEquals(queryApp2.get(0), app2);

        // -- do recycle --
        publisher1.shutdown();
        publisher2.shutdown();
        subscriber.shutdown();
    }

    @Test
    public void registerMultiInstanceAndQuery() throws Exception {
        final String appName = "test_app";

        List<Application> samples = new ArrayList<>();
        List<AppPublisher<?>> publishers = new ArrayList<>();

        // Create multi application instances with same name
        for (String instanceIp : new String[] { "10.1.1.1", "10.1.1.2", "10.1.1.3", "10.1.1.4" }) {
            Application app = Application.newBuilder().appName(appName).hostName(instanceIp)
                .port(8080).startTime(System.currentTimeMillis())
                .lastRecover(System.currentTimeMillis()).appState("UP").build();
            samples.add(app);

            AppPublisher<?> publisher = new ZookeeperAppPublisher(config, app,
                zookeeperRegistryClient);
            publisher.start();
            publisher.register();
            publishers.add(publisher);
        }
        samples.sort(Comparator.naturalOrder());

        // Create a subscriber after register
        AppSubscriber<?> subscriber = new ZookeeperAppSubscriber(config);
        subscriber.start();

        //Query applications
        List<Application> query = subscriber.getByName(appName);
        query.sort(Comparator.naturalOrder());

        Assert.assertEquals(query.size(), samples.size());
        Assert.assertArrayEquals(samples.toArray(), query.toArray());

        // -- do recycle --
        subscriber.shutdown();
        for (AppPublisher<?> publisher : publishers) {
            publisher.shutdown();
        }
    }

}
