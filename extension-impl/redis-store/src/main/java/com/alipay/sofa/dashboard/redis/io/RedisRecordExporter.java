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

import com.alipay.sofa.dashboard.client.io.RecordExporter;
import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class RedisRecordExporter extends RedisStoreBase implements RecordExporter {

    private final StringRedisTemplate template;

    public RedisRecordExporter(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public List<StoreRecord> getLatestRecords(HostAndPort hostAndPort, String schemeName,
                                              long duration) {
        String keyName = getKeyName(hostAndPort.toInstanceId(), schemeName);
        long current = System.currentTimeMillis();
        long queryMin = System.currentTimeMillis() - duration;

        Set<String> records = template.boundZSetOps(keyName)
            .rangeByScore(queryMin, current);
        return Optional.ofNullable(records).orElse(new HashSet<>())
            .stream()
            .map(it -> JsonUtils.parseObject(it, StoreRecord.class))
            .sorted(Comparator.comparingLong(StoreRecord::getTimestamp))
            .collect(Collectors.toList());
    }
}
