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
截止2012-03-15，3.4.3版本的zookeeper，还不支持这个功能，在3.5.0版本开始，支持动态加机器了，期待下吧: https://issues.apache.org/jira/browse/ZOOKEEPER-107


13. ZooKeeper集群中个服务器之间是怎样通信的？
Leader服务器会和每一个Follower/Observer服务器都建立TCP连接，同时为每个F/O都创建一个叫做LearnerHandler的实体。LearnerHandler主要负责Leader和F/O之间的网络通讯，包括数据同步，请求转发和Proposal提议的投票等。Leader服务器保存了所有F/O的LearnerHandler。

14.zookeeper是否会自动进行日志清理？如果进行日志清理？
zk自己不会进行日志清理，需要运维人员进行日志清理，详细关于zk的日志清理，可以查看《ZooKeeper日志清理》


