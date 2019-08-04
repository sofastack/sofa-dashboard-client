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
import com.alipay.sofa.dashboard.client.model.thread.ThreadSummaryDescriptor;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;

import java.util.List;
import java.util.function.Consumer;

import static org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ActuatorThreadSummaryDimension implements
                                           ApplicationDimension<ThreadSummaryDescriptor> {

    private static final String   KEY_THREAD_LIVE   = "jvm.threads.live";

    private static final String   KEY_THREAD_DAEMON = "jvm.threads.daemon";

    private static final String   KEY_THREAD_PEAK   = "jvm.threads.peak";

    private final MetricsEndpoint endpoint;

    public ActuatorThreadSummaryDimension(MetricsEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getName() {
        return RecordName.THREAD_SUMMARY;
    }

    @Override
    public Class<ThreadSummaryDescriptor> getType() {
        return ThreadSummaryDescriptor.class;
    }

    @Override
    public ThreadSummaryDescriptor currentValue() {
        MetricResponse threadLive = endpoint.metric(KEY_THREAD_LIVE, null);
        MetricResponse threadDaemon = endpoint.metric(KEY_THREAD_DAEMON, null);
        MetricResponse threadPeak = endpoint.metric(KEY_THREAD_PEAK, null);

        ThreadSummaryDescriptor descriptor = new ThreadSummaryDescriptor();
        for (Object[] parallel : new Object[][] {
            { getThreadSize(threadLive),
              (Consumer<Integer>) (descriptor::setLive) },
            { getThreadSize(threadDaemon),
              (Consumer<Integer>) (descriptor::setDaemon) },
            { getThreadSize(threadPeak),
              (Consumer<Integer>) (descriptor::setPeak) }
        }) {
            int sizeAsMB = (int) parallel[0];
            //noinspection unchecked
            Consumer<Integer> setter = (Consumer<Integer>) parallel[1];
            setter.accept(sizeAsMB);
        }
        return descriptor;
    }

    /**
     * Read thread size from metric response
     *
     * @param response metric response
     * @return thread size
     */
    private int getThreadSize(MetricResponse response) {
        List<MetricsEndpoint.Sample> measurements = response.getMeasurements();
        if (measurements != null && !measurements.isEmpty()) {
            MetricsEndpoint.Sample defaultSample = measurements.get(0);
            return defaultSample.getValue().intValue();
        }
        return 0;
    }
}
