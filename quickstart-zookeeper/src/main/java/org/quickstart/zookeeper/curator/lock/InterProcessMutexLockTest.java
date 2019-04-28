/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：InterProcessMutexLockTest.java
 * 版本信息：
 * 日期：2017年7月18日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.lock;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

/**
 * InterProcessMutexLockTest
 * 
 * @author：youngzil@163.com
 * @2017年7月18日 下午11:28:53
 * @version 2.0
 */
public class InterProcessMutexLockTest {

    /**
     * 原理：每次调用acquire在/lock1节点节点下使用CreateMode.EPHEMERAL_SEQUENTIAL 创建新的ephemeral节点，然后getChildren获取所有的children，判断刚刚创建的临时节点是否为第一个，如果是，则获取锁成功；如果不是，则删除刚刚创建的临时节点。
     * 
     * 注意： 每次accquire操作，成功，则请求zk server 2次（一次写，一次getChildren）；如果失败，则请求zk server 3次（一次写，一次getChildren，一次delete） main
     * 
     * @Description: void
     * @param args
     * @throws Exception
     * @Exception
     * @author：youngzil@163.com
     * @2017年7月18日 下午11:38:30
     */

    public static void main(String[] args) throws Exception {

        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("localhost:2181")
                // .namespace("/test1")
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(5000).build();
        curator.start();

        String lockName = "/lock1";
        InterProcessLock lock1 = new InterProcessMutex(curator, lockName);
        InterProcessLock lock2 = new InterProcessMutex(curator, lockName);
        lock1.acquire();
        boolean result = lock2.acquire(1, TimeUnit.SECONDS);
        System.out.println(result);
        lock1.release();
        result = lock2.acquire(1, TimeUnit.SECONDS);
        System.out.println(result);

        curator.close();
    }

}
