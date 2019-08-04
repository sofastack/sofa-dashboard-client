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
package com.alipay.sofa.dashboard.redis.config;

import com.alipay.sofa.dashboard.redis.io.LettuceConnFactoryProvider;
import com.alipay.sofa.dashboard.redis.io.RedisRecordExporter;
import com.alipay.sofa.dashboard.redis.io.RedisRecordImporter;
import com.alipay.sofa.dashboard.redis.properties.SofaDashboardRedisProperties;
import io.lettuce.core.resource.DefaultClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Closeable;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Component
@EnableConfigurationProperties({ SofaDashboardRedisProperties.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.dashboard.redis", value = "enable", matchIfMissing = true)
public class RedisStoreConfiguration implements Closeable {

    private static final Logger          LOGGER = LoggerFactory
                                                    .getLogger(RedisStoreConfiguration.class);

    private final DefaultClientResources res;

    private final StringRedisTemplate    template;

    public RedisStoreConfiguration(SofaDashboardRedisProperties props) {
        LettuceConnFactoryProvider config = new LettuceConnFactoryProvider(props);
        res = config.lettuceClientResources();
        RedisConnectionFactory factory = config.createFactory(res);
        this.template = new StringRedisTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRecordImporter getImporter(SofaDashboardRedisProperties props) {
        return new RedisRecordImporter(template, props.getRecordTtl());
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRecordExporter getExporter() {
        return new RedisRecordExporter(template);
    }

    @Override
    public void close() {
        LOGGER.info("Shutdown redis client resources");
        this.res.shutdown();
    }
}
