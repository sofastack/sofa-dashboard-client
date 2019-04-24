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
package com.alipay.sofa.dashboard.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 *
 * base on de.codecentric.boot.admin.client.config.SpringBootAdminClientEnabledCondition
 *
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/15 1:57 PM
 * @since:
 **/
public class SofaDashboardClientEnabledCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        SofaDashboardProperties sofaDashboardProperties = getSofaDashboardProperties(context);
        if (!sofaDashboardProperties.getClient().isEnable()) {
            return ConditionOutcome
                .noMatch("SOFA Dashboard Client is disabled, because 'com.alipay.sofa.dashboard.client.enabled' is false.");
        }
        return ConditionOutcome.match();
    }

    private SofaDashboardProperties getSofaDashboardProperties(ConditionContext context) {
        SofaDashboardProperties sofaDashboardProperties = new SofaDashboardProperties();
        Binder.get(context.getEnvironment()).bind("com.alipay.sofa.dashboard",
            Bindable.ofInstance(sofaDashboardProperties));
        return sofaDashboardProperties;
    }
}
