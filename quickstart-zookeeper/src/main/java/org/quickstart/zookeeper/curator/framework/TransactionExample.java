/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：TransactionExample.java
 * 版本信息：
 * 日期：2017年7月19日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.framework;

import java.util.Collection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;

/**
 * TransactionExample
 * 
 * @author：yangzl@asiainfo.com
 * @2017年7月19日 下午9:34:44
 * @version 2.0
 */
public class TransactionExample {

    public static void main(String[] args) {

    }

    public static Collection<CuratorTransactionResult> transaction(CuratorFramework client) throws Exception {
        // this example shows how to use ZooKeeper's new transactions
        Collection<CuratorTransactionResult> results = client.inTransaction().create().forPath("/a/path", "some data".getBytes()).and().setData().forPath("/another/path", "other data".getBytes())
                .and().delete().forPath("/yet/another/path").and().commit(); // IMPORTANT!
                                                                             // called
        for (CuratorTransactionResult result : results) {
            System.out.println(result.getForPath() + " - " + result.getType());
        }
        return results;
    }

    /*
     * These next four methods show how to use Curator's transaction APIs in a
     * more traditional - one-at-a-time - manner
     */
    public static CuratorTransaction startTransaction(CuratorFramework client) {
        // start the transaction builder
        return client.inTransaction();
    }

    public static CuratorTransactionFinal addCreateToTransaction(CuratorTransaction transaction) throws Exception {
        // add a create operation
        return transaction.create().forPath("/a/path", "some data".getBytes()).and();
    }

    public static CuratorTransactionFinal addDeleteToTransaction(CuratorTransaction transaction) throws Exception {
        // add a delete operation
        return transaction.delete().forPath("/another/path").and();
    }

    public static void commitTransaction(CuratorTransactionFinal transaction) throws Exception {
        // commit the transaction
        transaction.commit();
    }
}
