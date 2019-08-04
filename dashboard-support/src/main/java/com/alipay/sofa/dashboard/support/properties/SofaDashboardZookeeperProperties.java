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
package com.alipay.sofa.dashboard.support.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "com.alipay.sofa.dashboard.zookeeper")
public class SofaDashboardZookeeperProperties {

    /**
     * Zookeeper 工作地址.
     */
    private String address             = "127.0.0.1:2181";

    /**
     * Zookeeper 客户端错误重试间隔(ms).
     */
    private int    baseSleepTimeMs     = 1000;

    /**
     * Zookeeper 客户端最大重试次数.
     */
    private int    maxRetries          = 3;

    /**
     * Zookeeper 客户端会话超时时间(ms).
     */
    private int    sessionTimeoutMs    = 6000;

    /**
     * Zookeeper 客户端超时时间(ms).
     */
    private int    connectionTimeoutMs = 6000;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    @Override
    public String toString() {
        return "SofaDashboardZookeeperProperties{" + "address='" + address + '\''
               + ", baseSleepTimeMs=" + baseSleepTimeMs + ", maxRetries=" + maxRetries
               + ", sessionTimeoutMs=" + sessionTimeoutMs + ", connectionTimeoutMs="
               + connectionTimeoutMs + '}';
    }
}