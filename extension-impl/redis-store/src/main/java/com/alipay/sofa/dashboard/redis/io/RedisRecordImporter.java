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
package com.alipay.sofa.dashboard.redis.io;

import com.alipay.sofa.dashboard.client.io.RecordImporter;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import com.alipay.sofa.dashboard.redis.properties.SofaDashboardRedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class RedisRecordImporter extends RedisStoreBase implements RecordImporter {

    private static final Logger       LOGGER = LoggerFactory.getLogger(RedisRecordImporter.class);

    private final StringRedisTemplate template;

    public RedisRecordImporter(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void createTablesIfNotExists(String instanceId, Set<String> dimensionSchemes) {
        dimensionSchemes.forEach(name -> {
            // 设置数据超时时间
            //String keyName = getKeyName(instanceId, name);
            //template.boundZSetOps(keyName)
            //    .expire(props.getRecordTtl(), TimeUnit.SECONDS);
            //TODO: Add expire logic for each z-set element?
        });
    }

    @Override
    public void addRecords(String instanceId, List<StoreRecord> records) {
        for (StoreRecord record : records) {
            try {
                String keyName = getKeyName(instanceId, record.getSchemeName());
                String value = JsonUtils.toJsonString(record);
                double score = record.getTimestamp();
                template.boundZSetOps(keyName).add(value, score);

            } catch (Throwable err) {
                LOGGER.warn("Error in RedisRecordImporter#addRecords.", err);
            }
        }
    }

}
