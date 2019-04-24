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
package com.alipay.sofa.dashboard.client;

import com.alipay.sofa.dashboard.client.base.AbstractTestBase;
import com.alipay.sofa.dashboard.client.config.SofaDashboardProperties;
import com.alipay.sofa.dashboard.client.registration.SofaDashboardClientRegister;
import com.alipay.sofa.dashboard.client.zookeeper.ZkCommandClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.Stat;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import java.io.IOException;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/4/10 10:42 AM
 * @since:
 **/
public class SofaDashboardClientRegisterTest extends AbstractTestBase {

    private static TestingServer    server;

    private static CuratorFramework client;

    @Autowired
    SofaDashboardProperties         sofaDashboardProperties;

    @Autowired
    ApplicationContext              applicationContext;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new TestingServer(2181, true);
        server.start();

    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        server.stop();
    }

    @Test
    public void testSofaDashboardClientRegister() throws Exception {
        client = CuratorFrameworkFactory.newClient(sofaDashboardProperties.getZookeeper()
            .getAddress(), new ExponentialBackoffRetry(1000, 3));
        client.start();
        ZkCommandClient zkCommandClient = applicationContext.getBean(ZkCommandClient.class);
        Assert.assertNotNull(zkCommandClient);

        SofaDashboardClientRegister sofaDashboardClientRegister = applicationContext
            .getBean(SofaDashboardClientRegister.class);
        Assert.assertNotNull(sofaDashboardClientRegister);

        Stat stat = client.checkExists().forPath("/apps/instance/test");
        Assert.assertNotNull(stat);
        client.close();
    }

    @Test
    public void testSofaDashboardProperties() {
        Assert.assertEquals("127.0.0.1", sofaDashboardProperties.getClient().getInstanceIp());
        Assert.assertTrue(sofaDashboardProperties.getClient().isEnable());
        Assert.assertEquals("127.0.0.1:2181", sofaDashboardProperties.getZookeeper().getAddress());
        Assert.assertEquals(3, sofaDashboardProperties.getZookeeper().getMaxRetries());
        Assert.assertEquals(1000, sofaDashboardProperties.getZookeeper().getBaseSleepTimeMs());
        Assert.assertEquals(6000, sofaDashboardProperties.getZookeeper().getSessionTimeoutMs());
        Assert.assertEquals(6000, sofaDashboardProperties.getZookeeper().getConnectionTimeoutMs());
    }
}
