https://www.consul.io
https://github.com/hashicorp/consul
https://spring.io/projects/spring-cloud-consul
https://hub.docker.com/_/consul?tab=tags
https://github.com/consul/consul
https://github.com/gliderlabs/docker-consul
https://www.hashicorp.com/resources


提供命令行和API的方式


consul依赖go环境

consul安装
mac 用户，直接使用 brew 进行安装即可： brew install consul
或者下载压缩包，直接解压即可

安装部署
https://www.consul.io/docs/upgrading.html
https://learn.hashicorp.com/consul/getting-started/install

下载安装包
https://www.consul.io/downloads.html

解压后配置PATH之后
安装成功后，在命令行输入consul -v来检查是否安装成功

Consul 启动
./consul agent -dev           # -dev表示开发模式运行，另外还有-server表示服务模式运行


查看consul cluster中的每一个consul节点的信息
./consul members

Consul控制台
http://127.0.0.1:8500/ui/


Consul常用命令
命令	解释	示例
agent	运行一个consul agent	consul agent -dev
join	将agent加入到consul集群	consul join IP
members	列出consul cluster集群中的members	consul members
leave	将节点移除所在集群	consul leave


输入consul agent --help ，可以看到consul agent 的选项
比如
使用-client 参数可指定允许客户端使用什么ip去访问，例如-client 192.168.11.143 表示可以使用http://192.168.11.143:8500/ui 去访问。
我们尝试一下：
consul agent -dev -client 192.168.11.143
发现果然可以使用http://192.168.11.143:8500/ui 访问了。




Consul Cluster的高可用集群架构
两种部署方式：普通部署和Docker部署


Docker部署
拉取最新版本的Consul镜像：
docker pull consul

这里启动4个Consul Agent，3个Server（会选举出一个leader），1个Client。

#启动第1个Server节点，集群要求要有3个Server，将容器8500端口映射到主机8900端口，同时开启管理界面
docker run -d --name=consul1 -p 8900:8500 -e CONSUL_BIND_INTERFACE=eth0 consul agent --server=true --bootstrap-expect=3 --client=0.0.0.0 -ui
 
#启动第2个Server节点，并加入集群
docker run -d --name=consul2 -e CONSUL_BIND_INTERFACE=eth0 consul agent --server=true --client=0.0.0.0 --join 172.17.0.2
 
#启动第3个Server节点，并加入集群
docker run -d --name=consul3 -e CONSUL_BIND_INTERFACE=eth0 consul agent --server=true --client=0.0.0.0 --join 172.17.0.2
 
#启动第4个Client节点，并加入集群
docker run -d --name=consul4 -e CONSUL_BIND_INTERFACE=eth0 consul agent --server=false --client=0.0.0.0 --join 172.17.0.2

进入容器consul1：
docker exec -it consul1 /bin/sh
#执行ls后可以看到consul就在根目录
ls

请将下面的内容保存成文件services.json，并上传到容器的/consul/config目录中。
{
  "services": [
    {
      "id": "hello1",
      "name": "hello",
      "tags": [
        "primary"
      ],
      "address": "172.17.0.5",
      "port": 5000,
      "checks": [
        {
        "http": "http://localhost:5000/",
        "tls_skip_verify": false,
        "method": "Get",
        "interval": "10s",
        "timeout": "1s"
        }
      ]
    }
  ]
}
复制到consul config目录：
docker cp {这里请替换成services.json的本地路径} consul4:/consul/config
重新加载consul配置：

consul reload
然后这个服务就注册成功了。



搭建步骤：
启动node0机器上的Consul（node0机器上执行）：
consul agent -data-dir /tmp/node0 -node=node0 -bind=192.168.11.143 -datacenter=dc1 -ui -client=192.168.11.143 -server -bootstrap-expect 1

启动node1机器上的Consul（node1机器上执行）：
consul agent -data-dir /tmp/node1 -node=node1 -bind=192.168.11.144 -datacenter=dc1 -ui

启动node2机器上的Consul（node2机器上执行）：
consul agent -data-dir /tmp/node2 -node=node2 -bind=192.168.11.145 -datacenter=dc1 -ui -client=192.168.11.145

将node1节点加入到node0上（node1机器上执行）：
consul join 192.168.11.143

将node2节点加入到node0上（node2机器上执行）：
consul join -rpc-addr=192.168.11.145:8400  192.168.11.143

