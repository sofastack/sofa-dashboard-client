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
import com.alipay.sofa.dashboard.client.model.memory.MemoryDescriptor;
import com.alipay.sofa.dashboard.client.model.memory.MemoryHeapDescriptor;
import com.alipay.sofa.dashboard.client.model.memory.MemoryNonHeapDescriptor;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ActuatorMemoryDimension implements ApplicationDimension<MemoryDescriptor> {

    private static final String   KEY_MEMORY_COMMITTED  = "jvm.memory.committed";

    private static final String   KEY_MEMORY_USED       = "jvm.memory.used";

    private static final String   TAG_HEAP              = "area:heap";

    private static final String   TAG_NON_HEAP          = "area:nonheap";

    private static final String   TAG_METASPACE         = "id:Metaspace";

    private final MetricsEndpoint endpoint;

    private final List<String>    heapTags              = new ArrayList<>();

    private final List<String>    noneHeapTags          = new ArrayList<>();

    private final List<String>    noneHeapMetaSpaceTags = new ArrayList<>();

    public ActuatorMemoryDimension(MetricsEndpoint endpoint) {
        this.endpoint = endpoint;
        Collections.addAll(heapTags, TAG_HEAP);
        Collections.addAll(noneHeapTags, TAG_NON_HEAP);
        Collections.addAll(noneHeapMetaSpaceTags, TAG_NON_HEAP, TAG_METASPACE);
    }

    @Override
    public String getName() {
        return RecordName.MEMORY;
    }

    @Override
    public Class<MemoryDescriptor> getType() {
        return MemoryDescriptor.class;
    }

    @Override
    public MemoryDescriptor currentValue() {
        MetricResponse heapCommitted = endpoint.metric(KEY_MEMORY_COMMITTED, heapTags);
        MetricResponse heapUsed = endpoint.metric(KEY_MEMORY_USED, heapTags);
        MetricResponse nonHeapCommitted = endpoint.metric(KEY_MEMORY_COMMITTED, noneHeapTags);
        MetricResponse nonHeapUsed = endpoint.metric(KEY_MEMORY_USED, noneHeapTags);
        MetricResponse nonHeapMetaspace = endpoint.metric(KEY_MEMORY_USED, noneHeapMetaSpaceTags);

        MemoryDescriptor descriptor = new MemoryDescriptor();
        descriptor.setHeap(new MemoryHeapDescriptor());
        descriptor.setNonHeap(new MemoryNonHeapDescriptor());
        for (Object[] parallel : new Object[][] {
            { readSampleSizeAsMB(heapCommitted),
              (Consumer<Integer>) (descriptor.getHeap()::setSize) },
            { readSampleSizeAsMB(heapUsed),
              (Consumer<Integer>) (descriptor.getHeap()::setUsed) },
            { readSampleSizeAsMB(nonHeapCommitted),
              (Consumer<Integer>) (descriptor.getNonHeap()::setSize) },
            { readSampleSizeAsMB(nonHeapUsed),
              (Consumer<Integer>) (descriptor.getNonHeap()::setUsed) },
            { readSampleSizeAsMB(nonHeapMetaspace),
              (Consumer<Integer>) (descriptor.getNonHeap()::setMetaspace) }
        }) {
            int sizeAsMB = (int) parallel[0];
            //noinspection unchecked
            Consumer<Integer> setter = (Consumer<Integer>) parallel[1];
            setter.accept(sizeAsMB);
        }
        return descriptor;
    }

    /**
     * Read metrics sample size as MB
     *
     * @param response metric response from actuator
     * @return size as MB
     */
    @NonNull
    private int readSampleSizeAsMB(MetricResponse response) {
        List<MetricsEndpoint.Sample> measurements = response.getMeasurements();
        if (measurements != null && !measurements.isEmpty()) {
            MetricsEndpoint.Sample defaultSample = measurements.get(0);
            return convertBitToMB(defaultSample.getValue());
        }
        return 0;
    }

    /**
     * Convert bit size to MB
     *
     * @param size origin size
     * @return size as MB
     */
    private int convertBitToMB(@Nullable Number size) {
        if (size == null) {
            return 0;
        }
        return Math.round(size.floatValue() / (Byte.SIZE * 1024 * 1024));
    }
}
