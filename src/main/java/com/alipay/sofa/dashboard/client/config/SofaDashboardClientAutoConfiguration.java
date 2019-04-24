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
package com.alipay.sofa.dashboard.client.config;

import com.alipay.sofa.dashboard.client.listener.ApplicationContextClosedListener;
import com.alipay.sofa.dashboard.client.listener.ApplicationContextRefreshedListener;
import com.alipay.sofa.dashboard.client.registration.SofaDashboardClientRegister;
import com.alipay.sofa.dashboard.client.zookeeper.ZkCommandClient;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/15 2:03 PM
 * @since:
 **/
@Configuration
@EnableConfigurationProperties({ SofaDashboardProperties.class })
@ConditionalOnWebApplication
@Conditional(SofaDashboardClientEnabledCondition.class)
@ConditionalOnClass(CuratorFramework.class)
public class SofaDashboardClientAutoConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public SofaDashboardClientRegister registrator(SofaDashboardProperties sofaClientProperties,
                                                   ZkCommandClient commandClient) {
        return new SofaDashboardClientRegister(sofaClientProperties, commandClient, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public ZkCommandClient zkCommandClient() {
        return new ZkCommandClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextRefreshedListener applicationContextRefreshedListener() {
        return new ApplicationContextRefreshedListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextClosedListener applicationContextClosedListener() {
        return new ApplicationContextClosedListener();
    }
}
