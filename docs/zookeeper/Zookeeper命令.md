- [Zookeeper服务相关命令（服务端命令）](#Zookeeper服务相关命令（服务端命令）)
- [Zookeeper命令连接（zk客户端命令）](#Zookeeper命令连接（zk客户端命令）)
- [ZooKeeper常用四字命令](#ZooKeeper常用四字命令)




## Zookeeper服务相关命令（服务端命令）

在准备好相应的配置之后，可以直接通过zkServer.sh 这个脚本进行服务的相关操作
1. 启动ZK服务:       sh bin/zkServer.sh start
2. 查看ZK服务状态:   sh bin/zkServer.sh status
3. 停止ZK服务:       sh bin/zkServer.sh stop
4. 重启ZK服务:       sh bin/zkServer.sh restart
5. ZK服务连接:       sh bin/zkCli.sh -server 127.0.0.1:2181
备注:
需要在zookeeper目录下去执行这些命令,如:
[root@localhost zookeeper-3.4.10]# pwd
/opt/zookeeper-3.4.10


查看ZK版本
echo stat|nc localhost 2181



## Zookeeper命令连接（zk客户端命令）

启动
[root@localhost zookeeper-3.4.10]# sh ./bin/zkServer.sh start
单机模式(只有一台服务器)连接
[root@localhost zookeeper-3.4.10]# ./bin/zkCli.sh -server 127.0.0.1:2181

集群方式
bin/zkCli.sh -server 10.11.20.101:2181,10.11.20.103:2181,10.1.226.100:2181

zkCli -server 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
zkCli -server 172.16.48.179:2181,172.16.48.180:2181,172.16.48.181:2181

连接
sh bin/zkCli.sh -server 127.0.0.1:2181

帮助
help

退出
quit


zk客户端命令
ZooKeeper命令行工具类似于Linux的shell环境，不过功能肯定不及shell啦，但是使用它我们可以简单的对ZooKeeper进行访问，数据创建，数据修改等操作.  使用 zkCli.sh -server 127.0.0.1:2181 连接到 ZooKeeper 服务，连接成功后，系统会输出 ZooKeeper 的相关环境以及配置信息。

命令行工具的一些简单操作如下：
1. 显示根目录下、文件： ls / 使用 ls 命令来查看当前 ZooKeeper 中所包含的内容
2. 显示根目录下、文件： ls2 / 查看当前节点数据并能看到更新次数等数据
3. 创建文件，并设置初始内容： create /zk "test" 创建一个新的 znode节点“ zk ”以及与它关联的字符串
4. 获取文件内容： get /zk 确认 znode 是否包含我们所创建的字符串
5. 修改文件内容： set /zk "zkbak" 对 zk 所关联的字符串进行设置
6. 删除文件： delete /zk 将刚才创建的 znode 删除
7. 退出客户端： quit
8. 帮助命令： help



## ZooKeeper常用四字命令

 ZooKeeper 支持某些特定的四字命令字母与其的交互。它们大多是查询命令，用来获取 ZooKeeper 服务的当前状态及相关信息。用户在客户端可以通过 telnet 或 nc 向 ZooKeeper 提交相应的命令

1. 可以通过命令：echo stat|nc 127.0.0.1 2181 来查看哪个节点被选择作为follower或者leader
2. 使用echo ruok|nc 127.0.0.1 2181 测试是否启动了该Server，若回复imok表示已经启动。
3. echo dump| nc 127.0.0.1 2181 ,列出未经处理的会话和临时节点。
4. echo kill | nc 127.0.0.1 2181 ,关掉server
5. echo conf | nc 127.0.0.1 2181 ,输出相关服务配置的详细信息。
6. echo cons | nc 127.0.0.1 2181 ,列出所有连接到服务器的客户端的完全的连接 / 会话的详细信息。
7. echo envi |nc 127.0.0.1 2181 ,输出关于服务环境的详细信息（区别于 conf 命令）。
8. echo reqs | nc 127.0.0.1 2181 ,列出未经处理的请求。
9. echo wchs | nc 127.0.0.1 2181 ,列出服务器 watch 的详细信息。
10. echo wchc | nc 127.0.0.1 2181 ,通过 session 列出服务器 watch 的详细信息，它的输出是一个与 watch 相关的会话的列表。
11. echo wchp | nc 127.0.0.1 2181 ,通过路径列出服务器 watch 的详细信息。它输出一个与 session 相关的路径。



[命令行zkCli.sh使用指南](https://blog.csdn.net/ganglia/article/details/11606807)  
[看完这篇文章，还说自己不会使用Zookeeper命令吗](https://cloud.tencent.com/developer/article/1691455)  
