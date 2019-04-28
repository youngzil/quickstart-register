/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：DistributedBarrierExample.java
 * 版本信息：
 * 日期：2017年7月19日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.barrier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * DistributedBarrierExample 1.DistributedBarrier类说明 DistributedBarrier类实现了栅栏的功能。它的构造函数如下：
 * 
 * @param client client
 * @param barrierPath path to use as the barrier
 *
 *        public DistributedBarrier(CuratorFramework client, String barrierPath) DistributedBarrier构造函数中barrierPath参数用来确定一个栅栏，只要barrierPath参数相同(路径相同)就是同一个栅栏。通常情况下栅栏的使用如下： 1.主导client设置一个栅栏
 *        2.其他客户端就会调用waitOnBarrier()等待栅栏移除，程序处理线程阻塞 3.主导client移除栅栏，其他客户端的处理程序就会同时继续运行。 DistributedBarrier类的主要方法如下： setBarrier() - 设置栅栏 waitOnBarrier() - 等待栅栏移除 removeBarrier() - 移除栅栏
 *        异常处理：DistributedBarrier会监控连接状态，当连接断掉时waitOnBarrier()方法会抛出异常。
 * @author：youngzil@163.com
 * @2017年7月19日 上午8:36:14
 * @version 2.0
 */
public class DistributedBarrierExample {
    private static final int QTY = 5;
    private static final String PATH = "/examples/barrier";

    public static void main(String[] args) throws Exception {
        /**
         * 这个例子创建了controlBarrier来设置栅栏和移除栅栏。我们创建了5个线程，在此Barrier上等待。最后移除栅栏后所有的线程才继续执行。 如果你开始不设置栅栏，所有的线程就不会阻塞住。
         */
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        ExecutorService service = Executors.newFixedThreadPool(QTY);
        DistributedBarrier controlBarrier = new DistributedBarrier(client, PATH);
        controlBarrier.setBarrier();
        for (int i = 0; i < QTY; ++i) {
            final DistributedBarrier barrier = new DistributedBarrier(client, PATH);
            final int index = i;
            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Thread.sleep((long) (3 * Math.random()));
                    System.out.println("Client #" + index + " 等待");
                    barrier.waitOnBarrier();
                    System.out.println("Client #" + index + " 开始");
                    return null;
                }
            };
            service.submit(task);
        }
        Thread.sleep(1000 * 3);
        System.out.println("所有的Client都在等待");
        controlBarrier.removeBarrier();
        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);
        client.close();
        System.out.println("OK!");
    }
}
