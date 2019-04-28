/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：TransactionExamples.java
 * 版本信息：
 * 日期：2017年7月3日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.transaction;

import java.util.Collection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

/**
 * TransactionExamples 事务操作
 * 
 * @author：youngzil@163.com
 * @2017年7月3日 下午10:16:26
 * @version 1.0
 */
public class TransactionExamples {

    private static CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").sessionTimeoutMs(30000).connectionTimeoutMs(30000).canBeReadOnly(false)
            .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
            // .namespace("mynamespace")
            .defaultData(null).build();

    public static void main(String[] args) {
        try {
            client.start();
            // 开启事务
            CuratorTransaction transaction = client.inTransaction();

            Collection<CuratorTransactionResult> results =
                    transaction.create().forPath("/one", "some data".getBytes()).and().create().forPath("/another").and().setData().forPath("/another", "other data".getBytes()).and().create()
                            .forPath("/yet").and().delete().forPath("/yet").and().delete().forPath("/one").and().delete().forPath("/another").and().commit();

            for (CuratorTransactionResult result : results) {
                System.out.println(result.getForPath() + " - " + result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放客户端连接
            CloseableUtils.closeQuietly(client);
        }

    }

}
