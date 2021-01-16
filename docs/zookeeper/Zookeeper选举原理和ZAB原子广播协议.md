1. 刚刚启动：选举FastLeaderElection算法，选择出leader节点
2. 使用ZAB协议用来保证zookeeper各个节点之间数据的一致性
3. leader崩溃或者leader失去大多数的follower，这时候zk进入恢复模式，使用FastLeaderElection算法，选择出新的leader节点
4. 使用ZAB恢复模式，把新的leader节点上自身有而follower缺失的事务发送给它，再将这些事务的commit命令发送给 follower




作为一个分布式应用程序协调服务，在大型网站中，其本身也是集群部署的，安装zookeeper的时候最好是单数节点，因为要选举。

Zookeeper的leader节点是集群工作的核心，用来更新并保证leader和server具有相同的系统状态，Follower服务器是Leader的跟随者，用于接收客户端的请求并向客户端返回结果，在选举过程中参与投票。对于客户端来说，每个zookeeper都是一样的。


zookeeper提供了三种选择策略：
- LeaderElection
- AuthFastLeaderElection
- FastLeaderElection

这里仅介绍默认的算法：FastLeaderElection。


基础概念
- Sid：服务器id；
- Zxid：服务器的事务id，数据越新，zxid越大；
- epoch：逻辑时钟，在服务端是一个自增序列，每次进入下一轮投票后，就会加1；
- server状态：
    - Looking（选举状态）
    - Leading（领导者状态，表明当前server是leader）
    - Following（跟随者状态，表明当前server是Follower）
    - Observing（观察者状态、表明当前server是Observer）。


选举步骤  
当系统启动或者leader崩溃后，就会开始leader的选举。  
当leader崩溃或者leader失去大多数的follower，这时候zk进入恢复模式，恢复模式需要重新选举出一个新的leader，让所有的Server都恢复到一个正确的状态。  

1. 状态变更。服务器启动的时候每个server的状态时Looking，如果是leader挂掉后进入选举，那么余下的非Observer的Server就会将自己的服务器状态变更为Looking，然后开始进入Leader的选举状态；
2. 发起投票。每个server会产生一个（sid，zxid）的投票，系统初始化的时候zxid都是0，如果是运行期间，每个server的zxid可能都不同，这取决于最后一次更新的数据。将投票发送给集群中的所有机器；
3. 接收并检查投票。server收到投票后，会先检查是否是本轮投票，是否来自looking状态的server；
4. 处理投票。对自己的投票和接收到的投票进行PK：
    - 先检查zxid，较大的优先为leader；
    - 如果zxid一样，sid较大的为leader；  
    根据PK结果更新自己的投票，在次发送自己的投票；
5. 统计投票。每次投票后，服务器统计投票信息，如果有过半机器接收到相同的投票，那么leader产生，如果否，那么进行下一轮投票；
6. 改变server状态。一旦确定了Leader，server会更新自己的状态为Following或者是Leading。选举结束。



补充说明：

1. 在步骤2发送投票的时候，投票的信息除了sid和zxid，还有：
    - electionEpoch：逻辑时钟，用来判断多个投票是否在同一轮选举周期中，该值在服务端是一个自增序列，每次进入新一轮的投票后，都会对该值进行加1操作。
    - peerEpoch：被推举的Leader的epoch。
    - state：当前服务器的状态。
2. 为了能够相互投票，每两台服务器之间都会建立网络连接，为避免重复建立TCP连接，zk的server只允许sid大于自己的服务器与自己建立连接，否则断开当前连接，并主动和对方建立连接。




## ZAB原子广播协议
ZAB（Zookeeper Atomic Broadcast，zookeeper原子广播），ZAB协议用来保证zookeeper各个节点之间数据的一致性。

ZAB协议
- 原子广播
- 崩溃恢复

ZAB协议包括如下特点：
- follower节点上所有的写请求都转发给leader
- 写操作严格有序
- ZooKeeper使用改编的两阶段提交协议来保证各个节点的事务一致性


zookeeper集群的状态分为两种：正常状态和异常状态。也就是有leader（能提供服务）和没有leader（进入选举）

广播模式  
广播模式就是指zookeeper正常工作的模式。正常情况下，一个写入命令会经过如下步骤被执行

- leader从客户端或者follower那里收到一个写请求
- leader生成一个新的事务并为这个事务生成一个唯一的Zxid，
- leader将这个事务发送给所有的follows节点
- follower节点将收到的事务请求加入到历史队列(history queue)中，并发送ack给leader
- 当leader收到大多数follower（超过法定数量）的ack消息，leader会发送commit请求
- 当follower收到commit请求时，会判断该事务的Zxid是不是比历史队列中的任何事务的Zxid都小，如果是则commit，如果不是则等待比它更小的事务的commit



恢复模式  
当leader故障之后，zookeeper集群进入无主模式，此时zookeeper集群不能对外提供服务，必须选出一个新的leader完成数据一致后才能重新对外提供服务。zookeeper官方宣称集群可以在200毫秒内选出一个新leader。

如果在leader故障之前已经commit，zookeeper依然会根据Zxid或者myid选出数据最新的那个follower作为新的leader。新leader与follower建立FIFO的队列， 先将自身有而follower缺失的事务发送给它，再将这些事务的commit命令发送给 follower，这便保证了所有的follower都保存了所有的事务、所有的follower都处理了所有的消息。

ZAB 协议确保那些已经在 Leader 提交的事务最终会被所有服务器提交。  
ZAB 协议确保丢弃那些只在 Leader 提出/复制，但没有提交的事务。  



参考  
https://segmentfault.com/a/1190000014932133  
https://www.jianshu.com/p/e35104ec6e5a  

https://blog.csdn.net/Baisitao_/article/details/105877473  
https://zhuanlan.zhihu.com/p/87008001  
https://www.cnblogs.com/wuzhenzhao/p/9983231.html  

https://juejin.cn/post/6844903829176074247  
https://www.jianshu.com/p/5300f1f454e8  
https://posts.careerengine.us/p/5eedf3e81aba4f30fd8b4499  



ZK使用运维问题








