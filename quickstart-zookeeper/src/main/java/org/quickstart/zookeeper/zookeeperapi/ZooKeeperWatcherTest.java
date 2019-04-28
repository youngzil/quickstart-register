/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：dd.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.zookeeperapi;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

/**
 * dd
 * 
 * @author：youngzil@163.com
 * @2017年7月2日 下午8:30:48
 * @version 1.0
 */
public class ZooKeeperWatcherTest {

    private static final int TIME_OUT = 3000;
    private static final String HOST = "localhost:2181";

    private static final String path = "/test/haha";
    private static final String userauth = "/test/haha";
    private static final String userpath = "/test/haha";

    public static void main(String[] args) throws IOException {

        // 创建一个与服务器的连接
        ZooKeeper zookeeper = new ZooKeeper(HOST, TIME_OUT, new Watcher() {
            // 监控所有被触发的事件
            @Override
            public void process(WatchedEvent event) {
                System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型：" + event.getType());
            }
        });

        ZooKeeper zk = new ZooKeeper(HOST, TIME_OUT, null);

        Watcher wh = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("触发回调watcher实例： 路径" + event.getPath() + " 类型：" + event.getType());

                if (event.getType() == EventType.None) {
                    try { //
                          // 判断userauth权限是否能访问userpath
                        String auth_type = "digest";
                        zk.addAuthInfo(auth_type, userauth.getBytes());
                        zk.getData(userpath, null, null);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        System.out.println("get node faild:userpath=" + userpath + ",auth=" + userauth + " e:" + e.getMessage());
                        return;
                    }
                    System.out.println("userpath=" + userpath + " userauth=" + userauth);
                }

                try {

                    // switchinfo = getallswitch(); // 更新userpath和匿名用户路径下的配置信息，监听这些节点

                    // 监听用户路径节点
                    System.out.println("lesson user=" + userpath + " node...");
                    zk.exists(userpath, true); // 监听匿名用户路径节点
                    System.out.println("lesson user=" + userpath + " node...");
                    zk.exists(userpath, true);

                    // 监听用户路径下的开关节点
                    if (zk.exists(userpath, false) != null) {
                        System.out.println("lesson user=" + userpath + " 's swich node...");
                        List<String> swnodes = zk.getChildren(userpath, true); //
                        // 监听switch层节点的变化
                        Iterator<String> it_sw = swnodes.iterator();
                        while (it_sw.hasNext()) {
                            String swpath = userpath + "/" + it_sw.next();
                            System.out.println("lesson user=" + swpath + " node...");
                            zk.exists(swpath, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("lesson znode error:" + e.getMessage());
                }
            }
        };

        while (true) {

        }

    }
}
