/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：AppClient.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.zookeeperapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * AppClient 
 *  
 * @author：youngzil@163.com
 * @2017年7月2日 下午8:49:18 
 * @version 1.0
 */
/**
 * 某分布式系统中，主节点可以有多台，可以动态上下线； 任意一台客户端都能实时感知到主节点服务器的上下线。 Created by tianjun on 2016/12/19 0019.
 */
public class AppClient {
    private String groupNode = "sgroup";
    private ZooKeeper zk = null;
    private Stat stat = new Stat();
    private volatile List<String> serverList = null;

    /**
     * 连接zookeeper
     * 
     * @throws IOException
     */
    public void connectZookeeper() throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("mini04:2181,mini05:2181,mini06:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 如果发生了"/sgroup"节点下子节点变化事件，更新server列表，并重新注册监听
                if (event.getType() == Event.EventType.NodeChildrenChanged && ("/" + groupNode).equals(event.getPath())) {
                    try {
                        updateServerList();
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        updateServerList();
    }

    /**
     * 更新server列表
     */
    private void updateServerList() throws KeeperException, InterruptedException, UnsupportedEncodingException {
        List<String> newServerList = new ArrayList<String>();

        // 获取并监听groupNode子节点变化
        // watch参数为true，表示监听子节点变化事件
        // 每次都需要重新注册监听，因为一次注册，只能监听一次事件，如果还想继续保持监听，必须重新注册
        List<String> subList = zk.getChildren("/" + groupNode, true);
        for (String subNode : subList) {
            byte[] data = zk.getData("/" + groupNode + "/" + subNode, false, stat);
            newServerList.add(new String(data, "utf-8"));
        }

        // 替换server列表
        serverList = newServerList;
        System.out.println("server list updated:" + serverList);
    }

    /**
     * client的工作逻辑写在这个方法中 此处不做任何处理，只让client sleep
     * 
     * @throws InterruptedException
     */
    public void handle() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        AppClient ac = new AppClient();
        ac.connectZookeeper();
        ac.handle();
    }

}
