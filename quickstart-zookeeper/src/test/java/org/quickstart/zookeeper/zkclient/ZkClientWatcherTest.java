/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：ZkClientTest.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright youngzil Corporation 2017
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
public class ZkClientWatcherTest {

    private static final int TIME_OUT = 3000;
    private static final String HOST = "localhost:2181";

    private static final String path = "/test";

    private static final String childrenPath = "/test/haha";

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient(HOST, 2000, 30000);

        ZkClient zkClient2 = new ZkClient(HOST, 2000, 30000, new SerializableSerializer());

        ZkConnection zkConnection = new ZkConnection(HOST);
        ZkClient zkClient3 = new ZkClient(zkConnection);// connect

        // 订阅子节点变化
        zkClient2.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("clildren of path " + parentPath + ":" + currentChilds);

            }
        });
        // 订阅数据变化
        zkClient2.subscribeDataChanges(path, new IZkDataListener() {

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Data of " + dataPath + " has changed");
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println(dataPath + " has deleted");
            }

        });

        // 订阅连接数变化
        zkClient2.subscribeStateChanges(new IZkStateListener() {

            @Override
            public void handleNewSession() throws Exception {
                System.out.println("handleNewSession()");
            }

            @Override
            public void handleStateChanged(KeeperState stat) throws Exception {
                System.out.println("handleStateChanged,stat:" + stat);
            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) throws Exception {
                System.out.println("handleSessionEstablishmentError,stat:" + error.getMessage());

            }
        });

        while (true) {

        }
    }

}
