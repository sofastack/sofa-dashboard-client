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
import com.alipay.sofa.dashboard.client.ark.ArkBizLifecycleHandler;
import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardClientProperties;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardZookeeperProperties;
import com.alipay.sofa.dashboard.client.registry.AppPublisher;
import com.alipay.sofa.dashboard.client.registry.zookeeper.ZookeeperAppPublisher;
import com.alipay.sofa.dashboard.client.utils.NetworkAddressUtils;
import com.alipay.sofa.dashboard.client.zookeeper.LifecycleHandler;
import com.alipay.sofa.dashboard.client.zookeeper.ZookeeperClient;
import com.alipay.sofa.dashboard.client.zookeeper.ZookeeperConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

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
        app.setHostName(getHostIp(prop));
        app.setInternalHost(getInteralHost(prop));
        app.setPort(getPort(prop, env));
        app.setStartTime(current);
        app.setLastRecover(current);
        return app;
    }

    private String getInteralHost(SofaDashboardClientProperties properties) {
        return properties.getInternalHost();
    }

    @Bean
    @ConditionalOnMissingBean
    public IntrospectBizEndpoint introspectBizEndpoint() {
        return new IntrospectBizEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public ArkBizLifecycleHandler bizStateListener(IntrospectBizEndpoint endpoint,
                                                   Application application) {
        return new ArkBizLifecycleHandler(endpoint, application);
    }

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperConfig getZookeeperRegistryConfig(SofaDashboardZookeeperProperties prop) {
        ZookeeperConfig config = new ZookeeperConfig();
        config.setAddress(prop.getAddress());
        config.setBaseSleepTimeMs(prop.getBaseSleepTimeMs());
        config.setMaxRetries(prop.getMaxRetries());
        config.setSessionTimeoutMs(prop.getSessionTimeoutMs());
        config.setConnectionTimeoutMs(prop.getConnectionTimeoutMs());
        return config;
    }

    @Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean
	public ZookeeperClient zookeeperRegistryClient(SofaDashboardZookeeperProperties prop, List<LifecycleHandler> provider) {
		ZookeeperConfig config = getZookeeperRegistryConfig(prop);
		ZookeeperClient client = new ZookeeperClient(config);
		if (!CollectionUtils.isEmpty(provider)) {
			provider.forEach(client::addLifecycleHandler);
		}
		return client;
	}

    @Bean
    @ConditionalOnMissingBean
    public AppPublisher getAppPublisher(Application application, ZookeeperClient client) {
        return new ZookeeperAppPublisher(application, client);
    }

    private int getPort(SofaDashboardClientProperties properties, Environment env) {
        String virtualPort = properties.getVirtualPort();
        if (StringUtils.isEmpty(virtualPort)) {
            return Integer.parseInt(env.getProperty(KEY_SERVER_PORT, DEFAULT_SERVER_PORT));
        } else {
            return Integer.valueOf(virtualPort);
        }
    }

    private String getHostIp(SofaDashboardClientProperties properties) {
        NetworkAddressUtils.calculate(null, null);
        String ip = null;
        boolean isInstanceIpEmpty = StringUtils.isEmpty(properties.getInstanceIp());
        if (isInstanceIpEmpty) {
            boolean isVirtualHostEmpty = StringUtils.isEmpty(properties.getVirtualHost());
            if (isVirtualHostEmpty) {
                ip = NetworkAddressUtils.getLocalIP();
            } else {
                ip = properties.getVirtualHost();
            }
        } else {
            ip = properties.getInstanceIp();
        }
        return ip;
    }
}
