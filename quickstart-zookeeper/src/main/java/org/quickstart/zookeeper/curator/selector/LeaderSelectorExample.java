/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：RecipesExample.java
 * 版本信息：
 * 日期：2017年7月3日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.selector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.utils.CloseableUtils;

/**
 * RecipesExample Curator宣称，Recipes模块实现了除二阶段提交之外的所有zookeeper特性。 主要有Elections(选举)，Locks（锁），Barriers（关卡），Atomic（原子量），Caches，Queues等 选举主要依赖于LeaderSelector和LeaderLatch2个类。 前者是所有存活的客户端不间断的轮流做Leader，大同社会。
 * 后者是一旦选举出Leader，除非有客户端挂掉重新触发选举，否则不会交出领导权。某党？
 * 
 * @author：youngzil@163.com
 * @2017年7月3日 下午10:31:46
 * @version 1.0
 */
public class LeaderSelectorExample {

    public static void main(String[] args) {

        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderSelectorClient> examples = Lists.newArrayList();
        try {
            for (int i = 0; i < 10; i++) {

                CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").sessionTimeoutMs(30000).connectionTimeoutMs(30000).canBeReadOnly(false)
                        .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                        // .namespace("mynamespace")
                        .defaultData(null).build();

                LeaderSelectorClient example = new LeaderSelectorClient(client, "Client #" + i);
                clients.add(client);
                examples.add(example);

                client.start();
                example.start();
            }

            System.out.println("----------先观察一会选举的结果-----------");
            Thread.sleep(10000);

            System.out.println("----------关闭前5个客户端，再观察选举的结果-----------");
            for (int i = 0; i < 5; i++) {
                clients.get(i).close();
            }

            // 这里有个小技巧，让main程序一直监听控制台输入，异步的代码就可以一直在执行。不同于while(ture)的是，按回车或esc可退出
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (LeaderSelectorClient exampleClient : examples) {
                CloseableUtils.closeQuietly(exampleClient);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }

}