这样一个简单的Consul集群就搭建完成了，在node1上查看当前集群节点：
consul members -rpc-addr=192.168.11.143:8400
结果如下：
Node   Address              Status  Type    Build  Protocol  DC
node0  192.168.11.143:8301  alive   server  0.7.0  2         dc1
node1  192.168.11.144:8301  alive   client  0.7.0  2         dc1
node2  192.168.11.145:8301  alive   client  0.7.0  2         dc1

说明集群已经搭建成功了。

我们分析一下，为什么第5步和第6步需要加-rpc-addr 选项，而第4步不需要加任何选项呢？原因是-client 指定了客户端接口的绑定地址，包括：HTTP、DNS、RPC，而consul join 、consul members 都是通过RPC与Consul交互的。

如上，我们三个节点都加了-ui 参数启动了内建的界面。我们可以通过：http://192.168.11.143:8500/ui/ 或者http://192.168.11.145:8500/ui/进行访问，也可以在node1机器上通过http://127.0.0.1:8500/ui/ 进行访问，原因是node1没有开启远程访问 ，三种访问方式结果是一致的，如下：






Consul讲解：
可以有多个DataCenter，多个DataCenter通过Internet互联
单个DataCenter有Client和Server两种节点（所有的节点也被称为Agent）
Server节点有一个Leader和多个Follower
集群内数据的读写请求既可以直接发到Server，也可以通过Client使用RPC转发到Server，请求最终会到达Leader节点
在允许数据轻微陈旧的情况下，读请求也可以在普通的Server节点（Server Follower节点）完成
Consul Client可以认为是无状态的，它将注册信息通过RPC转发到Consul Server，服务信息保存在Server的各个节点中，并且通过Raft实现了强一致性。
服务注册到Consul可以通过HTTP API（8500端口）的方式，也可以通过Consul配置文件的方式（在Server节点直接使用命令让Server加载json配置文件）。


8300端口：集群内数据的读写和复制都是通过TCP的8300端口完成。
8301端口：单个数据中心的流言协议同时使用TCP和UDP通信，并且都使用8301端口
8302端口：跨数据中心的流言协议也同时使用TCP和UDP通信，端口使用8302。
8500端口：默认的Web控制台端口


首先Consul支持多数据中心，在上图中有两个DataCenter，他们通过Internet互联，同时请注意为了提高通信效率，只有Server节点才加入跨数据中心的通信。

在单个数据中心中，Consul分为Client和Server两种节点（所有的节点也被称为Agent）
Server节点保存数据
Client负责健康检查及转发数据请求到Server

Server节点有一个Leader和多个Follower，Leader节点会将数据同步到Follower，Server的数量推荐是3个或者5个，在Leader挂掉的时候会启动选举机制产生一个新的Leader。

集群内的Consul节点通过gossip协议（流言协议）维护成员关系，也就是说某个节点了解集群内现在还有哪些节点，这些节点是Client还是Server。单个数据中心的流言协议同时使用TCP和UDP通信，并且都使用8301端口。跨数据中心的流言协议也同时使用TCP和UDP通信，端口使用8302。

集群内数据的读写请求既可以直接发到Server，也可以通过Client使用RPC转发到Server，请求最终会到达Leader节点，在允许数据轻微陈旧的情况下，读请求也可以在普通的Server节点完成，集群内数据的读写和复制都是通过TCP的8300端口完成。




参考文档：
https://blog.csdn.net/u010046908/article/details/61916389
http://blog.didispace.com/consul-service-discovery-exp/
https://book-consul-guide.vnzmi.com/

Consul官方文档：https://www.consul.io/intro/getting-started/install.html
Consul 系列博文：http://www.cnblogs.com/java-zhao/archive/2016/04/13/5387105.html
使用consul实现分布式服务注册和发现：http://www.tuicool.com/articles/M3QFven

指导手册https://www.consul.io/docs/guides/index.html
安装部分参考自：https://www.consul.io/intro/getting-started/install.html
启动和停止服务部分参考自：https://www.consul.io/intro/getting-started/agent.html


查看
/quickstart-spring-cloud/doc/Consul安装部署.md
https://learn.hashicorp.com/consul/getting-started/install



安装参考
https://blog.csdn.net/u010694922/article/details/81703681
https://blog.csdn.net/liuxiaoxiaosmile/article/details/83017362
https://blog.csdn.net/liuzhuchen/article/details/81913562
https://www.cnblogs.com/newP/p/6349316.html


服务发现
https://www.cnblogs.com/newP/p/6349316.html
https://blog.csdn.net/shlazww/article/details/38736511
https://blog.csdn.net/mr_seaturtle_/article/details/77618403
https://www.cnblogs.com/ASPNET2008/p/6892137.html



