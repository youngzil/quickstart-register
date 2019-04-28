/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：CrudExample.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.crud;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;

/**
 * CrudExample
 * 
 * @author：youngzil@163.com
 * @2017年7月2日 下午8:55:39
 * @version 1.0
 */
public class CuratorExample {

    private static final String PATH = "/crud";

    public static void main(String[] args) {

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").sessionTimeoutMs(30000).connectionTimeoutMs(30000).canBeReadOnly(false)
                .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                // .namespace("mynamespace")
                .defaultData(null).build();

        // CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181")
        // .retryPolicy(new RetryNTimes(3000, 3000))
        // .connectionTimeoutMs(3000).build();

        /**
         * 常用接口有 create()增 delete(): 删 checkExists(): 判断是否存在 setData(): 改 getData(): 查 所有这些方法都以forpath()结尾，辅以watch(监听)，withMode（指定模式），和inBackground（后台运行）等方法来使用。
         */

        try {
            client.start();

            client.create().forPath(PATH, "I love messi".getBytes());

            byte[] bs = client.getData().forPath(PATH);
            System.out.println("新建的节点，data为:" + new String(bs));

            client.setData().forPath(PATH, "I love football".getBytes());

            // 由于是在background模式下获取的data，此时的bs可能为null
            byte[] bs2 = client.getData().watched().inBackground().forPath(PATH);
            System.out.println("修改后的data为" + new String(bs2 != null ? bs2 : new byte[0]));

            byte[] bs3 = client.getData().forPath(PATH);
            System.out.println("修改后的节点，data为:" + new String(bs3));

            client.delete().forPath(PATH);
            Stat stat = client.checkExists().forPath(PATH);

            // Stat就是对zonde所有属性的一个映射， stat=null表示节点不存在！
            System.out.println(stat);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }

    }

}
