
https://blog.csdn.net/xiaoxiaoxuanao/article/category/6460803
Cache分为两类监听类型：节点监听和子节点监听（直接子节点、多级子节点）。

1、NodeCache：节点本身监听
NodeCache不仅可以用于监听数据节点的内容变更，也能监听指定节点是否存在。如果原本节点不存在，那么Cache就会在节点被创建后触发NodeCacheListener。但是，如果该数据节点被删除，那么Curator就无法触发NodeCacheListener了。

2、PathChildrenCache：节点直接子节点监听
1）永久监听指定节点下的节点 
（2）只能监听指定节点下一级节点的变化，比如说指定节点”/example”, 在下面添加”node1”可以监听到，但是添加”node1/n1”就不能被监听到了 
（3）可以监听到的事件：节点创建、节点数据的变化、节点删除等
当指定节点的子节点发生变化时，就会回调该方法。PathChildrenCacheEvent类中定义了所有的事件类型，主要包括新增子节点（CHILD_ADDED）、子节点数据变更（CHILD_UPDATED）和子节点删除（CHILD_REMOVED）三类。
一旦该节点新增/删除子节点，或者子节点数据发生变更，就会回调PathChildrenCacheListener，并根据对应的事件类型进行相关的处理。同时，我们也看到，对于节点zk=book本身的变更，并没有通知到客户端。

3、TreeCache：节点的多级子节点
（1）永久监听指定节点下的节点的变化 
（2）可以监听到指定节点下所有节点的变化，比如说指定节点”/example”, 在下面添加”node1”可以监听到，但是添加”node1/n1”也能被监听到 
（3）可以监听到的事件：节点创建、节点数据的变化、节点删除等















