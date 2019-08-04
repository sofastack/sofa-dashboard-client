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
package com.alipay.sofa.dashboard.client.model.io;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class StoreRecord implements Serializable {

    private static final int serialVersionUID = 0x11;

    private String           schemeName;

    private long             timestamp;

    private String           value;

    public StoreRecord() {
    }

    private StoreRecord(Builder builder) {
        setSchemeName(builder.schemeName);
        setTimestamp(builder.timestamp);
        setValue(builder.value);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StoreRecord that = (StoreRecord) o;
        return getTimestamp() == that.getTimestamp()
               && Objects.equals(getSchemeName(), that.getSchemeName())
               && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSchemeName(), getTimestamp(), getValue());
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static final class Builder {
        private String schemeName;
        private long   timestamp;
        private String value;

        private Builder() {
        }

        public Builder schemeName(String schemeName) {
            this.schemeName = schemeName;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public StoreRecord build() {
            return new StoreRecord(this);
        }
    }
}
