https://www.ibm.com/developerworks/cn/opensource/os-cn-apache-zookeeper-watcher/    
https://blog.csdn.net/odailidong/article/details/46473695   
https://www.oschina.net/question/1247185_152442     

实现原理    
ZooKeeper 允许客户端向服务端注册一个 Watcher 监听，当服务端的一些指定事件触发了这个 Watcher，那么就会向指定客户端发送一个事件通知来实现分布式的通知功能。 

watch的通知机制是不可靠的，zkServer不会保证通知的可靠抵达。虽然zkclient与zkServer端是会有心跳机制保持链接，但是如果通知过程中断开，即时重新建立连接后，watch的状态是不会恢复。     


ZooKeeper 的 Watcher 机制主要包括客户端线程、客户端 WatchManager 和 ZooKeeper 服务器三部分。在具体工作流程上，简单地讲，客户端在向 ZooKeeper 服务器注册 Watcher 的同时，会将 Watcher 对象存储在客户端的 WatchManager 中。当 ZooKeeper 服务器端触发 Watcher 事件后，会向客户端发送通知，客户端线程从 WatchManager 中取出对应的 Watcher 对象来执行回调逻辑。  


ZooKeeper Watcher 特性总结      
1. 注册只能确保一次消费   
无论是服务端还是客户端，一旦一个 Watcher 被触发，ZooKeeper 都会将其从相应的存储中移除。因此，开发人员在 Watcher 的使用上要记住的一点是需要反复注册。这样的设计有效地减轻了服务端的压力。如果注册一个 Watcher 之后一直有效，那么针对那些更新非常频繁的节点，服务端会不断地向客户端发送事件通知，这无论对于网络还是服务端性能的影响都非常大。    

2. 客户端串行执行  
客户端 Watcher 回调的过程是一个串行同步的过程，这为我们保证了顺序，同时，需要开发人员注意的一点是，千万不要因为一个 Watcher 的处理逻辑影响了整个客户端的 Watcher 回调。   

3. 轻量级设计    
WatchedEvent 是 ZooKeeper 整个 Watcher 通知机制的最小通知单元，这个数据结构中只包含三部分的内容：通知状态、事件类型和节点路径。    
也就是说，Watcher 通知非常简单，只会告诉客户端发生了事件，而不会说明事件的具体内容。例如针对 NodeDataChanged 事件，ZooKeeper 的 Watcher 只会通知客户指定数据节点的数据内容发生了变更，而对于原始数据以及变更后的新数据都无法从这个事件中直接获取到，而是需要客户端主动重新去获取数据，这也是 ZooKeeper 的 Watcher 机制的一个非常重要的特性。另外，客户端向服务端注册 Watcher 的时候，并不会把客户端真实的 Watcher 对象传递到服务端，仅仅只是在客户端请求中使用 boolean 类型属性进行了标记，同时服务端也仅仅只是保存了当前连接的 ServerCnxn 对象。这样轻量级的 Watcher 机制设计，在网络开销和服务端内存开销上都是非常廉价的。  


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
		 
		 

结束语     
本文首先介绍了一个简单的监听示例代码，通过监听 ZNode 的变化，触发回调函数来实现触发后的业务处理，接下来简单介绍了一点回调函数的基本知识，然后我们开始讨论 Watcher 机制的实现原理，从 Watcher 接口开始聊，引申出 WatcherEvent 类型，再到添加 watcher 事件以及回调函数基本原理介绍，最后对 Watcher 机制的设计原理进行了三点总结。    


zookeeper watch的定义如下：watch事件是一次性触发器，当watch监视的数据发生变化时，通知设置了该watch的client，即watcher。   
关于watch要记住的是：   
1.watch是一次性触发的，如果获取一个watch事件并希望得到新变化的通知，需要重新设置watch     
2.watch是一次性触发的并且在获取watch事件和设置新watch事件之间有延迟，所以不能可靠的观察到节点的每一次变化。要认识到这一点。  
3.watch object只触发一次，比如，一个watch object被注册到同一个节点的getData()和exists()，节点被删除，仅对应于exists()的watch ojbect被调用    
4.若与服务端断开连接，直到重连后才能获取watch事件。   


