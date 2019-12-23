/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：InterProcessReadWriteLockTest.java
 * 版本信息：
 * 日期：2017年7月18日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.lock;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Test;

/**
 * InterProcessReadWriteLockTest
 * 
 * @author：youngzil@163.com
 * @2017年7月18日 下午11:38:55
 * @version 2.0
 */
public class InterProcessReadWriteLockTest {

    @Test
    public void testReadWriteLock() throws Exception {

        /**
         * 原理： 同InterProcessMutext，在ephemeral node的排序算法上做trick，write lock的排序在前。
         * 
         * 注意： 同一个InterProcessReadWriteLock如果已经获取了write lock，则获取read lock也会成功
         */

        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("localhost:2181")
                // .namespace("/test1")
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(5000).build();
        curator.start();

        String readWriteLockPath = "/RWLock";
        InterProcessReadWriteLock readWriteLock1 = new InterProcessReadWriteLock(curator, readWriteLockPath);
        InterProcessMutex writeLock1 = readWriteLock1.writeLock();
        InterProcessMutex readLock1 = readWriteLock1.readLock();

        InterProcessReadWriteLock readWriteLock2 = new InterProcessReadWriteLock(curator, readWriteLockPath);
        InterProcessMutex writeLock2 = readWriteLock2.writeLock();
        InterProcessMutex readLock2 = readWriteLock2.readLock();
        writeLock1.acquire();

        // same with WriteLock, can read
        assertTrue(readLock1.acquire(1, TimeUnit.SECONDS));

        // different lock, can't read while writting
        assertFalse(readLock2.acquire(1, TimeUnit.SECONDS));

        // different write lock, can't write
        assertFalse(writeLock2.acquire(1, TimeUnit.SECONDS));

        // release the write lock
        writeLock1.release();

        // both read lock can read
        assertTrue(readLock1.acquire(1, TimeUnit.SECONDS));
        assertTrue(readLock2.acquire(1, TimeUnit.SECONDS));

        curator.close();
    }

}
