package org.quickstart.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author youngzil@163.com
 * @description TODO
 * @createTime 2019/12/22 23:21
 */
public class WatchLostTest {

  // Zookeeper之Watcher监听事件丢失
  static String path = "/test/watchlost1";
  static String ZK_HOST = "127.0.0.1";
  static CuratorFramework client =
      CuratorFrameworkFactory.builder()
          .connectString(ZK_HOST)
          .retryPolicy(new ExponentialBackoffRetry(1000, 3))
          .build();

  public static void main(String[] args) {
    try {
      client.start();

      final NodeCache nodeCache = new NodeCache(client, path);
      nodeCache.start();

      if (client.checkExists().forPath(path) == null) {
        client.create().forPath(path, "0".getBytes());
      }

      nodeCache.getListenable().addListener(new NodeCacheListener() {
        @Override
        public void nodeChanged() throws Exception {
          if (nodeCache.getCurrentData() == null) {
            System.out.println("节点被删除");
          } else {
            System.out.println("节点当前内容为：" + new String(nodeCache.getCurrentData().getData()));
          }

        }
      });

      client.setData().forPath(path, "1".getBytes());
      client.setData().forPath(path, "2".getBytes());
      client.setData().forPath(path, "3".getBytes());
      client.setData().forPath(path, "4".getBytes());
      client.setData().forPath(path, "5".getBytes());
      client.setData().forPath(path, "6".getBytes());
      client.setData().forPath(path, "7".getBytes());
      client.setData().forPath(path, "8".getBytes());
      client.setData().forPath(path, "9".getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}