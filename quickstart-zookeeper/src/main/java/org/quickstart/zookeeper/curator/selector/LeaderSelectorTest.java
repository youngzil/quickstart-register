/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：LeaderSelectorTest.java
 * 版本信息：
 * 日期：2017年7月18日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.selector;

import static org.junit.Assert.*;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Test;

/**
 * LeaderSelectorTest
 * 
 * @author：youngzil@163.com
 * @2017年7月18日 下午11:42:23
 * @version 2.0
 */
public class LeaderSelectorTest {
    @Test
    public void testLeader() throws Exception {

        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("localhost:2181")
                // .namespace("/test1")
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(5000).build();
        curator.start();

        // 原理：内部基于InterProcessMutex实现，具体细节参见shared lock一节
        LeaderSelectorListener listener = new LeaderSelectorListener() {

            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println("i'm leader");
            }

            @Override
            public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
                // TODO Auto-generated method stub

            }
        };
        String leaderPath = "/leader";
        LeaderSelector selector1 = new LeaderSelector(curator, leaderPath, listener);
        selector1.start();
        LeaderSelector selector2 = new LeaderSelector(curator, leaderPath, listener);
        selector2.start();
        assertFalse(selector2.hasLeadership());

        curator.close();
    }
}
