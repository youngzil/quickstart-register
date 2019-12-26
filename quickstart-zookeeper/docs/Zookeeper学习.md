Zookeeper的三种角色和三种状态     
Zookeeper的节点数据结构 和 节点类型     
Zookeeper的watch机制   
  
  
ZooKeeper 是一个典型的分布式数据一致性解决方案，分布式应用程序可以基于 ZooKeeper 实现诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能。 
zookeeper作用：    
1、Zookeeper 一个最常用的使用场景就是用于担任服务生产者和服务消费者的注册中心(提供发布订阅服务)  
2、ActieMQ作为Master选举的功能      
3、Kafka是作为  
4、  
5、  
6、  
7、  

为什么最好使用奇数台服务器构成 ZooKeeper 集群？   
所谓的zookeeper容错是指，当宕掉几个zookeeper服务器之后，剩下的个数必须大于宕掉的个数的话整个zookeeper才依然可用。  
假如我们的集群中有n台zookeeper服务器，那么也就是剩下的服务数必须大于n/2。 
先说一下结论，2n和2n-1的容忍度是一样的，都是n-1    
所以何必增加那一个不必要的zookeeper呢？    


图解 Paxos 一致性协议  
Zookeeper ZAB 协议分析  



参考  
https://www.cnblogs.com/lanqiu5ge/p/9405601.html
https://github.com/Snailclimb/JavaGuide/blob/master/docs/system-design/framework/ZooKeeper.md   
https://hadyang.gitbook.io/interview/architecture/distributed/6-zk  
https://www.cnblogs.com/qingyunzong/p/8618965.html  
https://www.qingtingip.com/h_277671.html    


---------------------------------------------------------------------------------------------------------------------
ZooKeeper 是一个典型的分布式数据一致性解决方案，分布式应用程序可以基于 ZooKeeper 实现诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能。  




---------------------------------------------------------------------------------------------------------------------  
Zookeeper的三种角色和三种状态  
  
三种角色：leader、follower、observer  
领导者（leader），负责进行投票的发起和决议，更新系统状态  
学习者（learner），包括跟随者（follower）和观察者（observer），follower用于接受客户端请求并想客户端返回结果，在选主过程中参与投票  
Observer可以接受客户端连接，将写请求转发给leader，但observer不参加投票过程，只同步leader的状态，observer的目的是为了扩展系统，提高读取速度  
客户端（client），请求发起方  
  
  
  
每个Server在工作过程中有三种状态：LOOKING、LEADING、FOLLOWING  
　　　　LOOKING：当前Server不知道leader是谁，正在搜寻  
　　　　LEADING：当前Server即为选举出来的leader  
　　　　FOLLOWING：leader已经选举出来，当前Server与之同步  
  
  
  
zookeeper中的每个节点称为一个znode，每个znode维持一个数据结构，其内容如下：  
1、Version number − 版本号，当和该znode节点关联的数据发生变化时，版本号会自增1。  
2、Action Control List (ACL) − 访问控制列表，znode的访问控制机制，它控制znode的所有读写操作。  
3、Timestamp − znode创建时的时间戳，精确到毫秒。  
4、Data length − znode节点存储数据的长度，最大1MB。  
  
  
  
读取操作：当Client向zookeeper发出读请求时，无论是Leader还是Follower，都直接返回查询结果。  
写操作：  
1、① 写入请求直接发送到leader节点：由Leader节点广播并返回结果给Client  
2、② 写入请求发送到Follower节点：由Follower节点转发给Leader节点，处理后Leader返回结果给Follower，原来的Follower返回写入成功消息给Client；  
  
  
  
znode有三种基本类型和两种组合类型：  
1、Persistence znode 持久znode  
2、Ephemeral znode − 临时znode  
3、Sequential znode − 序列znode  
  
组合：  
1、Persistence+Sequential znode，组合出的节点类型。  
2、Ephemeral+Sequential znode，组合出的节点类型。  
  
  
  
ZooKeeper 的 Watcher 机制主要包括客户端线程、客户端 WatchManager 和 ZooKeeper 服务器三部分  
1. 注册只能确保一次消费  
2. 客户端串行执行  
3. 轻量级设计：WatchedEvent 是 ZooKeeper 整个 Watcher 通知机制的最小通知单元，这个数据结构中只包含三部分的内容：通知状态、事件类型和节点路径。  
  
  
  
