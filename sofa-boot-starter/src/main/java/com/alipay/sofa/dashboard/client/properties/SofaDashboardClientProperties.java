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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.google.common.base.CaseFormat;

@ConfigurationProperties(prefix = "com.alipay.sofa.dashboard.client")
public class SofaDashboardClientProperties {

    /**
     * 兼容SOFA-RPC/SOFA-BOOT配置
     */
    public static final String SOFA_RPC_PREFIX       = "com.alipay.sofa.rpc";

    public static final String SOFA_DASHBOARD_PREFIX = "com.alipay.sofa.dashboard.client";

    @Autowired
    private Environment        environment;

    /**
     * virtual host for service publish（服务发布虚拟host） 主要用于向RPC注册
     */
    private String             virtualHost;

    /**
     * virtual port for service publish（服务发布虚拟端口） * 主要用于向RPC注册
     */
    private String             virtualPort;

    /**
     * 内部IP
     */
    private String             internalHost;

    /**
     * 是否可用
     */
    private boolean            enable                = true;

    /**
     * 实例地址
     */
    private String             instanceIp            = "";

    /**
     * Dashboard度量数据存储上报延迟期望(s)
     */
    private long               storeInitDelayExp     = 30;

    /**
     * Dashboard度量数据存储上报周期(s)
     */
    private long               storeUploadPeriodExp  = 60;

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

    public String getInternalHost() {
        if (environment.containsProperty("rpc_register_internal_host")) {
            return environment.getProperty("rpc_register_internal_host");
        }
        return StringUtils.isEmpty(internalHost) ? getPropertiesByOrder(new Object() {
        }.getClass().getEnclosingMethod().getName()) : internalHost;
    }

    public void setInternalHost(String internalHost) {
        this.internalHost = internalHost;
    }

    public String getVirtualHost() {
        if (environment.containsProperty("rpc_register_virtual_host")) {
            return environment.getProperty("rpc_register_virtual_host");
        }
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        return StringUtils.isEmpty(virtualHost) ? getPropertiesByOrder(name) : virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getVirtualPort() {
        if (environment.containsProperty("rpc_register_virtual_port")) {
            return environment.getProperty("rpc_register_virtual_port");
        }
        return StringUtils.isEmpty(virtualPort) ? getPropertiesByOrder(new Object() {
        }.getClass().getEnclosingMethod().getName()) : virtualPort;
    }

    public void setVirtualPort(String virtualPort) {
        this.virtualPort = virtualPort;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private String getPropertiesByOrder(String enclosingMethodName) {
        if (environment == null) {
            return null;
        }
        String key = SOFA_DASHBOARD_PREFIX + "." + camelToDot(enclosingMethodName.substring(3));
        String dashboardPropertie = environment.getProperty(key);
        if (StringUtils.isEmpty(dashboardPropertie)) {
            String property = environment
                .getProperty(SOFA_RPC_PREFIX + "." + camelToDot(enclosingMethodName.substring(3)));
            return property;
        } else {
            return dashboardPropertie;
        }
    }

    public String camelToDot(String camelCaseString) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, camelCaseString).replaceAll("-",
            ".");
    }

    @Override
    public String toString() {
        return "SofaDashboardClientProperties{" + "enable=" + enable + ", instanceIp='"
               + instanceIp + '\'' + ", storeInitDelayExp=" + storeInitDelayExp
               + ", storeUploadPeriodExp=" + storeUploadPeriodExp + "virtualHost=" + virtualHost
               + "virtualPort=" + virtualPort + "internalHost=" + internalHost + '}';
    }
}