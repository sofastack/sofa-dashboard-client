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

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardClientProperties;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardZookeeperProperties;
import com.alipay.sofa.dashboard.client.registry.AppPublisher;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppPublisher;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryClient;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperRegistryConfig;
import com.alipay.sofa.dashboard.client.utils.NetworkAddressUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ SofaDashboardClientProperties.class,
                                SofaDashboardZookeeperProperties.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.dashboard.client", value = "enable", matchIfMissing = true)
public class AppPublisherConfiguration {

    private static final String KEY_SPRING_APP_NAME = "spring.application.name";

    private static final String KEY_SERVER_PORT     = "server.port";

    private static final String DEFAULT_SERVER_PORT = "8080";

    @Bean(name = "application")
    @ConditionalOnMissingBean
    public Application getApplicationInstance(Environment env, SofaDashboardClientProperties prop) {
        long current = System.currentTimeMillis();

        Application app = new Application();
        app.setAppName(env.getRequiredProperty(KEY_SPRING_APP_NAME));
        app.setHostName(getLocalIp(prop));
        app.setPort(Integer.parseInt(env.getProperty(KEY_SERVER_PORT, DEFAULT_SERVER_PORT)));
        app.setStartTime(current);
        app.setLastRecover(current);
        return app;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public AppPublisher getAppPublisher(SofaDashboardZookeeperProperties prop,
                                        Application application) {
        ZookeeperRegistryConfig config = getZookeeperRegistryConfig(prop);
        ZookeeperAppPublisher registry = new ZookeeperAppPublisher(config, application,
            zookeeperRegistryClient(prop));
        registry.start();
        return registry;
    }

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperRegistryClient zookeeperRegistryClient(SofaDashboardZookeeperProperties prop) {
        ZookeeperRegistryConfig config = getZookeeperRegistryConfig(prop);
        return new ZookeeperRegistryClient(config);
    }

    private String getLocalIp(SofaDashboardClientProperties properties) {
        NetworkAddressUtils.calculate(null, null);
        return StringUtils.isEmpty(properties.getInstanceIp()) ? NetworkAddressUtils.getLocalIP()
            : properties.getInstanceIp();
    }

    private ZookeeperRegistryConfig getZookeeperRegistryConfig(SofaDashboardZookeeperProperties prop) {
        ZookeeperRegistryConfig config = new ZookeeperRegistryConfig();
        config.setAddress(prop.getAddress());
        config.setBaseSleepTimeMs(prop.getBaseSleepTimeMs());
        config.setMaxRetries(prop.getMaxRetries());
        config.setSessionTimeoutMs(prop.getSessionTimeoutMs());
        config.setConnectionTimeoutMs(prop.getConnectionTimeoutMs());
        return config;
    }
}
