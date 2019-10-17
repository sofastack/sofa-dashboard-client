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
package com.alipay.sofa.dashboard.client.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * A lifecycle handler for zookeeper registry client
 *
 * @author chpengzh@didiglobal.com
 * @date 2019-08-28 22:56
 */
public interface LifecycleHandler {

    /**
     * Lifecycle handler name
     *
     * @return unique name
     */
    String getName();

    /**
     * Before {@link CuratorFramework#start()}
     * Maybe attach some {@link TreeCacheListener} or others.
     *
     * @param client client instance
     */
    default void beforeStart(CuratorFramework client) {

    }

    /**
     * Just after {@link CuratorFramework#start()}
     * Maybe create some session node or others.
     *
     * @param client client instance
     */
    default void afterStarted(CuratorFramework client) {

    }

    /**
     * Before {@link CuratorFramework#close()}
     *
     * @param client client instance
     */
    default void beforeShutdown(CuratorFramework client) {

    }

}