zookeeper的应用场景  
1、zookeeper实现配置中心：应用监听对应路径下的配置即可  
2、服务注册中心（服务发现）：服务提供者Provider向Zookeeper注册服务；服务消费者Consumer从zookeeper中查询服务和监听服务的变化，缓存服务信息到本地；调用远端的服务；  
3、zookeeper集群选主：使用zookeeper的ephemeral+sequence类型的znode可以实现集群的选主功能，跟分布式锁类似  
4、zookeeper实现分布式锁：创建一个ephemeral+sequence类型的znode，判断自己创建的znode是否序列最小，若是，则获的锁；若不是，在距离自己最近的前一个znode上设置一个watch，当获取到znode变更通知后  
5、zookeeper集群系统管理：  
  
  
  
  
选举原理：事务id号（zxid）：高32位的epoch +    低32位用于递增计数  
ZooKeeper 的非全新集群选主  
1、逻辑时钟小的选举结果被忽略，重新投票  
2、统一逻辑时钟后，数据 version 大的胜出  
3、数据 version 相同的情况下，server id 大的胜出  
  
  
  
  
  
Zookeeper学习参考  
https://www.jianshu.com/p/b48d50e1fcb1  
https://www.cnblogs.com/qingyunzong/p/8632995.html  
https://www.cnblogs.com/raphael5200/p/5285583.html  
https://blog.csdn.net/qiangcuo6087/article/details/79042035  
https://blog.csdn.net/wzk646795873/article/details/79706627  
  
  
  
  
  
Zookeeper动态扩容  
https://blog.csdn.net/levy_cui/article/details/70859355  
https://cloud.tencent.com/developer/article/1119410  
  
1、集群本来是单机模式，需要将它扩容成集群模式  
2、集群本来就有>2台机器在运行，只是将它扩容成更多的机器  
  
总的来说都是先部署新机器，再修改老机器配置文件重启。单机扩容集群短暂的停止服务，集群扩容集群是用户无感知的  
  
  
  
为了保证事务的顺序一致性，zookeeper采用了递增的事务id号（zxid）来标识事务。所有的提议（proposal）都在被提出的时候加上了zxid。实现中zxid是一个64位的数字，它高32位是epoch用来标识leader关系是否改变，每次一个leader被选出来，它都会有一个新的epoch，标识当前属于那个leader的统治时期。低32位用于递增计数。  
  
  
  
  
Jute是Zookeeper底层序列化组件，其用于Zookeeper进行网络数据传输和本地磁盘数据存储的序列化和反序列化工作。  
https://blog.csdn.net/lw_ghy/article/details/56301286  
https://my.oschina.net/u/2277632/blog/1540809  
  
  
读、写(更新)模式  
在ZooKeeper集群中，读可以从任意一个ZooKeeper Server读，这一点是保证ZooKeeper比较好的读性能的关键；写的请求会先Forwarder到Leader，然后由Leader来通过ZooKeeper中的原子广播协议，将请求广播给所有的Follower，Leader收到一半以上的写成功的Ack后，就认为该写成功了，就会将该写进行持久化，并告诉客户端写成功了。  
  
  
https://blog.csdn.net/xinguan1267/article/details/38422149  
  
  
Zookeeper  
http://jm.taobao.org/2013/10/07/zookeeper-faq/  
https://blog.csdn.net/u010185262/article/details/49910301  
  
1. 客户端对ServerList的轮询机制是什么？随机  
2.客户端如何正确处理CONNECTIONLOSS(连接断开) 和 SESSIONEXPIRED(Session 过期)两类连接异常：  
CONNECTIONLOSS(连接断开)：重连即可，Session还是存在的  
SESSIONEXPIRED(Session 过期)：要重新实例zookeeper对象，重新操作所有临时数据（包括临时节点和注册Watcher）。  
服务器认为这个session已经结束了（服务器无法确认是因为其它异常原因还是客户端主动结束会话），开始清除和这个会话有关的信息，包括这个会话创建的临时节点和注册的Watcher。  
  
  
3. 不同的客户端对同一个节点是否能获取相同的数据  
4、一个客户端修改了某个节点的数据，其它客户端能够马上获取到这个最新数据吗  
ZooKeeper不能确保任何客户端能够获取（即Read Request）到一样的数据，这个是实际存在的现象，当然延时很短。除非客户端自己要求，解决的方法是客户端B先调用 sync(), 再调用 getData().  
  
  
5. ZK为什么不提供一个永久性的Watcher注册机制  
不支持用持久Watcher的原因很简单，ZK无法保证性能。  
  
