- [客户端配置](#客户端配置)
- [客户端交互流程](#客户端交互流程)
- [客户端CONNECTIONLOSS和SESSIONEXPIRED](#客户端CONNECTIONLOSS和SESSIONEXPIRED)
- [面试的常见问题](#面试的常见问题)
- [客户端框架](#客户端框架)

---------------------------------------------------------------------------------------------------------------------  
## 面试的常见问题


通过阅读本文，想必大家已从
①ZooKeeper的由来。 -> 
②ZooKeeper 到底是什么 。-> 
③ ZooKeeper 的一些重要概念（会话（Session）、 Znode、版本、Watcher、ACL）-> 
④ZooKeeper 的特点。 -> 
⑤ZooKeeper 的设计目标。-> 
⑥ ZooKeeper 集群角色介绍 （Leader、Follower 和 Observer 三种角色）-> 
⑦ZooKeeper &ZAB 协议&Paxos算法。 这七点了解了 ZooKeeper 。
如果Zookeeper主节点宕机,重新选主30S不可用，怎么处理？

zk的通知机制怎么实现的？
初始化zk客户端的时候，需要做事情吗？怎么处理闪断的情况？
连接和session过期，各做什么，客户端怎么知道的是哪个事件？
连接断开需要用户关心吗？还是sdk jar处理的吗？重连多少次算是失败？默认的？按照sessionTimeout时间算的
session过期是什么意思？客户端怎么知道的？什么原理？
session过期zk服务端会做什么事情？除了去除watcher，还有临时节点
临时节点什么时候会被删掉？是连接断开还是session过期会删除临时节点？或者是根据心跳来的？
心跳跟连接断开、session 有什么关系？默认心跳时间是是多少吗？怎么判断是session 过期
session保持是怎么做的？是一个心跳没有了，还是多少次没有？比如心跳是30S时间，那session什么时候会过期



Zookeeper学习中的疑难问题总结
https://blog.csdn.net/u010963948/article/details/83381757


---------------------------------------------------------------------------------------------------------------------  

## 客户端CONNECTIONLOSS和SESSIONEXPIRED


连接断开(CONNECTIONLOSS)一般发生在网络的闪断或是客户端所连接的服务器挂机的时候
ZK客户端捕获“连接断开”异常 ——> 获取一个新的ZK地址 ——> 尝试连接

sessionTimeout是客户端自己设置的，是跟服务端协商的
心跳是为了维持Session的保持，服务端每次收到请求都会重置sessionTimeout
sessionTimeout之后删除会话创建的临时节点和注册的所有Watcher。

EventType enventType = event.getType(); // 事件类型(一共有四种)
KeeperState stat = event.getState(); // ZK状态(一共有四种)


---------------------------------------------------------------------------------------------------------------------  
## 客户端配置

public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,
            boolean canBeReadOnly, HostProvider aHostProvider,
            ZKClientConfig clientConfig) 

this.sessionTimeout = sessionTimeout;
connectTimeout = sessionTimeout / hostProvider.size();
readTimeout = sessionTimeout * 2 / 3;


ZooKeeper集群的服务器地址列表（connectString）
该地址是可以填写多个的，以逗号分隔。如"127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"

sessionTimeout
最终会引出三个时间设置：和服务端协商后的sessionTimeout、readTimeout、connectTimeout
服务器端使用协商后的sessionTimeout：即超过该时间后，客户端没有向服务器端发送任何请求（正常情况下客户端会每隔一段时间发送心跳请求，此时服务器端会从新计算客户端的超时时间点的），则服务器端认为session超时，清理数据。此时客户端的ZooKeeper对象就不再起作用了，需要再重新new一个新的对象了。

客户端使用connectTimeout、readTimeout分别用于检测连接超时和读取超时，一旦超时，则该客户端认为该服务器不稳定，就会从新连接下一个服务器地址。

Watcher
作为ZooKeeper对象一个默认的Watcher，用于接收一些事件通知。如和服务器连接成功的通知、断开连接的通知、Session过期的通知等。


---------------------------------------------------------------------------------------------------------------------  
## 客户端交互流程

实例化zookeeper客户端后,客户端会创建ClientCnxn,其表示与服务端的连接交互对象。
ClientCnxn将创建两个线程SendThread和EventThread线程。
发送线程主要完成请求的发送和从服务端过来的应答的读取,
对于读取的应答需要进一步处理的转由事件线程来处理。



整个流程：
一旦客户端开始创建Zookeeper对象，客户端Zookeeper状态state设置为CONNECTING，成功连接上服务器后，客户端Zookeeper状态变更为CONNECTED。

建立TCP连接之后，客户端发送ConnectRequest请求，申请建立session关联，此时服务器端会为该客户端分配sessionId和密码，同时开启对该session是否超时的检测。

当在sessionTimeout时间内，即还未超时，此时TCP连接断开，服务器端仍然认为该sessionId处于存活状态。此时，客户端会选择下一个ZooKeeper服务器地址进行TCP连接建立，TCP连接建立完成后，拿着之前的sessionId和密码发送ConnectRequest请求，如果还未到该sessionId的超时时间，则表示自动重连成功，对客户端用户是透明的，一切都在背后默默执行，ZooKeeper对象是有效的。

如果重新建立TCP连接后，已经达到该sessionId的超时时间了（服务器端就会清理与该sessionId相关的数据），则返回给客户端的sessionTimeout时间为0，sessionid为0，密码为空字节数组。客户端接收到该数据后，会判断协商后的sessionTimeout时间是否小于等于0，如果小于等于0，则使用eventThread线程先发出一个KeeperState.Expired事件，通知相应的Watcher，然后结束EventThread线程的循环，开始走向结束。此时ZooKeeper对象就是无效的了，必须要重新new一个新的ZooKeeper对象，分配新的sessionId了。



1、ZooKeeper客户端初始化后转换到CONNECTING状态，与ZooKeeper服务器（或者ZooKeeper集群中的一台服务器）建立连接后，进入CONNECTED状态
2、当客户端与ZooKeeper服务器断开连接或者无法收到服务器响应时，就会转回到CONNECTING状态，此时会一直收到CONNECTION_LOSS。
3、这时ZooKeeper客户端会自动地从列表中逐个选取新的地址进行重连，如果成功，状态又会转回CONNECTED状态
4、如果一直无法重连，超过会话超时时间(sessionTimeout)后，服务器认为这个session已经结束了，此时客户端无法感知
5、最后当客户端终于自动重连到ZooKeeper服务器时，会收到Session Expired；这种情况下，需要应用层关闭当前会话，然后重连。
6、在CONNECTING状态和CONNECTED状态，客户端都可以显示地关闭，进入CLOSED状态

当建立session时，客户会收到由服务器创建的session id和password。当自动重连时，客户端都会发送session id和password给服务端，重建的还是原来的session。以上的自动重连都是由底层的API库来实现，非应用层面去实现的重连



在ZooKeeper中，服务器和客户端之间维持的是一个长连接，在 SESSION_TIMEOUT 时间内，服务器会确定客户端是否正常连接(客户端会定时向服务器发送heart_beat),服务器重置下次SESSION_TIMEOUT时间。
因此，在正常情况下，Session一直有效，并且zk集群所有机器上都保存这个Session信息。
在出现问题情况下，客户端与服务器之间连接断了（客户端所连接的那台zk机器挂了，或是其它原因的网络闪断），这个时候客户端会主动在地址列表（初始化的时候传入构造方法的那个参数connectString）中选择新的地址进行连接。

好了，上面基本就是服务器与客户端之间维持长连接的过程了。
在这个过程中，用户可能会看到两类异常CONNECTIONLOSS(连接断开) 和SESSIONEXPIRED(Session 过期)。
CONNECTIONLOSS发生在上面红色文字部分，应用在进行操作A时，发生了CONNECTIONLOSS，此时用户不需要关心我的会话是否可用，应用所要做的就是等待客户端帮我们自动连接上新的zk机器，一旦成功连接上新的zk机器后，确认刚刚的操作A是否执行成功了。

SESSIONEXPIRED发生在上面粗体部分，这个通常是zk客户端与服务器的连接断了，试图连接上新的zk机器，这个过程如果耗时过长，超过 SESSION_TIMEOUT 后还没有成功连接上服务器，那么服务器认为这个session已经结束了（服务器无法确认是因为其它异常原因还是客户端主动结束会话），开始清除和这个会话有关的信息，包括这个会话创建的临时节点和注册的Watcher。
在这之后，客户端重新连接上了服务器在，但是很不幸，服务器会告诉客户端SESSIONEXPIRED。此时客户端要做的事情就看应用的复杂情况了，总之，要重新实例zookeeper对象，重新操作所有临时数据（包括临时节点和注册Watcher）。


SESSIONEXPIRED发生在上面蓝色文字部分，这个通常是ZK客户端与服务器的连接断了，试图连接上新的ZK机器，但是这个过程如果耗时过长，超过了SESSION_TIMEOUT 后还没有成功连接上服务器，那么服务器认为这个Session已经结束了（服务器无法确认是因为其它异常原因还是客户端主动结束会话），
由于在ZK中，很多数据和状态都是和会话绑定的，一旦会话失效，那么ZK就开始清除和这个会话有关的信息，包括这个会话创建的临时节点和注册的所有Watcher。
在这之后，由于网络恢复后，客户端可能会重新连接上服务器，但是很不幸，服务器会告诉客户端一个异常：SESSIONEXPIRED（会话过期）。此时客户端的状态变成 CLOSED状态，应用要做的事情就是的看自己应用的复杂程序了，要重新实例zookeeper对象，然后重新操作所有临时数据（包括临时节点和注册Watcher），总之，会话超时在ZK使用过程中是真实存在的。
 
所以这里也简单总结下，一旦发生会话超时，那么存储在ZK上的所有临时数据与注册的订阅者都会被移除，此时需要重新创建一个ZooKeeper客户端实例，需要自己编码做一些额外的处理。




参考
https://blog.csdn.net/ljimking/article/details/76835270
https://blog.csdn.net/bestree007/article/details/18996479
https://xusenqi.github.io/2019/06/06/%E5%85%B3%E4%BA%8E%E5%AE%A2%E6%88%B7%E7%AB%AF%E9%87%8D%E8%BF%9EZooKeeper%E7%9A%84%E7%9A%84%E9%82%A3%E4%BA%9B%E4%BA%8B%E5%84%BF/
https://www.jianshu.com/p/7491a8b558c1
https://halfsre.com/article/2019/5/13/7.html


---------------------------------------------------------------------------------------------------------------------  
## 客户端框架

1、zookeeper api
2、zkclient
3、curator-framework
4、











