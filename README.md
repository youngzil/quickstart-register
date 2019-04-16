原项目地址
https://gitee.com/youngzil/quickstart-all



Zookeeper
Eureka

Consul
Etcd

Doozer
Nacos的代码是托管在github上，https://github.com/alibaba/nacos


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



