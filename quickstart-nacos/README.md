[Nacos官网](https://nacos.io/)  
[Nacos Github](https://github.com/alibaba/nacos)  
[Nacos文档](https://nacos.io/zh-cn/docs/quick-start.html)  


an easy-to-use dynamic service discovery, configuration and service management platform for building cloud native applications.

一个易于使用的动态服务发现，配置和服务管理平台，用于构建云本机应用程序。

Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。

服务（Service）是 Nacos 世界的一等公民。Nacos 支持几乎所有主流类型的“服务”的发现、配置和管理



启动
sh startup.sh -m standalone


服务注册&发现和配置管理
服务注册
curl -X POST 'http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=nacos.naming.serviceName&ip=20.18.7.10&port=8080'

服务发现
curl -X GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=nacos.naming.serviceName'

发布配置
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test&content=HelloWorld"

获取配置
curl -X GET "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test"

关闭服务器
Linux/Unix/Mac
sh shutdown.sh



参考
https://blog.csdn.net/forezp/article/details/84724673


