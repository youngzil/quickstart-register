- [Curator介绍](#Curator介绍)
- [Curator分为两类监听类型：节点本身监听和子节点监听（直接子节点、多级子节点）](#Curator分为两类监听类型：节点本身监听和子节点监听（直接子节点、多级子节点）)
- [双栅栏功能：分布式Barrier](#双栅栏功能：分布式Barrier)
- [分布式计数器：原子变量DistributedAtomicLong和共享变量SharedCount](#分布式计数器：原子变量DistributedAtomicLong和共享变量SharedCount)
- [Leader Election选举相关的](#Leader选举相关的)
- [分布式锁相关](#分布式锁相关)
- [Queue分布式队列](#Queue分布式队列)
- [事务相关](#事务相关)
- [服务发现ServiceDiscovery](#服务发现ServiceDiscovery)

---------------------------------------------------------------------------------------------------------------------

## Curator介绍

Apache Curator是用于Apache ZooKeeper（一种分布式协调服务）的Java / JVM客户端库。它包括一个高级API框架和实用程序，使使用Apache ZooKeeper变得更加轻松和可靠。它还包括常见用例和扩展的配方，例如服务发现和Java 8异步DSL。

Curator是Netflix公司开源的一套zookeeper客户端框架，解决了很多Zookeeper客户端非常底层的细节开发工作，包括连接重连、反复注册Watcher和NodeExistsException异常等等。

Patrixck Hunt（Zookeeper）以一句“Guava is to Java that Curator to Zookeeper”给Curator予高度评价。


Curator包含的包：
1. curator-framework：对zookeeper的底层api的一些封装
2. curator-client：提供一些客户端的操作，例如重试策略等
3. curator-recipes：封装了一些高级特性，如：Cache事件监听、选举、分布式锁、分布式计数器、分布式Barrier等。




[Curator](http://curator.apache.org/) :基于ZooKeeper的更高层次的封装
[Curator Github](https://github.com/apache/curator)





[Zookeeper Curator详解](https://my.oschina.net/roccn?tab=newest&catalogId=5647769)  

[Apache Curator 的简单介绍](https://blog.csdn.net/xiaojin21cen/article/details/88538102)  
[Zookeeper客户端Curator的API使用详解](https://www.cnblogs.com/shamo89/p/9800925.html)  
[https://www.baeldung.com/apache-curator](https://www.baeldung.com/apache-curator)  


---------------------------------------------------------------------------------------------------------------------


## Curator分为两类监听类型：节点本身监听和子节点监听（直接子节点、多级子节点）


1、NodeCache：节点本身监听
NodeCache不仅可以用于监听数据节点的内容变更，也能监听指定节点是否存在。如果原本节点不存在，那么Cache就会在节点被创建后触发NodeCacheListener。但是，如果该数据节点被删除，那么Curator就无法触发NodeCacheListener了。

2、PathChildrenCache：节点直接子节点监听
- （1）永久监听指定节点下的节点 
- （2）只能监听指定节点下一级节点的变化，比如说指定节点”/example”, 在下面添加”node1”可以监听到，但是添加”node1/n1”就不能被监听到了 
- （3）可以监听到的事件：节点创建、节点数据的变化、节点删除等
当指定节点的子节点发生变化时，就会回调该方法。PathChildrenCacheEvent类中定义了所有的事件类型，主要包括新增子节点（CHILD_ADDED）、子节点数据变更（CHILD_UPDATED）和子节点删除（CHILD_REMOVED）三类。
一旦该节点新增/删除子节点，或者子节点数据发生变更，就会回调PathChildrenCacheListener，并根据对应的事件类型进行相关的处理。同时，我们也看到，对于节点zk=book本身的变更，并没有通知到客户端。

3、TreeCache：节点的多级子节点
- （1）永久监听指定节点下的节点的变化 
- （2）可以监听到指定节点下所有节点的变化，比如说指定节点”/example”, 在下面添加”node1”可以监听到，但是添加”node1/n1”也能被监听到 
- （3）可以监听到的事件：节点创建、节点数据的变化、节点删除等


参考  
https://blog.csdn.net/xiaoxiaoxuanao/article/category/6460803  



---------------------------------------------------------------------------------------------------------------------

## 双栅栏功能：分布式Barrier

对于普通栅栏，如果要控制多个任务的开始，结束。需要自己实现控制逻辑。 而双重栅栏，相当于有了开始，结束的边界，在使用时，可以更好的控制。

双栅栏类是：
DistributedDoubleBarrier：当enter方法被调用时，成员被阻塞，直到所有的成员都调用了enter。当leave方法被调用时，它也阻塞调用线程，知道所有的成员都调用了leave。
DistributedBarrier类：waitOnBarrier和removeBarrier




---------------------------------------------------------------------------------------------------------------------
## 分布式计数器：原子变量DistributedAtomicLong和共享变量SharedCount

DistributedAtomicLong：内部通过DistributedAtomicValue来实现分布式原子数据的操作。

DistributedAtomicLong的操作大部分都是使用DistributedAtomicValue的trySet方法。

所以，使用时务必需要检查返回的AtomicValue对象的succeeded()方法。


Shared Counter：管理着一个共享整型数据。所有的客户端都监听者同一个路径下的这个整型值的变化。



[Shared Counter 的使用与分析](https://my.oschina.net/roccn/blog/916616)  
[Distributed Atomic Long 的使用与分析](https://my.oschina.net/roccn/blog/917128)  



---------------------------------------------------------------------------------------------------------------------

## Leader选举相关的

Leader Election


LeaderSelector
LeaderLatch


LeaderSelector
基本原理
利用Curator中InterProcessMutex分布式锁进行抢主，抢到锁的即为Leader


LeaderLatch
基本原理
选择一个根路径，例如"/leader_select"，多个机器同时向该根路径下创建临时顺序节点，如"/leader_latch/node_3"，"/leader_latch/node_1"，"/leader_latch/node_2"，节点编号最小(这里为node_1)的zk客户端成为leader，没抢到Leader的节点都监听前一个节点的删除事件，在前一个节点删除后进行重新抢主



[Master选举LeaderLatch,LeaderSelector使用及原理分析](https://blog.csdn.net/hosaos/article/details/88727817)  
[http://curator.incubator.apache.org/curator-recipes/leader-election.html](http://curator.incubator.apache.org/curator-recipes/leader-election.html)  

---------------------------------------------------------------------------------------------------------------------

## 分布式锁相关

InterProcessMutex：分布式可重入排它锁
InterProcessSemaphoreMutex：分布式排它锁
InterProcessReadWriteLock：分布式读写锁
InterProcessMultiLock：将多个锁作为单个实体管理的容器
InterProcessSemaphoreV2：分布式的信号量Semaphore




1. 可重入锁Shared Reentrant Lock：它是由类InterProcessMutex来实现。
2. 不可重入锁Shared Lock：这个类是InterProcessSemaphoreMutex。

3. 可重入读写锁Shared Reentrant Read Write Lock

类似JDK的ReentrantReadWriteLock.
一个读写锁管理一对相关的锁。 一个负责读操作，另外一个负责写操作。 读操作在写锁没被使用时可同时由多个进程使用，而写锁使用时不允许读 (阻塞)。
此锁是可重入的。一个拥有写锁的线程可重入读锁，但是读锁却不能进入写锁。
这也意味着写锁可以降级成读锁， 比如请求写锁 --->读锁 ---->释放写锁。 从读锁升级成写锁是不成的。

主要由两个类实现：
- InterProcessReadWriteLock
- InterProcessLock

使用时首先创建一个InterProcessReadWriteLock实例，然后再根据你的需求得到读锁或者写锁， 读写锁的类型是InterProcessLock。

4. 信号量Shared Semaphore ：InterProcessSemaphoreV2类

一个计数的信号量类似JDK的Semaphore。 JDK中Semaphore维护的一组许可(permits)，而Cubator中称之为租约(Lease)。
有两种方式可以决定semaphore的最大租约数。第一种方式是有用户给定的path决定。第二种方式使用SharedCountReader类。


5. 多锁对象 Multi Shared Lock：类InterProcessMultiLock
   Multi Shared Lock是一个锁的容器。 当调用acquire， 所有的锁都会被acquire，如果请求失败，所有的锁都会被release。 同样调用release时所有的锁都被release(失败被忽略)。
   基本上，它就是组锁的代表，在它上面的请求释放操作都会传递给它包含的所有的锁。

   

[ZooKeeper的用法：分布式锁](https://colobu.com/2014/12/12/zookeeper-recipes-by-example-2/)  
[Curator实现分布式锁](https://www.cnblogs.com/qlqwjy/p/10518900.html)  


---------------------------------------------------------------------------------------------------------------------
## Queue分布式队列


DistributedQueue是最普通的一种队列。

DistributedIdQueue和上面的队列类似， 但是可以为队列中的每一个元素设置一个ID。 可以通过ID把队列中任意的元素移除。

DistributedPriorityQueue优先级队列对队列中的元素按照优先级进行排序。 Priority越小， 元素月靠前， 越先被消费掉。当优先级队列得到元素增删消息时，它会暂停处理当前的元素队列，然后刷新队列。minItemsBeforeRefresh指定刷新前当前活动的队列的最小数量。 主要设置你的程序可以容忍的不排序的最小值。

DistributedDelayQueue，JDK中也有DelayQueue，不知道你是否熟悉。 DistributedDelayQueue也提供了类似的功能， 元素有个delay值， 消费者隔一段时间才能收到元素。

SimpleDistributedQueue
前面虽然实现了各种队列，但是你注意到没有，这些队列并没有实现类似JDK一样的接口。
SimpleDistributedQueue提供了和JDK一致性的接口(但是没有实现Queue接口)。



DistributedQueue是最普通的一种队列。 它设计以下四个类：
QueueBuilder
QueueConsumer
QueueSerializer
DistributedQueue


Curator也提供ZK Recipe的分布式队列实现。 利用ZK的 PERSISTENTSEQUENTIAL节点， 可以保证放入到队列中的项目是按照顺序排队的。 如果单一的消费者从队列中取数据， 那么它是先入先出的，这也是队列的特点。 如果你严格要求顺序，你就的使用单一的消费者，可以使用leader选举只让leader作为唯一的消费者。

但是， 根据Netflix的Curator作者所说， ZooKeeper真心不适合做Queue，或者说ZK没有实现一个好的Queue，详细内容可以看 Tech Note 4， 原因有五：
1. ZK有1MB 的传输限制。 实践中ZNode必须相对较小，而队列包含成千上万的消息，非常的大。
2. 如果有很多节点，ZK启动时相当的慢。 而使用queue会导致好多ZNode. 你需要显著增大 initLimit 和 syncLimit.
3. ZNode很大的时候很难清理。Netflix不得不创建了一个专门的程序做这事。
4. 当很大量的包含成千上万的子节点的ZNode时， ZK的性能变得不好
5. ZK的数据库完全放在内存中。 大量的Queue意味着会占用很多的内存空间。

尽管如此， Curator还是创建了各种Queue的实现。 如果Queue的数据量不太多，数据量不太大的情况下，酌情考虑，还是可以使用的。


正常情况下先将消息从队列中移除，再交给消费者消费。 但这是两个步骤，不是原子的。 可以调用Builder的lockPath()消费者加锁， 当消费者消费数据时持有锁，这样其它消费者不能消费此消息。 如果消费失败或者进程死掉，消息可以交给其它进程。这会带来一点性能的损失。 最好还是单消费者模式使用队列。



[Curator的分布式队列实现](https://colobu.com/2014/12/15/zookeeper-recipes-by-example-7/)  


---------------------------------------------------------------------------------------------------------------------
## 事务相关


CuratorFramework提供了事务的概念，可以将每个操作放在一个原子事务中。什么叫事务？事务是原子的，单个操作都都成功，可能都失败。


---------------------------------------------------------------------------------------------------------------------
## 服务发现ServiceDiscovery

ServiceDiscovery类

Service Discovery 的使用

一般而言，分为 Service Registry 和 Service Discovery，对应服务端和客户端。也就是由服务提供者，讲自身的信息注册到Zookeeper，然后，客户端通过到Zookeeper中查找服务信息，然后根据信息就行调用


关于Apache curator的service discovery的一些介绍可以参考官方文档：http://curator.apache.org/curator-x-discovery/index.html


[Service Discovery with Apache Curator](https://www.cnblogs.com/hupengcool/p/3976362.html)  
[5分钟了解[Apache]Curator Service Discovery](https://www.jianshu.com/p/a4b99a08f1ca)  
[Curator教程（三）服务注册&发现（Service Discovery）](https://blog.csdn.net/top_code/article/details/53559160)  

---------------------------------------------------------------------------------------------------------------------



