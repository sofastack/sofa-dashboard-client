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

import java.util.List;
import java.util.Map;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface AppSubscriber {

    /**
     * Startup registry.
     *
     * @return return {@code false} if it is already started
     */
    boolean start();

    /**
     * Shutdown registry.
     */
    void shutdown();

    /**
     * Get all application instances.
     *
     * @return application instance list
     */
    List<Application> getAll();

    /**
     * Get application instances by name.
     *
     * @param appName application service name
     * @return application instance list
     */
    List<Application> getByName(String appName);

    /**
     * Get all application names
     *
     * @return application names list
     */
    List<String> getAllNames();

    /**
     * Count all instance group by service name
     *
     * @return count result
     */
    Map<String, Integer> summaryCounts();

}
