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

import com.alipay.sofa.dashboard.client.base.SpringBootWebApplication;
import com.alipay.sofa.dashboard.client.registration.SofaDashboardClientRegister;
import com.alipay.sofa.dashboard.client.zookeeper.ZkCommandClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/4/10 10:56 AM
 * @since:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-enable.properties")
public class EnableSofaDashboardClientRegisterTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testSofaDashboardClientRegister() throws Exception {
        try {
            applicationContext.getBean(ZkCommandClient.class);
            Assert.fail("ZkCommandClient should not be registered");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("No qualifying bean of type"));
        }

        try {
            applicationContext.getBean(SofaDashboardClientRegister.class);
            Assert.fail("SofaDashboardClientRegister should not be registered");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("No qualifying bean of type"));
        }
    }
}
