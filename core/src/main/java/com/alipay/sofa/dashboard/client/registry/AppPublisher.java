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

/**
 * Application instance registry.
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public abstract class AppPublisher<CFG extends RegistryConfig> {

    private final Application application;

    private final CFG         config;

    protected AppPublisher(Application application, CFG config) {
        this.application = application;
        this.config = config;
    }

    public Application getApplication() {
        return application;
    }

    public CFG getConfig() {
        return config;
    }

    /**
     * Startup registry.
     *
     * @return {@code false}, if it is already started
     */
    public abstract boolean start() throws Exception;

    /**
     * Shutdown registry.
     */
    public abstract void shutdown() throws Exception;

    /**
     * Publish instance onto registry central.
     * It should be called after started
     */
    public abstract void register() throws Exception;

    /**
     * Remove instance from registry central.
     */
    public abstract void unRegister() throws Exception;

}
