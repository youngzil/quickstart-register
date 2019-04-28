/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：AppServer.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.zookeeperapi;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * AppServer 
 *  
 * @author：youngzil@163.com
 * @2017年7月2日 下午8:50:01 
 * @version 1.0
 */
/**
 * 某分布式系统中，主节点可以有多台，可以动态上下线； 任意一台客户端都能实时感知到主节点服务器的上下线。 Created by tianjun on 2016/12/19 0019.
 */
public class AppServer {

    private String groupNode = "sgroup";

    private String subNode = "sub";

    /**
     * 链接zookeeper
     * 
     * @param address
     * @throws IOException
     */
    public void connectZookeeper(String address) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("mini04:2181,mini05:2181,mini06:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 不做处理
            }
        });
        // 在"/sgroup"下创建子节点
        // 子节点类型设置为EPHEMERAL_SEQUENTIAL，表明这是一个临时节点，且在子节点的名称后面加上一串数字后缀
        // 将server的地址数据关联到新创建的子节点上
        String createPath = zk.create("/" + groupNode + "/" + subNode, address.getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("create:" + createPath);
    }

    /**
     * server的工作逻辑写在这个方法中
     * 
     * @throws InterruptedException
     */
    public void handle() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        
        ZooKeeper zk = new ZooKeeper("localhost:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 不做处理
            }
        });
        // 在"/sgroup"下创建子节点
        // 子节点类型设置为EPHEMERAL_SEQUENTIAL，表明这是一个临时节点，且在子节点的名称后面加上一串数字后缀
        // 将server的地址数据关联到新创建的子节点上
        String createPath = zk.create("/" + "sgroup" + "/" + "child", "sss".getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("create:" + createPath);
        
        if (args.length == 0) {
            System.err.println("the first argument must be server address");
            System.exit(1);
        }

        AppServer as = new AppServer();
        as.connectZookeeper(args[0]);
        as.handle();
    }

}