6. 使用watch需要注意的几点  
a. Watches通知是一次性的，必须重复注册.  
b. 发生CONNECTIONLOSS之后，只要在session_timeout之内再次连接上（即不发生SESSIONEXPIRED），那么这个连接注册的watches依然在。  
c. 节点数据的版本变化会触发NodeDataChanged，注意，这里特意说明了是版本变化。存在这样的情况，只要成功执行了setData()方法，无论内容是否和之前一致，都会触发NodeDataChanged。  
d. 对某个节点注册了watch，但是节点被删除了，那么注册在这个节点上的watches都会被移除。  
e. 同一个zk客户端对某一个节点注册相同的watch，只会收到一次通知。  
f. Watcher对象只会保存在客户端，不会传递到服务端。  
  
  
7.我能否收到每次节点变化的通知  
不能，一般如果节点数据的更新频率很高的话，是没有问题的  
原因在于：当一次数据修改，通知客户端，客户端再次注册watch，在这个过程中，可能数据已经发生了许多次数据修改  
  
8.能为临时节点创建子节点吗？不能。  
  
9. 是否可以拒绝单个IP对ZK的访问,操作  
ZK本身不提供这样的功能，它仅仅提供了对单个IP的连接数的限制。你可以通过修改iptables来实现对单个ip的限制，当然，你也可以通过这样的方式来解决。https://issues.apache.org/jira/browse/ZOOKEEPER-1320  
  
10. 在getChildren(String path, boolean watch)是注册了对节点子节点的变化，那么子节点的子节点变化能通知吗？不能  
  
11.创建的临时节点什么时候会被删除，是连接一断就删除吗？延时是多少？  
连接断了之后，ZK不会马上移除临时数据，只有当SESSIONEXPIRED之后，才会把这个会话建立的临时数据移除。因此，用户需要谨慎设置Session_TimeOut  
  
  
12. zookeeper是否支持动态进行机器扩容？如果目前不支持，那么要如何扩容呢？  
已经支持了，总的来说都是先部署新机器，再修改老机器配置文件重启。单机扩容集群短暂的停止服务，集群扩容集群是用户无感知的  
截止2012-03-15，3.4.3版本的zookeeper，还不支持这个功能，在3.5.0版本开始，支持动态加机器了，期待下吧: https://issues.apache.org/jira/browse/ZOOKEEPER-107  
  
  
13. ZooKeeper集群中个服务器之间是怎样通信的？  
Leader服务器会和每一个Follower/Observer服务器都建立TCP连接，同时为每个F/O都创建一个叫做LearnerHandler的实体。LearnerHandler主要负责Leader和F/O之间的网络通讯，包括数据同步，请求转发和Proposal提议的投票等。Leader服务器保存了所有F/O的LearnerHandler。  
  
14.zookeeper是否会自动进行日志清理？如果进行日志清理？  
zk自己不会进行日志清理，需要运维人员进行日志清理，详细关于zk的日志清理，可以查看《ZooKeeper日志清理》  
  
  
zookeeper：Watcher、ZK状态，事件类型（一）  
zookeeper有watch事件，是一次性触发的，当watch监视的数据发生变化时，通知设置了该watch的client.即watcher.  
同样：其watcher是监听数据发送了某些变化，那就一定会有对应的事件类型和状态类型。  
	事件类型:(znode节点相关的)  
		 EventType:NodeCreated            //节点创建  
		 EventType:NodeDataChanged        //节点的数据变更  
		 EventType:NodeChildrentChanged   //子节点下的数据变更  
		 EventType:NodeDeleted  
	状态类型:(是跟客户端实例相关的)  
		 KeeperState:Disconneced        //连接失败  
 		 KeeperState:SyncConnected	//连接成功	   
		 KeeperState:AuthFailed         //认证失败  
		 KeeperState:Expired            //会话过期  
  
zookeeper的ACL(AUTH)  
ACL(Access Control List),Zookeeper作为一个分布式协调框架，其内部存储的都是一些关于分布式  
系统运行时状态的元数据，尤其是设计到一些分布式锁，Master选举和协调等应用场景。我们需要有  
效地保障Zookeeper中的数据安全，Zookeeper提供了三种模式。权限模式，授权对象，权限。  
权限模式：Scheme,开发人员最多使用的如下四种权限模式：  
	IP:ip模式通过ip地址粒度进行权限控制模式，例如配置了：192.168.110.135即表示权限控  
           制都是针对这个ip地址的，同时也支持按网段分配，比如：192.168.110.*  
	Digest:digest是最常用的权限控制模式，也更符合我们对权限控制的认识，其类似于  
               "username:password"形式的权限标识进行权限配置。ZK会对形成的权限标识先后进  
                行两次编码处理，粉笔是SHA-1加密算法和Base64编码。  
        World：World是一直最开放的权限控制模式。这种模式可以看做为特殊的Digest，他仅仅是  
               一个标识而已。  
        Super：超级用户模式，在超级用户模式下可以对ZK任意进行操作。  
  
