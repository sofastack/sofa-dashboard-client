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

import com.alipay.sofa.dashboard.client.dimension.ApplicationDimension;
import com.alipay.sofa.dashboard.client.io.RecordImporter;
import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.client.properties.SofaDashboardClientProperties;
import com.alipay.sofa.dashboard.client.schedule.DimensionRecordingSchedule;
import com.alipay.sofa.dashboard.client.utils.NetworkAddressUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ SofaDashboardClientProperties.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.dashboard.client", value = "enable", matchIfMissing = true)
public class DimensionStoreConfiguration {

    private static final String KEY_SERVER_PORT     = "server.port";

    private static final String DEFAULT_SERVER_PORT = "8080";

    @Bean
    @ConditionalOnMissingBean
    public DimensionRecordingSchedule createStoreSchedule(List<ApplicationDimension> dimensions,
                                                          ObjectProvider<RecordImporter> storeProvider,
                                                          SofaDashboardClientProperties props,
                                                          Environment env) {
        HostAndPort hostAndPort = getHostAndPort(env, props);
        RecordImporter importer = storeProvider.getIfAvailable(EmptyRecordImporter::new);
        return new DimensionRecordingSchedule(hostAndPort, dimensions, importer,
            props.getStoreInitDelayExp(), props.getStoreUploadPeriodExp());
    }

    private HostAndPort getHostAndPort(Environment env, SofaDashboardClientProperties properties) {
        NetworkAddressUtils.calculate(null, null);
        String ip = StringUtils.isEmpty(properties.getInstanceIp()) ? NetworkAddressUtils
            .getLocalIP() : properties.getInstanceIp();
        int port = Integer.parseInt(env.getProperty(KEY_SERVER_PORT, DEFAULT_SERVER_PORT));
        return new HostAndPort(ip, port);
    }

    /**
     * An empty implement for record importer
     */
    private static class EmptyRecordImporter implements RecordImporter {

        @Override
        public void createTablesIfNotExists(HostAndPort instanceId, Set<String> dimensionSchemes) {

        }

        @Override
        public void addRecords(HostAndPort instanceId, List<StoreRecord> records) {

        }
    }
}
