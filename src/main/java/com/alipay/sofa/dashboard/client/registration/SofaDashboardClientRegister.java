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
package com.alipay.sofa.dashboard.client.registration;

import com.alipay.sofa.dashboard.client.common.Constants;
import com.alipay.sofa.dashboard.client.common.NetworkAddressUtils;
import com.alipay.sofa.dashboard.client.common.ObjectBytesUtils;
import com.alipay.sofa.dashboard.client.config.SofaDashboardProperties;
import com.alipay.sofa.dashboard.client.model.Application;
import com.alipay.sofa.dashboard.client.zookeeper.ZkCommandClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/18 10:18 AM
 * @since:
 **/
public class SofaDashboardClientRegister {

    private static final Logger           LOGGER       = LoggerFactory
                                                           .getLogger(SofaDashboardClientRegister.class);

    private final AtomicReference<String> registeredId = new AtomicReference<>();

    private ZkCommandClient               zkCommandClient;

    private SofaDashboardProperties       sofaDashboardProperties;

    private final String                  appName;

    private final int                     port;

    public SofaDashboardClientRegister(SofaDashboardProperties sofaClientProperties,
                                       ZkCommandClient zkCommandClient, Environment environment) {
        this.sofaDashboardProperties = sofaClientProperties;
        this.zkCommandClient = zkCommandClient;
        this.appName = environment.getProperty(Constants.APP_NAME_KEY);
        String port = environment.getProperty(Constants.SERVER_PORT_KEY, Constants.SERVER_DEFAULT_PORT);
        try {
            this.port = Integer.parseInt(port);
        } catch (Exception e) {
            LOGGER.error("server port parse error, port:{}", port);
            throw new RuntimeException("server port parse error");
        }
    }

    public boolean register(String status) {
        boolean isRegistrationSuccessful = false;
        Application self = createApplication(status);
        Stat stat;
        try {
            stat = zkCommandClient.getCuratorClient().checkExists()
                .forPath(Constants.SOFA_BOOT_CLIENT_ROOT);
            if (stat == null) {
                zkCommandClient.getCuratorClient().create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(Constants.SOFA_BOOT_CLIENT_ROOT);
            }
            byte[] bytes = ObjectBytesUtils.convertFromObject(self);
            String path = Constants.SOFA_BOOT_CLIENT_ROOT + Constants.SOFA_BOOT_CLIENT_INSTANCE;
            path = path + "/" + appName + "/" + getRegisteredId();
            zkCommandClient.getCuratorClient().create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(path, bytes);
            isRegistrationSuccessful = true;
        } catch (Exception e) {
            LOGGER.error("Failed to register application to zookeeper.", e);
        }
        return isRegistrationSuccessful;
    }

    public void deregister() {
        String id = registeredId.get();
        if (id != null) {
            String path = Constants.SOFA_BOOT_CLIENT_ROOT + Constants.SOFA_BOOT_CLIENT_INSTANCE;
            path = path + "/" + getRegisteredId();
            try {
                zkCommandClient.getCuratorClient().delete().forPath(path);
            } catch (Exception e) {
                LOGGER.error("Failed to deregister application to zookeeper.", e);
            }
        }
    }

    /**
     * Returns the id of this client as given by the admin server.
     * Returns null if the client has not registered against the admin server yet.
     *
     * @return
     */
    public String getRegisteredId() {
        return getLocalIp(sofaDashboardProperties) + ":" + port;
    }

    protected Application createApplication(String status) {
        Application application = new Application();
        application.setAppName(appName);
        application.setHostName(getLocalIp(sofaDashboardProperties));
        application.setPort(port);
        application.setAppState(status);
        return application;
    }

    private static String getLocalIp(SofaDashboardProperties sofaDashboardProperties) {
        String ip;
        NetworkAddressUtils.calculate(null, null);
        if (StringUtils.isEmpty(sofaDashboardProperties.getClient().getInstanceIp())) {
            ip = NetworkAddressUtils.getLocalIP();
        } else {
            ip = sofaDashboardProperties.getClient().getInstanceIp();
        }
        return ip;
    }
}