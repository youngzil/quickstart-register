package org.quickstart.zookeeper.curator.masterslave;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * <p>描述: [功能描述] </p >
 *
 * https://blog.csdn.net/Dongguabai/article/details/105940409
 *
 * @author yangzl
 * @version v1.0
 * @date 2020/11/11 09:58
 */
public class MasterRegister extends Thread {

  private static final String ROOT_PATH = "/test";

  private static final Long WAIT_SECONDS = 3L;

  public volatile boolean master = false;

  private CuratorFramework zkClient;

  private static final String ZOOKEEPER_URL = "127.0.0.1:2181";

  private String port;

  public MasterRegister(String port) {
    this.port = port;
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    this.zkClient = CuratorFrameworkFactory.newClient(ZOOKEEPER_URL, retryPolicy);
    this.zkClient.start();
  }

  @SneakyThrows
  @Override
  public void run() {

    //Spring 容器启动后创建临时节点
    //由于在ZooKeeper中规定了所有非叶子节点必须为持久节点，调用上面这个API之后，只有path参数对应的数据节点是临时节点，其父节点均为持久节点
    regist();
    PathChildrenCache childrenCache = new PathChildrenCache(zkClient, ROOT_PATH, true);
    childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    // 节点数据change事件的通知方法
    childrenCache.getListenable().addListener((curatorFramework, event) -> {
      if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
        System.out.println("节点变更，开始重新选举");
        //todo：防止脑裂/防止网络抖动/数据同步
        try {
          TimeUnit.SECONDS.sleep(WAIT_SECONDS);
        } catch (InterruptedException ignored) {
        }
        regist();
      }
    });
  }

  private void regist() {
    System.out.printf("机器【%s】开始抢占 Master", port);
    System.out.println();
    try {
      zkClient.create()
          .creatingParentsIfNeeded()
          .withMode(CreateMode.EPHEMERAL)
          .forPath(ROOT_PATH + "/master", port.getBytes());
      System.out.printf("机器【%s】成为了 Master", port);
      System.out.println();
      master = true;
    } catch (Exception e) {
      System.out.printf("机器【%s】抢占 Master 失败", port);
      System.out.println();
      master = false;
    }
  }


  public static void main(String[] args) throws IOException {

    for (int i = 0; i < 3; i++) {
      MasterRegister masterRegister = new MasterRegister("2" + i);
      masterRegister.run();
    }
    System.in.read();
  }

}
