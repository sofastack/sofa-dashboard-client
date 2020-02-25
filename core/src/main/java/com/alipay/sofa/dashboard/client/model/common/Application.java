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

import org.springframework.util.StringUtils;

/**
 * Application instance model definition
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 19/1/19 上午11:48
 **/
public class Application implements Serializable, Comparable<Application> {

    private static final int serialVersionUID = 0x11;

    /**
     * Application name
     */
    private String           appName;

    /**
     * Binding host name
     */
    private String           hostName;

    /**
     * 内部IP
     * 
     */
    private String           internalHost;

    /**
     * Binding port
     */
    private int              port;

    /**
     * Running state
     */
    private String           appState;

    /**
     * Startup timestamp(ms)
     */
    private long             startTime;

    /**
     * Recover timestamp(ms)
     */
    private long             lastRecover;

    public Application() {
    }

    private Application(Builder builder) {
        setAppName(builder.appName);
        setHostName(builder.hostName);
        setInternalHost(builder.internalHost);
        setPort(builder.port);
        setAppState(builder.appState);
        setStartTime(builder.startTime);
        setLastRecover(builder.lastRecover);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Application{" + "appName='" + appName + '\'' + ", hostName='" + hostName + '\''
               + ", port=" + port + ", appState='" + appState + '\'' + ", startTime=" + startTime
               + ", lastRecover=" + lastRecover + "internalHost" + internalHost + '}';
    }

    public String getInternalHost() {
        return internalHost;
    }

    public void setInternalHost(String internalHost) {
        this.internalHost = internalHost;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastRecover() {
        return lastRecover;
    }

    public void setLastRecover(long lastRecover) {
        this.lastRecover = lastRecover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Application that = (Application) o;
        return getPort() == that.getPort() && Objects.equals(getAppName(), that.getAppName())
               && Objects.equals(getHostName(), that.getHostName())
               && Objects.equals(getInternalHost(), that.getInternalHost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAppName(), getHostName(), getInternalHost(), getPort());
    }

    @Override
    public int compareTo(Application o) {
        int nameSign = Integer.compare(appName.compareTo(o.appName), 0) << 3;
        int hostSign = Integer.compare(hostName.compareTo(o.hostName), 0) << 2;
        int portSign = Integer.compare(port, o.port);
        boolean isTargetInternalHostNull = StringUtils.isEmpty(o.internalHost);
        boolean isInternalHostNull = StringUtils.isEmpty(internalHost);
        if (isInternalHostNull && isTargetInternalHostNull) {
            return nameSign + hostSign + portSign;
        } else if (isTargetInternalHostNull == false && isInternalHostNull == false) {
            int internalSign = Integer.compare(internalHost.compareTo(o.internalHost), 0) << 1;
            return nameSign + hostSign + internalSign + portSign;
        } else {
            return 1;
        }
    }

    public static final class Builder {

        private String appName;
        private String hostName;
        private String internalHost;
        private int    port;
        private String appState;
        private long   startTime;
        private long   lastRecover;

        private Builder() {
        }

        public Builder internalHost(String internalHost) {
            this.internalHost = internalHost;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder appState(String appState) {
            this.appState = appState;
            return this;
        }

        public Builder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder lastRecover(long lastRecover) {
            this.lastRecover = lastRecover;
            return this;
        }

        public Application build() {
            return new Application(this);
        }
    }
}
