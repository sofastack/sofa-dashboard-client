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

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Zookeeper client
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ZookeeperClient implements Closeable {

    private final AtomicBoolean                 start    = new AtomicBoolean(false);

    private final AtomicBoolean                 shutdown = new AtomicBoolean(false);

    private final CuratorFramework              curatorClient;

    private final Map<String, LifecycleHandler> handlers = new ConcurrentSkipListMap<>();

    public ZookeeperClient(ZookeeperConfig config) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(config.getBaseSleepTimeMs(),
            config.getMaxRetries());
        // to build curatorClient
        curatorClient = CuratorFrameworkFactory.builder().connectString(config.getAddress())
            .sessionTimeoutMs(config.getSessionTimeoutMs())
            .connectionTimeoutMs(config.getConnectionTimeoutMs()).retryPolicy(retryPolicy).build();
    }

    public boolean isRunning() {
        return start.get() && !shutdown.get();
    }

    /**
     * Get Curator client
     *
     * @return client instance
     */
    public CuratorFramework getCuratorClient() {
        return curatorClient;
    }

    /**
     * 添加一个生命周期绑定
     *
     * @param handler lifecycle handler
     */
    public void addLifecycleHandler(LifecycleHandler handler) {
        this.handlers.put(handler.getName(), handler);
    }

    /**
     * Startup curator client
     *
     * @return return {@code true} if client is not started.
     */
    public boolean start() {
        if (start.compareAndSet(false, true)) {
            handlers.forEach((k, v) -> v.beforeStart(curatorClient));
            curatorClient.start();
            handlers.forEach((k, v) -> v.afterStarted(curatorClient));
            return true;
        }
        // Since registry is already started
        return false;
    }

    /**
     * Shutdown curator client
     */
    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            handlers.forEach((k, v) -> v.beforeShutdown(curatorClient));
            curatorClient.close();
        }
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }
}