权限对象：值得是权限赋予的用户或者是一个指定的实体，例如ip地址或机器等。在不同的模式下，  
授权对象是不同的。这种模式和权限对象一一对应。  
  
权限：权限就是指那些通过权限检测后可以被允许执行的操作，在ZK中，对数据的操作权限分为以下  
五大类：create,delete,read,write,admin  
  
参考：  
https://blog.csdn.net/qq_17089617/article/details/77928207  
https://blog.csdn.net/qq_17089617/article/details/77959377  
https://blog.csdn.net/pdw2009/article/details/73794525  
  
  
  
开始之前先介绍一些Zookeeper的权限。zookeeper支持的权限有5种分别是  
CREATE: 你可以创建子节点。  
READ: 你可以获取节点数据以及当前节点的子节点列表。  
WRITE: 你可以为节点设置数据。  
DELETE: 你可以删除子节点。  
ADMIN: 可以为节点设置权限。  
  
ZooKeeper设置ACL权限控制  
ZK的节点有5种操作权限：  
CREATE、READ、WRITE、DELETE、ADMIN 也就是 增、删、改、查、管理权限，这5种权限简写为crwda(即：每个单词的首字符缩写)  
注：这5种权限中，delete是指对子节点的删除权限，其它4种权限指对自身节点的操作权限  
  
身份的认证有4种方式：  
world：默认方式，相当于全世界都能访问  
auth：代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户)  
digest：即用户名:密码这种方式认证，这也是业务系统中最常用的  
ip：使用Ip地址认证  
  
设置访问控制：  
方式一：（推荐）  
1）增加一个认证用户  
addauth digest 用户名:密码明文  
eg. addauth digest user1:password1  
2）设置权限  
setAcl /path auth:用户名:密码明文:权限  
eg. setAcl /test auth:user1:password1:cdrwa  
3）查看Acl设置  
getAcl /path  
  
方式二：  
setAcl /path digest:用户名:密码密文:权限  
注：这里的加密规则是SHA1加密，然后base64编码。  
  
  
参考：  
1、http://www.cnblogs.com/yjmyzz/p/zookeeper-acl-demo.html  
 2、http://zookeeper.apache.org/doc/r3.1.2/zookeeperProgrammers.html  
https://www.jianshu.com/p/147ca2533aff  
  
https://blog.csdn.net/qq_17089617/article/details/77928207  
https://blog.csdn.net/qq_17089617/article/details/77959377  
  
  
  
利用zookeeper能做啥  
https://blog.csdn.net/yuzuyi2006/article/details/80009752  
  
1.使用zookeeper 实现动态维护服务列表（名字服务）  
2.使用zookeeper实现配置管理  
3．使用zookeeper分布式锁  
4.使用zookeeper集群管理  
  
  
  
zookeeper的选主机制的实现过程以及原理  
https://blog.csdn.net/lilong329329/article/details/78620382  
  
  
  
  
  
ZK总结  
https://www.cnblogs.com/Desneo/p/7212114.html  
https://blog.csdn.net/yjp198713/article/details/79400927  
https://www.cnblogs.com/netoxi/p/7291214.html  
  
Zookeeper：Client缺点：api复杂，节点只能一级一级创建，Watcher一次性的，只能监听直接子节点  
http://jm.taobao.org/2013/10/07/zookeeper-faq/  
1. 客户端对ServerList的轮询机制是什么？随机  
2.客户端如何正确处理CONNECTIONLOSS(连接断开) 和 SESSIONEXPIRED(Session 过期)两类连接异常：  
CONNECTIONLOSS(连接断开)：重连即可，Session还是存在的  
SESSIONEXPIRED(Session 过期)：要重新实例zookeeper对象，重新操作所有临时数据（包括临时节点和注册Watcher）。  
服务器认为这个session已经结束了（服务器无法确认是因为其它异常原因还是客户端主动结束会话），开始清除和这个会话有关的信息，包括这个会话创建的临时节点和注册的Watcher。  
  
  
3. 不同的客户端对同一个节点是否能获取相同的数据  
 一个客户端修改了某个节点的数据，其它客户端能够马上获取到这个最新数据吗  
