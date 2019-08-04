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
import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ActuatorEnvironmentDimension implements ApplicationDimension<EnvironmentDescriptor> {

    private EnvironmentEndpoint endpoint;

    public ActuatorEnvironmentDimension(EnvironmentEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getName() {
        return RecordName.ENVIRONMENT;
    }

    @Override
    public Class<EnvironmentDescriptor> getType() {
        return EnvironmentDescriptor.class;
    }

    @Override
    public EnvironmentDescriptor currentValue() {
        EnvironmentEndpoint.EnvironmentDescriptor result = endpoint.environment(null);
        //
        // Consider of dependency design, we do not use an actuator model directly.
        // We choose to define a serializable model in core module,
        // which can also be used by other extension modules(storage, for example)
        //
        String jsonString = JsonUtils.toJsonString(result);
        return JsonUtils.parseObject(jsonString, EnvironmentDescriptor.class);
    }

}
