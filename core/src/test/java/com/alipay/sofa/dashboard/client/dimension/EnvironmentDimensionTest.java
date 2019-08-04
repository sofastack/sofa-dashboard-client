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
package com.alipay.sofa.dashboard.client.dimension;

import com.alipay.sofa.dashboard.client.context.DimensionTestContext;
import com.alipay.sofa.dashboard.client.dimension.actuator.ActuatorEnvironmentDimension;
import com.alipay.sofa.dashboard.client.model.io.RecordName;
import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DimensionTestContext.class)
public class EnvironmentDimensionTest {

    private static final Logger          LOGGER = LoggerFactory
                                                    .getLogger(EnvironmentDimensionTest.class);

    @Autowired
    private ActuatorEnvironmentDimension dimension;

    @Test
    public void basicInvokeTest() {
        Assert.assertEquals(dimension.getName(), RecordName.ENVIRONMENT);
        Assert.assertEquals(dimension.getType(), EnvironmentDescriptor.class);

        EnvironmentDescriptor descriptor = dimension.currentValue();
        Assert.assertNotNull(dimension.currentValue());
        LOGGER.info("Fetch environment => {}", JsonUtils.toJsonString(descriptor));
    }

}
