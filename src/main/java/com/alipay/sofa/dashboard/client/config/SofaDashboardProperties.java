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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/15 1:50 PM
 * @since:
 **/
@ConfigurationProperties(prefix = "com.alipay.sofa.dashboard")
public class SofaDashboardProperties {

    private SofaDashboardClientProperties    client    = new SofaDashboardClientProperties();

    private SofaDashboardZookeeperProperties zookeeper = new SofaDashboardZookeeperProperties();

    public SofaDashboardClientProperties getClient() {
        return client;
    }

    public void setClient(SofaDashboardClientProperties client) {
        this.client = client;
    }

    public SofaDashboardZookeeperProperties getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(SofaDashboardZookeeperProperties zookeeper) {
        this.zookeeper = zookeeper;
    }

    public static class SofaDashboardClientProperties {
        /**
         * 是否可用
         */
        private boolean enable     = true;

        /**
         * 实例地址
         */
        private String  instanceIp = "";

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getInstanceIp() {
            return instanceIp;
        }

        public void setInstanceIp(String instanceIp) {
            this.instanceIp = instanceIp;
        }

        @Override
        public String toString() {
            return "SofaDashboardClientProperties{" + "enable=" + enable + ", instanceIp='"
                   + instanceIp + '\'' + '}';
        }
    }

    public static class SofaDashboardZookeeperProperties {

        private String address;

        private int    baseSleepTimeMs     = 1000;

        private int    maxRetries          = 3;

        private int    sessionTimeoutMs    = 6000;

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
}
