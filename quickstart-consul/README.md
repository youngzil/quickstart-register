[Consul API和SDK Client Libraries & SDKs](https://www.consul.io/api/libraries-and-sdks.html)  
[consul-api地址](https://www.consul.io/api-docs)  


Consul Client for Java：  
[consul-client](https://github.com/rickfast/consul-client)  
[consul-api](https://github.com/Ecwid/consul-api)  
spring-cloud-starter-consul-discovery封装的就是consul-api  



consul的主要接口是RESTful HTTP API，该API可以用来增删查改nodes、services、checks、configguration。所有的endpoints主要分为以下类别：
- kv - Key/Value存储
- agent - Agent控制
- catalog - 管理nodes和services
- health - 管理健康监测
- session - Session操作
- acl - ACL创建和管理
- event - 用户Events
- status - Consul系统状态

参考  
[服务发现系统consul-HTTP API](https://blog.csdn.net/u010246789/article/details/51871051)  
[使用consul实现服务的注册和发现](https://blog.csdn.net/u010246789/article/details/51777011)  
[Consul 主要端口和API](https://blog.csdn.net/liuxiaoxiaosmile/article/details/83017587)  
[Consul集群统一网关访问（网关单点）](https://blog.csdn.net/liuxiaoxiaosmile/article/details/83017430)  



利用Consul的KV存储来实现Leader Election  
[Application Leader Election with Sessions](https://www.consul.io/docs/guides/leader-election.html)


