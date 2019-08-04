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

import com.alipay.sofa.dashboard.client.dimension.actuator.*;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardClientProperties;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application metrics dimension
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ SofaDashboardClientProperties.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.dashboard.client", value = "enable", matchIfMissing = true)
public class AppDimensionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ActuatorEnvironmentDimension createEnvironmentDimension(EnvironmentEndpoint endpoint) {
        return new ActuatorEnvironmentDimension(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActuatorHealthDimension createHealthDimension(HealthEndpoint endpoint) {
        return new ActuatorHealthDimension(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActuatorInfoDimension createInfoDimension(InfoEndpoint endpoint) {
        return new ActuatorInfoDimension(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActuatorLoggersDimension createLoggersDimension(LoggersEndpoint endpoint) {
        return new ActuatorLoggersDimension(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActuatorMappingsDimension createMappingsDimension(MappingsEndpoint endpoint) {
        return new ActuatorMappingsDimension(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActuatorMemoryDimension createMemoryDimension(MetricsEndpoint endpoint) {
        return new ActuatorMemoryDimension(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActuatorThreadSummaryDimension createThreadSummaryDimension(MetricsEndpoint endpoint) {
        return new ActuatorThreadSummaryDimension(endpoint);
    }

}
