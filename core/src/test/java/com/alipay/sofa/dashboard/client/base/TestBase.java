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
package com.alipay.sofa.dashboard.client.base;

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppPublisher;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppSubscriber;
import com.alipay.sofa.dashboard.client.zookeeper.ZookeeperClient;
import com.alipay.sofa.dashboard.client.zookeeper.ZookeeperConfig;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/26 4:48 PM
 * @since:
 **/
public abstract class TestBase {

    protected static TestingServer             testServer;

    private final List<ZookeeperAppPublisher>  publishers  = new ArrayList<>();

    private final List<ZookeeperAppSubscriber> subscribers = new ArrayList<>();

    @Before
    public void setupZkServer() throws Exception {
        testServer = new TestingServer(22181, true);
        testServer.start();
    }

    @After
    public void recycleServer() throws IOException {
        testServer.stop();
    }

    protected ZookeeperAppPublisher newPublisher(Application app) {
        ZookeeperConfig config = new ZookeeperConfig();
        config.setAddress("127.0.0.1:22181");
        ZookeeperClient client = new ZookeeperClient(config);
        ZookeeperAppPublisher publisher = new ZookeeperAppPublisher(app, client);
        publishers.add(publisher);
        return publisher;
    }

    protected ZookeeperAppSubscriber newSubscriber() {
        ZookeeperConfig config = new ZookeeperConfig();
        config.setAddress("127.0.0.1:22181");
        ZookeeperClient client = new ZookeeperClient(config);
        ZookeeperAppSubscriber subscriber = new ZookeeperAppSubscriber(client);
        subscribers.add(subscriber);
        return subscriber;
    }
}
