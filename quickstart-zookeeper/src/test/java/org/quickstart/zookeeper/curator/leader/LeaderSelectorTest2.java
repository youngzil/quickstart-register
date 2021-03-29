package org.quickstart.zookeeper.curator.leader;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderSelectorTest2 {
    static int CLINET_COUNT = 10;
    static String LOCK_PATH = "/leader_selector";

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clientsList = Lists.newArrayListWithCapacity(CLINET_COUNT);
        //启动10个zk客户端，每几秒进行一次leader选举
        for (int i = 0; i < CLINET_COUNT; i++) {
            CuratorFramework client = getZkClient();
            clientsList.add(client);
            ExampleClient exampleClient = new ExampleClient(client, LOCK_PATH, "client_" + i);
            exampleClient.start();
        }
        //sleep 以观察抢主过程
        Thread.sleep(Integer.MAX_VALUE);
    }

    static class ExampleClient extends LeaderSelectorListenerAdapter implements Closeable {
        private final String name;
        private final LeaderSelector leaderSelector;
        private final AtomicInteger leaderCount = new AtomicInteger();

        public ExampleClient(CuratorFramework client, String path, String name) {
            this.name = name;

            leaderSelector = new LeaderSelector(client, path, this);

            // 该方法能让客户端在释放leader权限后 重新加入leader权限的争夺中
            leaderSelector.autoRequeue();
        }

        public void start() throws IOException {
            leaderSelector.start();
        }

        @Override
        public void close() throws IOException {
            leaderSelector.close();
        }

        @Override
        public void takeLeadership(CuratorFramework client) throws Exception {
            // 抢到leader权限后sleep一段时间，并释放leader权限
            final int waitSeconds = (int)(5 * Math.random()) + 1;

            System.out.println(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
            System.out.println(name + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
            } catch (InterruptedException e) {
                System.err.println(name + " was interrupted.");
                Thread.currentThread().interrupt();
            } finally {
                System.out.println(name + " relinquishing leadership.\n");
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

