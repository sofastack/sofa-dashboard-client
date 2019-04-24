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
package com.alipay.sofa.dashboard.client.listener;

import com.alipay.sofa.dashboard.client.registration.SofaDashboardClientRegister;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 *
 * By listening to the ApplicationReadyEvent listener, after the application is fully started,
 * get the client's health check status, and then register
 *
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/19 2:17 PM
 * @since:
 **/
public class ApplicationContextRefreshedListener implements
                                                ApplicationListener<ContextRefreshedEvent> {

    private static final Logger         LOGGER = LoggerFactory
                                                   .getLogger(ApplicationContextRefreshedListener.class);

    @Autowired
    private ReadinessCheckListener      readinessCheckListener;

    @Autowired
    private SofaDashboardClientRegister sofaDashboardClientRegister;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Start to register sofa dashboard client.");
        String status = readinessCheckListener.getHealthCheckerStatus()
                        && readinessCheckListener.getHealthCallbackStatus() ? Status.UP.toString()
            : Status.DOWN.toString();
        boolean isRegister = sofaDashboardClientRegister.register(status);
        if (isRegister) {
            LOGGER.info("sofa dashboard client register success.");
        }
    }
}
