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
package com.alipay.sofa.dashboard.client.listener;

import com.alipay.sofa.dashboard.client.registration.SofaDashboardClientRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 监听 ContextClosedEvent 事件，执行应用信息注销操作
 * 如果异常停机导致未执行该方法，后面是通过 zk 自身的心跳机制也会移除
 *
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/4/11 6:53 PM
 * @since:
 **/
public class SofaDashboardClientApplicationContextClosedListener
                                                                implements
                                                                ApplicationListener<ContextClosedEvent> {

    @Autowired
    private SofaDashboardClientRegister sofaDashboardClientRegister;

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        sofaDashboardClientRegister.deregister();
    }
}
