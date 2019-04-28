/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：ZkClientTest.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.zkclient;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * ZkClientTest
 * 
 * @author：youngzil@163.com
 * @2017年7月2日 下午7:53:41
 * @version 1.0
 */
public class ZkClientTest {

    private static final int TIME_OUT = 3000;
    private static final String HOST = "localhost:2181";

    private static final String path = "/test";

    private static final String childrenPath = "/test/haha";

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient(HOST, 2000, 30000);

        ZkClient zkClient2 = new ZkClient(HOST, 2000, 30000, new SerializableSerializer());

        ZkConnection zkConnection = new ZkConnection(HOST);
        ZkClient zkClient3 = new ZkClient(zkConnection);// connect

        String data = "hello world";

        // 一些简单的实例
        // 判断节点是否存在
        if (!zkClient.exists(path)) {
            // 创建节点,必须父节点存在，一级一级创建，
            zkClient.createPersistent(path);
            //zkClient.createPersistent(path,true);//设置createParents参数为true，表明需要递归创建父节点
            
            // 写入数据
            zkClient.writeData(path, data);

            // zkClient.createPersistent(path, data);
            // // 创建子节点
            // zkClient.create(path, data, CreateMode.PERSISTENT);
        }

        // 读取节点数据
        String readData = zkClient.readData(path, false);
        System.out.println(path + "下的数据：" + readData);

        // 获得子节点个数
        int childCount = zkClient.countChildren(path);
        System.out.println(path + "中子节点数为：" + childCount);

        // 获得子节点
        List<String> children = zkClient.getChildren(path);
        if (children.isEmpty()) {
            System.out.println(path + "中没有节点");
        } else {
            System.out.println(path + "中存在节点");
            for (String child : children) {
                System.out.println("节点为：" + child);
            }
        }

        // 删除节点
        zkClient.delete(path);

        zkClient.close();

    }

}
