/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：DistributedAtomicLongExample.java
 * 版本信息：
 * 日期：2017年7月19日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.counter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;

import com.google.common.collect.Lists;

/**
 * DistributedAtomicLongExample
 * 
 * @author：youngzil@163.com
 * @2017年7月19日 下午9:25:59
 * @version 2.0
 */
public class DistributedAtomicLongExample {
    private static final int QTY = 5;
    private static final String PATH = "/examples/counter";

    public static void main(String[] args) throws IOException, Exception {
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            List<DistributedAtomicLong> examples = Lists.newArrayList();
            ExecutorService service = Executors.newFixedThreadPool(QTY);
            for (int i = 0; i < QTY; ++i) {
                final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));

                examples.add(count);
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            // Thread.sleep(rand.nextInt(1000));
                            AtomicValue<Long> value = count.increment();
                            // AtomicValue<Long> value = count.decrement();
                            // AtomicValue<Long> value = count.add((long)rand.nextInt(20));
                            System.out.println("succeed: " + value.succeeded());
                            if (value.succeeded())
                                System.out.println("Increment: from " + value.preValue() + " to " + value.postValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                };
                service.submit(task);
            }

            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        }

    }

}
