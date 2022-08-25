package org.quickstart.zookeeper.curator.framework;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;

/**
 * 增删改查
 * 
 * @author shencl
 */
public class CrudExamples {
    private static CuratorFramework client = ClientFactory.newClient();
    private static final String PATH = "/crud";

    public static void main(String[] args) {
        try {

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3, 30000);
            CuratorFramework client2 = CuratorFrameworkFactory.newClient("172.16.48.179:2181,172.16.48.180:2181,172.16.48.181:2181/kfk1", 30000, 3000, retryPolicy);
            client2.start();

            client2.delete().deletingChildrenIfNeeded().forPath("/offsets");


            client.start();

            // client.create().forPath(PATH, "I love messi".getBytes());


            client.delete().deletingChildrenIfNeeded().forPath("/crud");
            // client.delete().forPath(PATH);



            // byte[] bs = client.getData().forPath(PATH);
            // System.out.println("新建的节点，data为:" + new String(bs));

            // client.setData().forPath(PATH, "I love football".getBytes());

            // 由于是在background模式下获取的data，此时的bs可能为null
            // byte[] bs2 = client.getData().watched().inBackground().forPath(PATH);
            // System.out.println("修改后的data为" + new String(bs2 != null ? bs2 : new byte[0]));

            // client.delete().forPath(PATH);
            // Stat stat = client.checkExists().forPath(PATH);

            // Stat就是对zonde所有属性的一个映射， stat=null表示节点不存在！
            // System.out.println(stat);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