ZooKeeper不能确保任何客户端能够获取（即Read Request）到一样的数据，这个是实际存在的现象，当然延时很短。除非客户端自己要求，解决的方法是客户端B先调用 sync(), 再调用 getData().  
  
  
5. ZK为什么不提供一个永久性的Watcher注册机制  
不支持用持久Watcher的原因很简单，ZK无法保证性能。  
  
6. 使用watch需要注意的几点  
a. Watches通知是一次性的，必须重复注册.  
b. 发生CONNECTIONLOSS之后，只要在session_timeout之内再次连接上（即不发生SESSIONEXPIRED），那么这个连接注册的watches依然在。  
c. 节点数据的版本变化会触发NodeDataChanged，注意，这里特意说明了是版本变化。存在这样的情况，只要成功执行了setData()方法，无论内容是否和之前一致，都会触发NodeDataChanged。  
d. 对某个节点注册了watch，但是节点被删除了，那么注册在这个节点上的watches都会被移除。  
e. 同一个zk客户端对某一个节点注册相同的watch，只会收到一次通知。  
f. Watcher对象只会保存在客户端，不会传递到服务端。  
  
  
7.我能否收到每次节点变化的通知  
不能，一般如果节点数据的更新频率很高的话，是没有问题的  
原因在于：当一次数据修改，通知客户端，客户端再次注册watch，在这个过程中，可能数据已经发生了许多次数据修改  
  
8.能为临时节点创建子节点吗？不能。  
  
9. 是否可以拒绝单个IP对ZK的访问,操作  
ZK本身不提供这样的功能，它仅仅提供了对单个IP的连接数的限制。你可以通过修改iptables来实现对单个ip的限制，当然，你也可以通过这样的方式来解决。https://issues.apache.org/jira/browse/ZOOKEEPER-1320  
  
10. 在getChildren(String path, boolean watch)是注册了对节点子节点的变化，那么子节点的子节点变化能通知吗？不能  
  
11.创建的临时节点什么时候会被删除，是连接一断就删除吗？延时是多少？  
连接断了之后，ZK不会马上移除临时数据，只有当SESSIONEXPIRED之后，才会把这个会话建立的临时数据移除。因此，用户需要谨慎设置Session_TimeOut  
  
  
12. zookeeper是否支持动态进行机器扩容？如果目前不支持，那么要如何扩容呢？  
截止2012-03-15，3.4.3版本的zookeeper，还不支持这个功能，在3.5.0版本开始，支持动态加机器了，期待下吧: https://issues.apache.org/jira/browse/ZOOKEEPER-107  
  
  
13. ZooKeeper集群中个服务器之间是怎样通信的？  
Leader服务器会和每一个Follower/Observer服务器都建立TCP连接，同时为每个F/O都创建一个叫做LearnerHandler的实体。LearnerHandler主要负责Leader和F/O之间的网络通讯，包括数据同步，请求转发和Proposal提议的投票等。Leader服务器保存了所有F/O的LearnerHandler。  
  
14.zookeeper是否会自动进行日志清理？如果进行日志清理？  
zk自己不会进行日志清理，需要运维人员进行日志清理，详细关于zk的日志清理，可以查看《ZooKeeper日志清理》  
  
  
Zookeeper脑裂是什么原因导致这样情况的出现呢？   
集群发现master挂掉（网络导致的假死），进行master切换，并且通知client，切换和通知client都需要时间，导致有的client连接到老的master，有的连接到新的master，此时连接不同master的client更新，就导致数据不一致，  
主要原因是Zookeeper集群和Zookeeper client判断超时并不能做到完全同步，也就是说可能一前一后，如果是集群先于client发现那就会出现上面的情况。同时，在发现并切换后通知各个客户端也有先后快慢。一般出现这种情况的几率很小，需要master与Zookeeper集群网络断开但是与其他集群角色之间的网络没有问题，还要满足上面那些情况，但是一旦出现就会引起很严重的后果，数据不一致。  
如何避免？   
在slaver切换的时候不在检查到老的master出现问题后马上切换，而是在休眠一段足够的时间，确保老的master已经获知变更并且做了相关的shutdown清理工作了然后再注册成为master就能避免这类问题了，这个休眠时间一般定义为与Zookeeper定义的超时时间就够了，但是这段时间内系统不可用了。  
  
  
