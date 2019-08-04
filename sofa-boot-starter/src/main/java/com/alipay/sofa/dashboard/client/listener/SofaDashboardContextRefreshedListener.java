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

import com.alipay.sofa.dashboard.client.registry.AppPublisher;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * By listening to the ContextRefreshedEvent listener, after the application is fully started,
 * get the client's health check status, and then register
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/2/19 2:17 PM
 **/
@Component
public class SofaDashboardContextRefreshedListener implements
                                                  ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory
                                           .getLogger(SofaDashboardContextRefreshedListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        ReadinessCheckListener readinessCheckListener = context
            .getBean(ReadinessCheckListener.class);
        AppPublisher<?> publisher = context.getBean(AppPublisher.class);

        try {
            String status = readinessCheckListener.getHealthCheckerStatus()
                            && readinessCheckListener.getHealthCallbackStatus() ? Status.UP
                .toString() : Status.DOWN.toString();
            publisher.getApplication().setAppState(status);
            publisher.register();
        } catch (Exception e) {
            LOGGER.info("sofa dashboard client register failed.", e);
        }
    }
}
