package org.quickstart.zookeeper.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class TestCache {

    @Test
    public void test() throws Exception {
        CuratorFrameworkFactory.Builder builder =
            CuratorFrameworkFactory.builder().connectString("127.0.0.179:2181,127.0.0.180:2181,127.0.0.181:2181")
                .retryPolicy(new ExponentialBackoffRetry(60000, 3, 60000)).namespace("test33");
        CuratorFramework client = builder.build();
        client.start();

        if (!client.blockUntilConnected(60000 * 3, TimeUnit.MILLISECONDS)) {
            client.close();
        }

        TreeCache cache = new TreeCache(client, "/test");
        cache.start();

    }
}
