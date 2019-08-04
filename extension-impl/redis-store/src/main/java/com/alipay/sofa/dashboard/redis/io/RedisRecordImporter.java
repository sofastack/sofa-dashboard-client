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
import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class RedisRecordImporter extends RedisStoreBase implements RecordImporter {

    private static final Logger       LOGGER = LoggerFactory.getLogger(RedisRecordImporter.class);

    private final StringRedisTemplate template;

    private final long                timeoutTtl;

    public RedisRecordImporter(StringRedisTemplate template, long timeoutTtl) {
        this.template = template;
        this.timeoutTtl = TimeUnit.SECONDS.toMillis(timeoutTtl);
    }

    @Override
    public void createTablesIfNotExists(HostAndPort hostAndPort, Set<String> dimensionSchemes) {
        // Do nothing in redis case
    }

    @Override
    public void addRecords(HostAndPort hostAndPort, List<StoreRecord> records) {
        template.executePipelined((RedisCallback<Void>) connection -> {
            for (StoreRecord record : records) {
                byte[] keyName = getKeyName(hostAndPort.toInstanceId(), record.getSchemeName())
                    .getBytes(Charset.defaultCharset());
                byte[] value = JsonUtils.toJsonString(record)
                    .getBytes(Charset.defaultCharset());
                long score = record.getTimestamp();
                long expire = score - timeoutTtl;

                connection.zRemRangeByScore(keyName, 0, expire); // Expire timeout record
                connection.zAdd(keyName, score, value);
            }
            return null;
        });
    }
}
