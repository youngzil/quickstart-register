原项目地址
https://gitee.com/youngzil/quickstart-all



Zookeeper
Eureka

Consul
Etcd

Doozer
Nacos的代码是托管在github上，https://github.com/alibaba/nacos

doozer：2011年开始就不更新了，基本废弃了。。。。
https://github.com/ha/doozerd
https://github.com/ha/doozer


目前，市面上有非常多的服务发现工具，《Open-Source Service Discovery》（http://jasonwilder.com/blog/2014/02/04/service-discovery-in-the-cloud/）一文中列举了如下开源的服务发现工具。
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
https://blog.csdn.net/uxiAD7442KMy1X86DtM3/article/details/79847016



ZooKeeper无疑是分布式协调应用的最佳选择，功能全，社区活跃，用户群体很大，对所有典型的用例都有很好的封装，支持不同语言的绑定。缺点是，整个应用比较重，依赖于Java，不支持跨数据中心。

Consul作为使用Go语言开发的分布式协调，对业务发现的管理提供很好的支持，他的HTTP API也能很好的和不同的语言绑定，并支持跨数据中心的应用。缺点是相对较新，适合喜欢尝试新事物的用户。

etcd是一个更轻量级的分布式协调的应用，提供了基本的功能，更适合一些轻量级的应用来使用。



eureka https://github.com/Netflix/eureka
Netflix开发的定位服务，应用于fail over和load balance的功能


curator http://curator.apache.org/
基于ZooKeeper的更高层次的封装

doozerd https://github.com/ha/doozerd
基于GO的高可靠，分布式的数据存储，过去两年已经不活跃

openreplica http://openreplica.org/
基于Python开发的，面向对象的接口的分布式应用协调的工具

serf http://www.serfdom.io/
serf提供轻量级的cluster成员管理，故障检测（failure detection）和协调。开发基于GO语言。Consul使用了serf提供的功能

noah https://github.com/lusis/Noah
基于ruby的ZooKeeper实现，过去三年不活跃

copy cat https://github.com/kuujo/copycat
基于日志的分布式协调的框架，使用Java开发



参考
https://blog.csdn.net/younger_china/article/details/53063426




