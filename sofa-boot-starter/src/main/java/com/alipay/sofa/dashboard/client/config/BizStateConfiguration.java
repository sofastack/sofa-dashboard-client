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

import com.alipay.sofa.ark.springboot2.endpoint.IntrospectBizEndpoint;
import com.alipay.sofa.dashboard.client.listener.BizStateListener;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardClientProperties;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardZookeeperProperties;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/26 2:49 PM
 * @since:
 **/
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ SofaDashboardClientProperties.class,
                                SofaDashboardZookeeperProperties.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.dashboard.client", value = "enable", matchIfMissing = true)
public class BizStateConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BizStateListener bizStateListener(ZookeeperRegistryClient zookeeperRegistryClient,
                                             Environment env, SofaDashboardClientProperties prop) {
        return new BizStateListener(zookeeperRegistryClient, prop, env, introspectBizEndpoint());
    }

    @Bean
    @ConditionalOnMissingBean
    public IntrospectBizEndpoint introspectBizEndpoint() {
        return new IntrospectBizEndpoint();
    }
}