ZooKeeper 在官网着重提示在使用 Watch 的时候要注意：  
   - Watch 是一次性的，如果 watch 事件发生了，还想 watch 需要再设置新的watch    
   - 因为 watch 的一次性，再次注册 watch 的网络延迟，所以 znode 每次变更不可能都 watch 到    
   - 一个 watch 对象或者函数/上下文对(pair)，只会触发一次。比如，如果相同的 watch 对象注册了 exist 和 getData 调用在相同文件，并且文件已经被删除，watch 对象只会在文件被删除触发一次   
   - 当你与一个服务断开（比如zk服务宕机），你将不会获得任何 watch，直到连接重连。因此，session 事件将会发送给所有 watch 处理器。使用 session 事件进入一个安全模式：当断开连接的时候将不会收到任何事件，因此您的进程应该以该模式保守运行   


可以看到，并不是每次 client 都是收到 watch 回调，会漏掉几次。所以在使用 ZooKeeper Watch 的时候，不能觉得监听回调一定会成功，所以在写代码的时候要注意这一点。  


zookeeper watch的定义如下：watch事件是一次性触发器，当watch监视的数据发生变化时，通知设置了该watch的client，即watcher。

需要注意三点：

1.一次性触发器
client在一个节点上设置watch，随后节点内容改变，client将获取事件。当节点内容再次改变，client不会获取这个事件，除非它又执行了一次读操作并设置watch

2.发送至client，watch事件延迟
watch事件异步发送至观察者。比如说client执行一次写操作，节点数据内容发生变化，操作返回后，而watch事件可能还在发往client的路上。这种情况下，zookeeper提供有序保证：client不会得知数据变化，直到它获取watch事件。网络延迟或其他因素可能导致不同client在不同时刻获取watch事件和操作返回值。

3.设置watch的数据内容
涉及到节点改变的不同方式。比方说zookeeper维护两个watch列表：节点的数据watch和子节点watch。getData()和exists()设置了内容watch，getChildren()设置了子节点watch，操作返回的数据类型不同，前者是节点的内容，后者是节点的子节点列表。setData()触发内容watch，create()触发当前节点的"内容watch"和其父节点的"子节点watch"，delete()同时触发"内容watch"和"子节点watch"（其子节点被全部删除），以及其父节点的"子节点watch"。说白了，对当前节点的操作，要考虑到对其父节点与子节点的影响。

watch在客户端所连接的服务端本地维护。watch的设置、维护、分发操作都很轻量级。当客户端连接到新的服务端，watch将被任一会话事件触发。与服务端断开连接时，不能获取watch事件。客户端重连后，之前注册的watch将被重新注册并在需要时触发。通常这一切透明地发生，用户不会察觉到。有一种情况watch可能丢失：之前对一个尚未建立的节点的设置了exists watch，如果断开期间该节点被建立或删除，那么此watch将丢失。

对于watch，zookeeper提供以下保证：
1.watch对于其他事件、watch、异步响应是有序的。zookeeper client library保证有序分发
2.客户端监视一个节点，总是先获取watch事件，再发现节点的数据变化。
3.watch事件的顺序对应于zookeeper服务所见的数据更新的顺序。

关于watch要记住的是：
1.watch是一次性触发的，如果获取一个watch事件并希望得到新变化的通知，需要重新设置watch
2.watch是一次性触发的并且在获取watch事件和设置新watch事件之间有延迟，所以不能可靠的观察到节点的每一次变化。要认识到这一点。
3.watch object只触发一次，比如，一个watch object被注册到同一个节点的getData()和exists()，节点被删除，仅对应于exists()的watch ojbect被调用
4.若与服务端断开连接，直到重连后才能获取watch事件。




参考  
https://cloud.tencent.com/developer/article/1158972     
https://blog.csdn.net/wo541075754/article/details/70207722  

