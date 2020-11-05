[项目地址](https://github.com/youngzil/quickstart-register)



服务发现工具:  
- [Zookeeper](docs/zookeeper/Zookeeper学习.md)：ZooKeeper无疑是分布式协调应用的最佳选择，功能全，社区活跃，用户群体很大，对所有典型的用例都有很好的封装，支持不同语言的绑定。缺点是，整个应用比较重，依赖于Java，不支持跨数据中心。
- [Eureka](docs/Eureka.md)：Eureka是一项基于REST（代表性状态转移）的服务，主要在AWS云中用于查找服务，以实现负载均衡和中间层服务器的故障转移。
- [Nacos](docs/Nacos学习.md)：一个易于使用的动态服务发现，配置和服务管理平台，用于构建云本机应用程序。
- [Consul](docs/Consul.md)：Consul作为使用Go语言开发的分布式协调，对业务发现的管理提供很好的支持，他的HTTP API也能很好的和不同的语言绑定，并支持跨数据中心的应用。缺点是相对较新，适合喜欢尝试新事物的用户。
- [Etcd](docs/Etcd.md)：etcd是一个更轻量级的分布式协调的应用，提供了基本的功能，更适合一些轻量级的应用来使用。
- [Serf](https://www.serf.io/) : serf提供轻量级的cluster成员管理，故障检测（failure detection）和协调。开发基于GO语言。Consul使用了serf提供的功能，跟Consul是同一个公司HashiCorp的产品
- [Atomix介绍](#Atomix介绍) : 用于构建容错分布式系统的反应性Java框架 入门
- [SmartStack](#SmartStack：云中的服务发现)：云中的服务发现
- [Noah](https://github.com/lusis/Noah) : 基于ruby的ZooKeeper实现，十来年不活跃
- [Doozer](#Doozer介绍)：2011年开始就不更新了，基本废弃了。。。。 




目前，市面上有非常多的服务发现工具，[《Open-Source Service Discovery》](http://jasonwilder.com/blog/2014/02/04/service-discovery-in-the-cloud/) 一文中列举了如下开源的服务发现工具。  
Name	Type	AP or CP	Language	Dependencies	Integration  
Zookeeper	General	CP	Java	JVM	Client Binding  
Doozer	General	CP	Go	          Client Binding  
Etcd	General	Mixed (1)	Go	 Client Binding/HTTP  
SmartStack	Dedicated	AP	Ruby	haproxy/Zookeeper	Sidekick (nerve/synapse)  
Eureka	Dedicated	AP	Java	JVM	Java Client  
NSQ (lookupd)	Dedicated	AP	Go	 Client Binding  
Serf	Dedicated	AP	Go	 Local CLI  
Spotify (DNS)	Dedicated	AP	N/A	Bind	DNS Library  
SkyDNS	Dedicated	Mixed (2)	Go	 HTTP/DNS Library  

参考  
[服务发现框架选型，Consul还是Zookeeper还是etcd](https://blog.csdn.net/uxiAD7442KMy1X86DtM3/article/details/79847016)  
[使用Python进行分布式系统协调 (ZooKeeper/Consul/etcd)
](https://blog.csdn.net/younger_china/article/details/53063426)  
[SpringCloud服务注册中心比较:Consul vs Zookeeper vs Etcd vs Eureka](https://blog.csdn.net/u010963948/article/details/71730165)  






## Atomix介绍

[Atomix](https://atomix.io/) : 用于构建容错分布式系统的反应性Java框架 入门

A reactive Java framework for building fault-tolerant distributed systems





## SmartStack：云中的服务发现

SmartStack是一个自动化的服务发现和注册框架。
通过透明地处理组织内运行代码的计算机的创建，删除，故障和维护工作，它使工程师的工作变得更加轻松。
我们认为，我们针对此问题的方法是最好的方法：与任何同类方法相比，概念上更简单，更易于操作，更可配置并且提供更多的内省。
过去一年，SmartStack方式已经在Airbnb上进行了实战测试，并且在许多大小的组织中都具有广泛的适用性。

[SmartStack: Service Discovery in the Cloud](https://medium.com/airbnb-engineering/smartstack-service-discovery-in-the-cloud-4b8a080de619)




## Doozer介绍
[Doozer](https://github.com/ha/doozerd) ：2011年开始就不更新了，基本废弃了。。。。   

基于GO的高可靠，分布式的数据存储，过去两年已经不活跃  

A consistent distributed data store.

一致的分布式数据存储。


[Doozer Client Github地址](https://github.com/ha/doozer)  
Note: doozerd is the server. This is the Go client driver for doozer.  
Go client driver for doozerd, a consistent, distributed data store








openreplica http://openreplica.org/
基于Python开发的，面向对象的接口的分布式应用协调的工具




