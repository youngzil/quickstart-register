/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：CuratorTest.java
 * 版本信息：
 * 日期：2017年7月3日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.crud;

import java.nio.charset.Charset;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;

/**
 * CuratorTest
 * 
 * @author：youngzil@163.com
 * @2017年7月3日 下午11:29:17
 * @version 1.0
 */
public class CuratorTest {

    private CuratorFramework zkTools;

    public void curatorDemo() throws Exception {
        zkTools = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").namespace("zk/test").retryPolicy(new RetryNTimes(5, 1000)).connectionTimeoutMs(30000).build();
        zkTools.start();

        zkTools.create()// 创建一个路径
                .creatingParentsIfNeeded()// 如果指定的节点的父节点不存在，递归创建父节点
                .withMode(CreateMode.PERSISTENT)// 存储类型（临时的还是持久的）
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)// 访问权限
                .forPath("/zk/test");// 创建的路径

        zkTools.// 对路径节点赋值
                setData().forPath("/zk/test", "hello world".getBytes(Charset.forName("utf-8")));

        byte[] buffer = zkTools.getData().usingWatcher(new ZKWatch("/zk/test")).forPath("/zk/test");
        System.out.println(new String(buffer));
    }

    public static void main(String[] args) throws Exception {
        CuratorTest test = new CuratorTest();
        test.curatorDemo();

    }

    public class ZKWatch implements CuratorWatcher {
        private final String path;

        public String getPath() {
            return path;
        }

        public ZKWatch(String path) {
            this.path = path;
        }

        @Override
        public void process(WatchedEvent event) throws Exception {
            if (event.getType() == EventType.NodeDataChanged) {
                byte[] data = zkTools.getData().forPath(path);
                System.out.println(path + ":" + new String(data, Charset.forName("utf-8")));
            }
        }

    }

}
