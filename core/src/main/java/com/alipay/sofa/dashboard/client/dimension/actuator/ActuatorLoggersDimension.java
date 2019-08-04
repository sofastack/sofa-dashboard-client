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
package com.alipay.sofa.dashboard.client.dimension.actuator;

import com.alipay.sofa.dashboard.client.dimension.ApplicationDimension;
import com.alipay.sofa.dashboard.client.model.io.RecordName;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ActuatorLoggersDimension implements ApplicationDimension<LoggersDescriptor> {

    private final LoggersEndpoint endpoint;

    public ActuatorLoggersDimension(LoggersEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getName() {
        return RecordName.LOGGERS;
    }

    @Override
    public Class<LoggersDescriptor> getType() {
        return LoggersDescriptor.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LoggersDescriptor currentValue() {
        Map<String, Object> loggersInfo = endpoint.loggers();
        if (loggersInfo.isEmpty()) {
            return new LoggersDescriptor();
        }

        //
        // see:
        //  org.springframework.boot.actuate.logging.LoggersEndpoint#getLevels
        //  org.springframework.boot.actuate.logging.LoggersEndpoint#getLoggers
        //
        Set<LogLevel> levels = (Set<LogLevel>) loggersInfo.get("levels");
        Map<String, LoggersEndpoint.LoggerLevels> loggers =
            (Map<String, LoggersEndpoint.LoggerLevels>) loggersInfo.get("loggers");

        // Map to core definition
        List<String> levelsDesc = levels.stream().map(String::valueOf).collect(Collectors.toList());
        Map<String, LoggersDescriptor.LoggerItem> loggersDesc = loggers.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                LoggersDescriptor.LoggerItem item = new LoggersDescriptor.LoggerItem();
                item.setConfiguredLevel(entry.getValue().getConfiguredLevel());
                item.setEffectiveLevel(entry.getValue().getEffectiveLevel());
                return item;
            }));

        LoggersDescriptor descriptor = new LoggersDescriptor();
        descriptor.getLevels().addAll(levelsDesc);
        descriptor.getLoggers().putAll(loggersDesc);
        return descriptor;
    }
}
