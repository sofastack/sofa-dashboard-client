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

import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryClient;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryConfig;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/26 4:48 PM
 * @since:
 **/
public abstract class TestBase {

    protected static ZookeeperRegistryClient zookeeperRegistryClient;

    protected static TestingServer           testServer;

    @Before
    public void setupZkServer() throws Exception {
        testServer = new TestingServer(2181, true);
        testServer.start();

        ZookeeperRegistryConfig config = new ZookeeperRegistryConfig();
        config.setAddress("127.0.0.1:2181");
        zookeeperRegistryClient = new ZookeeperRegistryClient(config);
    }

    @After
    public void recycleServer() throws IOException {
        zookeeperRegistryClient.getCuratorClient().close();
        testServer.stop();
    }
}
