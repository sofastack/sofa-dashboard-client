## SOFADashboard client

[![Build Status](https://travis-ci.org/sofastack/sofa-dashboard-client.svg?branch=master)](https://travis-ci.org/sofastack/sofa-dashboard-client)
[![Coverage Status](https://coveralls.io/repos/github/sofastack/sofa-dashboard-client/badge.svg?branch=master)](https://coveralls.io/github/sofastack/sofa-dashboard-client?branch=master)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Maven](https://img.shields.io/github/release/sofastack/sofa-dashboard-client.svg)](https://github.com/sofastack/sofa-dashboard-client/releases)

SOFADashboard client 用于向 SOFADashboard 服务端注册 IP、端口、健康检查状态等应用基本信息。

## 功能简介

SOFADashboard client 并非是直接通过 API 调用的方式将自身应用信息直接注册到 SOFADashboard 服务端 ，而是借助于 Zookeeper 来完成。

![image.png](https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*n7ntQ7-iWMkAAAAAAAAAAABjARQnAQ)

客户端向 Zookeeper 中如上图所示的节点中写入数据，每一个 ip:port 节点代表一个应用实例，应用本身信息将写入当前节点的 data 中。

## 快速开始

* 通过 [SOFABoot 快速开始](https://www.sofastack.tech/sofa-boot/docs/QuickStart) 新建一个 SOFABoot 工程，然后引入 zookeeper 客户端依赖 和 sofa-dashboard -client 依赖

```xml
<dependency>
  <groupId>com.alipay.sofa</groupId>
  <artifactId>sofa-dashboard-client</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

* 配置
```properties
# 配置应用名
spring.application.name=samples-app
# 端口
server.port=8081
# 指定 zk 地址
com.alipay.sofa.admin.zookeeper.address=localhost:2181
```
* 在 SOFADashboard 管控端查看应用注册信息

> 参考 [SOFADashboard Server 快速开始](https://www.sofastack.tech/sofa-dashboard/docs/QuickStart) 部署 SOFADashboard 服务端

![client-dashboard](https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*fEkBTJtcMzsAAAAAAAAAAABjARQnAQ)


## 示例

SOFADashboard client 的编译环境的要求为 JDK8，需要采用 Apache Maven 3.2.5 或者更高的版本进行编译。

## 文档

请参考 [SOFADashboard 官方文档](https://www.sofastack.tech/sofa-dashboard/docs/Home)
