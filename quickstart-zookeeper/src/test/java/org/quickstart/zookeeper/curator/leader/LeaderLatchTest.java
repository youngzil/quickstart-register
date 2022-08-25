package org.quickstart.zookeeper.curator.leader;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LeaderLatchTest {
    static int CLINET_COUNT = 10;
    static String LOCK_PATH = "/leader_latch";

    @Test
    public void testLeaderLatch() throws Exception {
        List<CuratorFramework> clientsList = Lists.newArrayListWithCapacity(CLINET_COUNT);
        List<LeaderLatch> leaderLatchList = Lists.newArrayListWithCapacity(CLINET_COUNT);

        //创建10个zk客户端模拟leader选举
        for (int i = 0; i < CLINET_COUNT; i++) {
            CuratorFramework client = getZkClient();
            clientsList.add(client);

            System.out.println("LeaderLatch" + i);

            LeaderLatch leaderLatch = new LeaderLatch(client, LOCK_PATH, "CLIENT_" + i);
            leaderLatchList.add(leaderLatch);

            new Thread(() -> {
                //必须调用start()方法来进行抢主
                try {
                    leaderLatch.start();
                    leaderLatch.await();
                    System.out.println(leaderLatch.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        System.in.read();

    }

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clientsList = Lists.newArrayListWithCapacity(CLINET_COUNT);
        List<LeaderLatch> leaderLatchList = Lists.newArrayListWithCapacity(CLINET_COUNT);
        //创建10个zk客户端模拟leader选举
        for (int i = 0; i < CLINET_COUNT; i++) {
            CuratorFramework client = getZkClient();
            clientsList.add(client);
            LeaderLatch leaderLatch = new LeaderLatch(client, LOCK_PATH, "CLIENT_" + i);
            leaderLatchList.add(leaderLatch);
            //必须调用start()方法来进行抢主
            leaderLatch.start();
        }
        //判断当前leader是哪个客户端
        checkLeader(leaderLatchList);

    }

    private static void checkLeader(List<LeaderLatch> leaderLatchList) throws Exception {
        //Leader选举需要时间 等待10秒
        Thread.sleep(10000);
        for (int i = 0; i < leaderLatchList.size(); i++) {
            LeaderLatch leaderLatch = leaderLatchList.get(i);
            //通过hasLeadership()方法判断当前节点是否是leader
            if (leaderLatch.hasLeadership()) {
                System.out.println("当前leader:" + leaderLatch.getId());
                //释放leader权限 重新进行抢主
                // leaderLatch.close();
                checkLeader(leaderLatchList);
            }
        }
    }

    private static CuratorFramework getZkClient() {
        String zkServerAddress = "127.0.0.1:2182,127.0.0.1:2183,127.0.0.1:2184";
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, 5000);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()//
            .connectString(zkServerAddress)//
            .sessionTimeoutMs(5000)//
            .connectionTimeoutMs(5000)//
            .retryPolicy(retryPolicy)//
            .build();
        zkClient.start();
        return zkClient;
    }

}

