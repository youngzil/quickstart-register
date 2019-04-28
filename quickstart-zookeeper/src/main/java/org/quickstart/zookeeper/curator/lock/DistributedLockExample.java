/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：DistributedLockExample.java
 * 版本信息：
 * 日期：2017年7月3日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.lock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.utils.CloseableUtils;

/**
 * DistributedLockExample 分布式锁实例
 * 
 * @author：youngzil@163.com
 * @2017年7月3日 下午10:46:38
 * @version 1.0
 */
public class DistributedLockExample {

    private static CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").sessionTimeoutMs(30000).connectionTimeoutMs(30000).canBeReadOnly(false)
            .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
            // .namespace("mynamespace")
            .defaultData(null).build();
    private static final String PATH = "/locks";

    // 进程内部（可重入）读写锁
    private static final InterProcessReadWriteLock lock;
    // 读锁
    private static final InterProcessLock readLock;
    // 写锁
    private static final InterProcessLock writeLock;

    static {
        client.start();
        lock = new InterProcessReadWriteLock(client, PATH);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public static void main(String[] args) {
        try {
            List<Thread> jobs = Lists.newArrayList();
            for (int i = 0; i < 10; i++) {
                Thread t = new Thread(new ParallelJob("Parallel任务" + i, readLock));
                jobs.add(t);
            }

            for (int i = 0; i < 10; i++) {
                Thread t = new Thread(new MutexJob("Mutex任务" + i, writeLock));
                jobs.add(t);
            }

            for (Thread t : jobs) {
                t.start();
            }

            // 这里有个小技巧，让main程序一直监听控制台输入，异步的代码就可以一直在执行。不同于while(ture)的是，按回车或esc可退出
            System.out.println("Press enter/return to quit");
            new BufferedReader(new InputStreamReader(System.in)).readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

}
