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
package com.alipay.sofa.dashboard.client.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chpengzh@foxmail.com
 * @date 2019-08-25 15:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaDashboardContextRefreshedListenerTest.TestApplicationContext.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = { "management.endpoints.web.exposure.include=env,health,info,loggers,mappings,metrics" })
public class SofaDashboardContextRefreshedListenerTest {

    @Test
    public void contextRefreshedTest() {
        // A new context will be created, since then both
    }

    @SpringBootApplication(scanBasePackages = "no.such.package")
    public static class TestApplicationContext {
    }
}
