/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：ZooKeeperTest.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.zookeeperapi;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeperTest
 * 
 * @author：youngzil@163.com
 * @2017年7月2日 下午7:05:50
 * @version 1.0
 */
public class ZooKeeperTest {

    private static final int TIME_OUT = 30000;
    private static final String HOST = "127.0.0.1:2181";
//    private static final String HOST = "localhost:2181";

    private static final String path = "/test3";

    private static final String childrenPath = "/test/haha";

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper(HOST, TIME_OUT, null);
        System.out.println("=========创建节点===========");

        if (zookeeper.exists(path, false) == null) {
            zookeeper.create(path, "znode1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        System.out.println("=============查看节点是否安装成功===============");
        System.out.println(new String(zookeeper.getData(path, false, null)));

        System.out.println("=========修改节点的数据==========");
        String data = "zNode2";
        zookeeper.setData(path, data.getBytes(), -1);

        System.out.println("========查看修改的节点是否成功=========");
        System.out.println(new String(zookeeper.getData(path, false, null)));

        List<String> list = zookeeper.getChildren(path, false);
        if (list.isEmpty()) {
            System.out.println(path + "中没有节点");
        } else {
            System.out.println(path + "中存在节点");
            for (String child : list) {
                System.out.println("节点为：" + child);
            }
        }

        System.out.println("=======删除节点==========");
        zookeeper.delete(path, -1);

        System.out.println("==========查看节点是否被删除============");
        System.out.println("节点状态：" + zookeeper.exists(path, false));

        //zookeeper.close();

        // 创建一个目录节点
        zookeeper.create("/testRootPath2", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        // 创建一个子目录节点
        zookeeper.create("/testRootPath3/testChildPathOne2", "testChildDataOne".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(new String(zookeeper.getData("/testRootPath", false, null)));
        // 取出子目录节点列表
        System.out.println(zookeeper.getChildren("/testRootPath", true));
        // 修改子目录节点数据
        zookeeper.setData("/testRootPath/testChildPathOne", "modifyChildDataOne".getBytes(), -1);
        System.out.println("目录节点状态：[" + zookeeper.exists("/testRootPath", true) + "]");
        // 创建另外一个子目录节点
        zookeeper.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(new String(zookeeper.getData("/testRootPath/testChildPathTwo", true, null)));
        // 删除子目录节点
        zookeeper.delete("/testRootPath/testChildPathTwo", -1);
        zookeeper.delete("/testRootPath/testChildPathOne", -1);
        // 删除父目录节点
        zookeeper.delete("/testRootPath", -1);
        // 关闭连接
        zookeeper.close();

    }

}
