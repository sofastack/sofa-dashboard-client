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
package com.alipay.sofa.dashboard.client.model.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/5/7 9:22 PM
 **/
public class LoggersDescriptor implements Serializable {

    private static final int              serialVersionUID = 0x11;

    private final List<String>            levels           = new ArrayList<>();

    private final Map<String, LoggerItem> loggers          = new HashMap<>();

    public List<String> getLevels() {
        return levels;
    }

    public Map<String, LoggerItem> getLoggers() {
        return loggers;
    }

    public static class LoggerItem implements Serializable {

        private static final int serialVersionUID = 0x11;

        private String           configuredLevel;

        private String           effectiveLevel;

        public String getConfiguredLevel() {
            return configuredLevel;
        }

        public void setConfiguredLevel(String configuredLevel) {
            this.configuredLevel = configuredLevel;
        }

        public String getEffectiveLevel() {
            return effectiveLevel;
        }

        public void setEffectiveLevel(String effectiveLevel) {
            this.effectiveLevel = effectiveLevel;
        }
    }
}
