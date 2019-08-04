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
package com.alipay.sofa.dashboard.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "com.alipay.sofa.dashboard.client")
public class SofaDashboardClientProperties {

    /**
     * 是否可用
     */
    private boolean enable               = true;

    /**
     * 实例地址
     */
    private String  instanceIp           = "";

    /**
     * Dashboard度量数据存储上报延迟期望(s)
     */
    private long    storeInitDelayExp    = 30;

    /**
     * Dashboard度量数据存储上报周期(s)
     */
    private long    storeUploadPeriodExp = 60;

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

    public long getStoreInitDelayExp() {
        return storeInitDelayExp;
    }

    public void setStoreInitDelayExp(long storeInitDelayExp) {
        this.storeInitDelayExp = storeInitDelayExp;
    }

    public long getStoreUploadPeriodExp() {
        return storeUploadPeriodExp;
    }

    public void setStoreUploadPeriodExp(long storeUploadPeriodExp) {
        this.storeUploadPeriodExp = storeUploadPeriodExp;
    }

    @Override
    public String toString() {
        return "SofaDashboardClientProperties{" + "enable=" + enable + ", instanceIp='"
               + instanceIp + '\'' + ", storeInitDelayExp=" + storeInitDelayExp
               + ", storeUploadPeriodExp=" + storeUploadPeriodExp + '}';
    }
}