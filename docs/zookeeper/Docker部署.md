- [单节点安装](#单节点安装)
- [集群方式选择使用docker-compose来完成](#集群部署)



---------------------------------------------------------------------------------------------------------------------
## 单节点安装


拉取镜像
docker pull zookeeper
默认是摘取最新版本 zookeeper:latest。


将它部署在 /usr/local/zookeeper 目录下：
cd /usr/local && mkdir zookeeper && cd zookeeper

创建data目录，用于挂载容器中的数据目录：
mkdir data



cd 部署目录
mkdir -p zookeeper/data
mkdir -p zookeeper/datalog
mkdir -p zookeeper/logs



部署命令：
docker run -d -e TZ="Asia/Shanghai" -p 2181:2181 --name singlezookeeper --restart always -v /Users/lengfeng/zookeeper/data:/data -v /Users/lengfeng/zookeeper/datalog:/datalog -v /Users/lengfeng/zookeeper/logs:/logs zookeeper


docker run -d -e TZ="Asia/Shanghai" -p 2181:2181 -v $PWD/data:/data --name zookeeper --restart always zookeeper


普通的docker部署方式
docker run --name zk1 --net host --restart always -d -v/opt/data/zk1/data:/data -v /opt/data/zk1/conf/zoo.cfg:/conf/zoo.cfg -p 2181:2181 -p 2881:2881 -p 3881:3881 zookeeper


docker run -itd -p 2181:2181 --restart always --name=zookeeper-tmp-server  --privileged=true \
-v /docker/develop/zookeeper/data:/data \
-v /docker/develop/zookeeper/datalog:/datalog \
-v /docker/develop/zookeeper/logs:/logs \
-e "ZOO_STANDALONE_ENABLED=true" \
-e "TZ=Asia/Shanghai" \
-e "ZOO_4LW_COMMANDS_WHITELIST=*" \
-e "ZOO_AUTOPURGE_SNAPRETAINCOUNT=5" \
-e "ZOO_AUTOPURGE_PURGEINTERVAL=24" \
-e "ZOO_MAX_CLIENT_CNXNS=64" \
-e "ZOO_TICK_TIME=2000" \
-e "ZOO_INIT_LIMIT=100" \
-e "ZOO_SYNC_LIMIT=5" \
zookeeper:3.5.6



命令详细说明：
```
-e TZ="Asia/Shanghai" # 指定上海时区
-d # 表示在一直在后台运行容器
-p 2181:2181 # 对端口进行映射，将本地2181端口映射到容器内部的2181端口
--name # 设置创建的容器名称
-v # 将本地目录(文件)挂载到容器指定目录；
--restart always #始终重新启动zookeeper
```


查看容器启动情况：
docker ps -a

注：状态（STATUS）为Up，说明容器已经启动成功。



测试
使用zk命令行客户端连接zk

docker run -it --rm --link singlezookeeper:zookeeper zookeeper zkCli.sh -server zookeeper
说明：-server zookeeper是启动zkCli.sh的参数


ls /
create /zk "test"
get /zk
set /zk "zkbak"
delete /zk
帮助命令： help
退出客户端： quit




$ docker run --name some-app --link some-zookeeper:zookeeper -d application-that-uses-zookeeper






其它命令
```
# 查看zookeeper容器实例进程信息
docker top zookeeper

# 停止zookeeper实例进程
docker stop zookeeper

# 启动zookeeper实例进程
docker start zookeeper

# 重启zookeeper实例进程
docker restart zookeeper

# 查看zookeeper进程日志
docker logs -f zookeeper

# 杀死zookeeper实例进程
docker kill -s KILL zookeeper

# 移除zookeeper实例
docker rm -f -v zookeeper
```



启动容器时可指定的环境变量名	默认值
ZOO_TICK_TIME	2000
ZOO_INIT_LIMIT	5
ZOO_SYNC_LIMIT	2
ZOO_MAX_CLIENT_CNXNS	60
ZOO_STANDALONE_ENABLED	false
ZOO_MY_ID	如果是在复制模式下该参数是必须的，要么在启动容器时，指定此环境变量。要么在数据目录的myid文件里指定
ZOO_SERVERS	如果是在复制模式下该参数是必须的，要么在启动容器时，指定此环境变量。要么在zoo.cfg配置文件里指定 。This variable allows you to specify a list of machines of the Zookeeper ensemble. Each entry has the form of server.id=host:port:port. Entries are separated with space. Do note that this variable will not have any effect if you start the container with a /conf directory that already contains the zoo.cfg file.


---------------------------------------------------------------------------------------------------------------------

## 集群部署

## 集群方式选择使用docker-compose来完成


安装docker-compose
curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose



编写配置文件，并将其命名为：docker-compose.yml（docker-compose默认配置文件名）

此配置文件表示，Docker需要启动三个zookeeper实例，并将2181，2182，2183三个端口号映射到容器内的2181这个端口上。
ZOO_MY_ID：表示zk服务的ID, 取值为1-255之间的整数，且必须唯一
ZOO_SERVERS：表示zk集群的主机列表

端口1用于对client端提供服务，端口2用于选举leader，端口3用于集群内通讯使用(Leader会监听此端口)



启动zookeeper集群
docker-compose up -d
该命令执行需要在docker-compose配置文件的目录下执行

docker-compose -f docker-compose-zk3.4.yml up -d


查看zookeeper集群实例
通过docker ps查看

通过docker-compose ps查看
[root@izbp13xko46hud9vfr5s94z conf]# docker-compose ps

注：这个命令需要在docker-compose配置文件下执行。

docker-compose ps

docker ps -a


管理docker-compose服务
```
# 停止docker-compose服务
docker-compose stop

# 启动docker-compose服务
docker-compose start
# 重启docker-compose服务
docker-compose restart

docker-compose stop
docker-compose rm
```



zkCli -server 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183



查看zookeeper集群节点主从关系
使用docker exec -it zoo1 /bin/bash这个命令进入zoo1节点中，之后输入./bin/zkServer.sh statu来查看节点主从关系

[root@izbp13xko46hud9vfr5s94z conf]# docker exec -it zoo1 /bin/bash
bash-4.4# ./bin/zkServer.sh status



---------------------------------------------------------------------------------------------------------------------


参考  
[Zookeeper——Docker下安装部署](https://my.oschina.net/u/4395639/blog/4073233)  
[Docker下Zookeeper集群搭建](http://jaychang.cn/2018/05/05/Docker%E4%B8%8BZookeeper%E9%9B%86%E7%BE%A4%E6%90%AD%E5%BB%BA/)  
[Linux Centos7 环境搭建Docker部署Zookeeper分布式集群服务实战](https://segmentfault.com/a/1190000021503362)  
[https://hub.docker.com/_/zookeeper](https://hub.docker.com/_/zookeeper)  
[记一个 Docker-Compose 部署 ZooKeeper 集群的坑，集群模式下不可连接](https://juejin.cn/post/6844904181354987528)  


