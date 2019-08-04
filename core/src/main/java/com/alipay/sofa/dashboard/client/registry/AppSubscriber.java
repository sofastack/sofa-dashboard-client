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
package com.alipay.sofa.dashboard.client.registry;

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.model.common.RegistryConfig;

import java.util.List;
import java.util.Map;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public abstract class AppSubscriber<CFG extends RegistryConfig> {

    private final CFG config;

    public AppSubscriber(CFG config) {
        this.config = config;
    }

    public CFG getConfig() {
        return config;
    }

    /**
     * Startup registry.
     *
     * @return return {@code false} if it is already started
     */
    public abstract boolean start();

    /**
     * Shutdown registry.
     */
    public abstract void shutdown();

    /**
     * Get all application instances.
     *
     * @return application instance list
     */
    public abstract List<Application> getAll();

    /**
     * Get application instances by name.
     *
     * @return application instance list
     */
    public abstract List<Application> getByName(String appName);

    /**
     * Get all application names
     *
     * @return application names list
     */
    public abstract List<String> getAllNames();

    /**
     * Count all instance group by service name
     *
     * @return count result
     */
    public abstract Map<String, Integer> summaryCounts();

}
