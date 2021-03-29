package org.quickstart.zookeeper.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.TimeUnit;

public class InterProcessSemaphoreMutexTest {

    public static void main(String[] args) throws Exception {

        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("localhost:2181")
            // .namespace("/test1")
            .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(5000).build();
        curator.start();

        String lockName = "/lock1";
        InterProcessLock lock1 = new InterProcessSemaphoreMutex(curator, lockName);
        InterProcessLock lock2 = new InterProcessSemaphoreMutex(curator, lockName);
        lock1.acquire();
        boolean result = lock2.acquire(1, TimeUnit.SECONDS);
        System.out.println(result);
        lock1.release();
        result = lock2.acquire(1, TimeUnit.SECONDS);
        System.out.println(result);

        curator.close();
    }

}
