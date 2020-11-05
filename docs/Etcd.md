[etcd官网](https://etcd.io/)  
[etcd Github](https://github.com/etcd-io/etcd)  
[etcd文档](https://etcd.io/docs/)  
[etcd文档 Github地址](https://etcd.io/docs/)  
[etcd demo学习](https://github.com/etcd-io/etcd/blob/master/Documentation/demo.md)  


A distributed, reliable key-value store for the most critical data of a distributed system

用于分布式系统最关键数据的分布式可靠键值存储



中文文档
http://etcd.doczh.cn/documentation/
https://doczhcn.gitbook.io/etcd/



提供命令行和API的方式

从etcd的架构图中我们可以看到，etcd主要分为四个部分。

HTTP Server： 用于处理用户发送的API请求以及其它etcd节点的同步与心跳信息请求。
Store：用于处理etcd支持的各类功能的事务，包括数据索引、节点状态变更、监控与反馈、事件处理与执行等等，是etcd对用户提供的大多数API功能的具体实现。
Raft：Raft强一致性算法的具体实现，是etcd的核心。
WAL：Write Ahead Log（预写式日志），是etcd的数据存储方式。除了在内存中存有所有数据的状态以及节点的索引以外，etcd就通过WAL进行持久化存储。WAL中，所有的数据提交前都会事先记录日志。Snapshot是为了防止数据过多而进行的状态快照；Entry表示存储的具体日志内容。


etcd概念词汇表
Raft：etcd所采用的保证分布式系统强一致性的算法。
Node：一个Raft状态机实例。
Member： 一个etcd实例。它管理着一个Node，并且可以为客户端请求提供服务。
Cluster：由多个Member构成可以协同工作的etcd集群。
Peer：对同一个etcd集群中另外一个Member的称呼。
Client： 向etcd集群发送HTTP请求的客户端。
WAL：预写式日志，etcd用于持久化存储的日志格式。
snapshot：etcd防止WAL文件过多而设置的快照，存储etcd数据状态。
Proxy：etcd的一种模式，为etcd集群提供反向代理服务。
Leader：Raft算法中通过竞选而产生的处理所有数据提交的节点。
Follower：竞选失败的节点作为Raft中的从属节点，为算法提供强一致性保证。
Candidate：当Follower超过一定时间接收不到Leader的心跳时转变为Candidate开始竞选。
Term：某个节点成为Leader到下一次竞选时间，称为一个Term。
Index：数据项编号。Raft中通过Term和Index来定位数据。


1. ETCD是什么
ETCD是用于共享配置和服务发现的分布式，一致性的KV存储系统。该项目目前最新稳定版本为2.3.0. 具体信息请参考[项目首页]和[Github]。ETCD是CoreOS公司发起的一个开源项目，授权协议为Apache。


部署 etcd 集群作为独立集群是直截了当的。仅用一个命令就可以启动它：
$ ./etcd
...
启动的 etcd 成员在 localhost:2379 监听客户端请求。



Docker部署
$ sudo docker run -p 4001:4001 -v /etc/ssl/certs/:/etc/ssl/certs/ quay.io/coreos/etcd:v2.0.0_rc.1

rm -rf /tmp/etcd-data.tmp && mkdir -p /tmp/etcd-data.tmp && \
  docker rmi gcr.io/etcd-development/etcd:v3.3.10 || true && \
  docker run \
  -p 2379:2379 \
  -p 2380:2380 \
  --mount type=bind,source=/tmp/etcd-data.tmp,destination=/etcd-data \
  --name etcd-gcr-v3.3.10 \
  gcr.io/etcd-development/etcd:v3.3.10 \
  /usr/local/bin/etcd \
  --name s1 \
  --data-dir /etcd-data \
  --listen-client-urls http://0.0.0.0:2379 \
  --advertise-client-urls http://0.0.0.0:2379 \
  --listen-peer-urls http://0.0.0.0:2380 \
  --initial-advertise-peer-urls http://0.0.0.0:2380 \
  --initial-cluster s1=http://0.0.0.0:2380 \
  --initial-cluster-token tkn \
  --initial-cluster-state new

docker exec etcd-gcr-v3.3.10 /bin/sh -c "/usr/local/bin/etcd --version"
docker exec etcd-gcr-v3.3.10 /bin/sh -c "ETCDCTL_API=3 /usr/local/bin/etcdctl version"
docker exec etcd-gcr-v3.3.10 /bin/sh -c "ETCDCTL_API=3 /usr/local/bin/etcdctl endpoint health"
docker exec etcd-gcr-v3.3.10 /bin/sh -c "ETCDCTL_API=3 /usr/local/bin/etcdctl put foo bar"
docker exec etcd-gcr-v3.3.10 /bin/sh -c "ETCDCTL_API=3 /usr/local/bin/etcdctl get foo"



通过使用 etcdctl 来和已经启动的集群交互：
# 使用 API 版本 3
$ export ETCDCTL_API=3
$ ./etcdctl put foo bar
OK
$ ./etcdctl get foo
bar



etcdctl 是一个和 etcd 服务器交互的命令行工具。
如果不用 etcdctl 修改成员，可以使用 v2 HTTP members API 或者 v3 gRPC members API.


写改、读、删、观察
./etcdctl put
./etcdctl get
./etcdctl del
./etcdctl watch

ETCDCTL_API=3 etcdctl put mykey "this is awesome" #设置
ETCDCTL_API=3 etcdctl get mykey #获取
ETCDCTL_API=3 etcdctl del mykey #删除
ETCDCTL_API=3 etcdctl get "" --from-key  #获取所有的键值
ETCDCTL_API=3 etcdctl get "" --from-key --keys-only #只获取所有的key不含value
ETCDCTL_API=3 etcdctl get "" --from-key --keys-only --limit 3 #获取key只显示3个
ETCDCTL_API=3 etcdctl get --prefix my #获取所有以my开头的key及对应value
ETCDCTL_API=3 etcdctl del my --prefix #删除所有以my开头的键值


这是观察多个键 foo 和 zoo 的命令:
$ etcdctl watch -i
$ watch foo
$ watch zoo
# 在另外一个终端: etcdctl put foo bar
PUT
foo
bar
# 在另外一个终端: etcdctl put zoo val
PUT
zoo
val


观察 key 的历史改动
# 从修订版本 2 开始观察键 `foo` 的改动
$ etcdctl watch --rev=2 foo


这里应该是观察变更时同时返回修改之前的值
# 在键 `foo` 上观察变更并返回被修改的值和上个修订版本的值
$ etcdctl watch --prev-kv foo
# 在另外一个终端: etcdctl put foo bar_latest
PUT
foo         # 键
bar_new     # 在修改前键foo的上一个值
foo         # 键
bar_latest  # 修改后键foo的值


压缩修订版本的命令：
$ etcdctl compact 5

# 在压缩修订版本之前的任何修订版本都不可访问
$ etcdctl get --rev=4 foo


应用可以为 etcd 集群里面的键授予租约。当键被附加到租约时，它的存活时间被绑定到租约的存活时间，而租约的存活时间相应的被 time-to-live (TTL)管理。在租约授予时每个租约的最小TTL值由应用指定。租约的实际 TTL 值是不低于最小 TTL，由 etcd 集群选择。一旦租约的 TTL 到期，租约就过期并且所有附带的键都将被删除。

租约有种像是为了方便管理KV而做的分组

授予租约、撤销租约、租约续约（维持租约）

# 授予租约，TTL为10秒
$ etcdctl lease grant 10
lease 32695410dcc0ca06 granted with TTL(10s)

# 附加键 foo 到租约32695410dcc0ca06
$ etcdctl put --lease=32695410dcc0ca06 foo bar
OK

撤销租约：应用通过租约 id 可以撤销租约。撤销租约将删除所有它附带的 key。

撤销同一个租约的命令：
$ etcdctl lease revoke 32695410dcc0ca06
lease 32695410dcc0ca06 revoked

$ etcdctl get foo
# 空应答，因为租约撤销导致foo被删除


维持租约：应用可以通过刷新键的 TTL 来维持租约，以便租约不过期。

这是维持同一个租约的命令：
$ etcdctl lease keep-alive 32695410dcc0ca06

获取租约信息：查询租约的信息和租约绑定了哪些KV
应用程序可能想知道租约信息，以便可以更新或检查租约是否仍然存在或已过期。应用程序也可能想知道有那些键附加到了特定租约。

这是获取租约信息的命令：
$ etcdctl lease timetolive 694d5765fc71500b

这是获取租约信息和租约附带的键的命令：
$ etcdctl lease timetolive --keys 694d5765fc71500b
# 如果租约已经过期或者不存在，它将给出下面的应答:
Error:  etcdserver: requested lease not found



集群搭建（3种方式）和 集群动态扩缩容

用于启动 etcd 集群的机制：
静态----提前预知所有的节点ip地址
etcd 发现
DNS 发现


运行时重配置的设计（集群动态扩缩容）：
两阶段配置修改保持集群安全：阶段 1 - 通知集群新配置，阶段 2 - 启动新成员


灾难恢复：集群恢复




参考
https://blog.csdn.net/aa1215018028/article/details/81116435 
https://www.cnblogs.com/softidea/p/6517959.html
https://blog.csdn.net/kikajack/article/details/80377526


部署
https://lihaoquan.me/2016/6/24/learning-etcd-1.html
https://www.jianshu.com/p/d63265949e52
