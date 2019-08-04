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
package com.alipay.sofa.dashboard.client.model.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * Host and port definition
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class HostAndPort implements Comparable<HostAndPort>, Serializable {

    private static final int serialVersionUID = 0x11;

    private String           host;

    private int              port;

    public HostAndPort() {
    }

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Convert host & port to instance id
     *
     * @return instance id
     */
    public String toInstanceId() {
        return String.format("%s_%d", host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int compareTo(HostAndPort o) {
        int hostSign = Integer.compare(host.compareTo(o.host), 0) << 2;
        int portSign = Integer.compare(port, o.port);
        return hostSign + portSign;
    }

    @Override
    public String toString() {
        return "HostAndPort{" + "host='" + host + '\'' + ", port=" + port + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HostAndPort that = (HostAndPort) o;
        return getPort() == that.getPort() && Objects.equals(getHost(), that.getHost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort());
    }
}
