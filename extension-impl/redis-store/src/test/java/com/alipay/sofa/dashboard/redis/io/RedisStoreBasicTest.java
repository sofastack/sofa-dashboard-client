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

import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.redis.context.RedisTestContext;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisTestContext.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class RedisStoreBasicTest {

    private static RedisServer  redisServer;

    @Autowired
    private RedisRecordImporter importer;

    @Autowired
    private RedisRecordExporter exporter;

    @BeforeClass
    public static void initServer() throws IOException {
        redisServer = new RedisServer(26379);
        redisServer.start();
    }

    @AfterClass
    public static void recycleServer() {
        redisServer.stop();
    }

    @Test
    public void writeRecordAndRead() {
        final HostAndPort hostAndPort = new HostAndPort("127.0.0.1", 8080);
        final String schemeName = "test";
        List<StoreRecord> samples = Lists.newArrayList(StoreRecord.newBuilder()
            .schemeName(schemeName).timestamp(System.currentTimeMillis()).value("aaaaa").build());

        importer.createTablesIfNotExists(hostAndPort, Sets.newHashSet(schemeName));
        importer.addRecords(hostAndPort, samples);

        List<StoreRecord> query = exporter.getLatestRecords(hostAndPort, schemeName, 60_000);
        Assert.assertArrayEquals(samples.toArray(), query.toArray());
    }
}
