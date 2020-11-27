package org.quickstart.zookeeper.curator.masterslave;

import java.io.IOException;
import java.util.List;
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
public class MasterRegister2 extends Thread {

  private static final String ROOT_PATH = "/test";

  private static final Long WAIT_SECONDS = 3L;

  public volatile boolean master = false;

  public String masterPort;

  private CuratorFramework zkClient;

  private static final String ZOOKEEPER_URL = "127.0.0.1:2181";

  private String port;

  public MasterRegister2(String port) {
    this.port = port;
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    this.zkClient = CuratorFrameworkFactory.newClient(ZOOKEEPER_URL, retryPolicy);
    this.zkClient.start();
  }

  @Override
  public void run() {
    //Spring 容器启动后创建临时节点
    regist();
  }

  private void regist() {
    try {
      zkClient.create()
          .creatingParentsIfNeeded()
          .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
          .forPath(ROOT_PATH + "/master", port.getBytes());
    } catch (Exception ignored) {
    }
  }

  private void printMaster() throws Exception {
    //todo 缓存优化-监听变化
    List<String> list = this.zkClient.getChildren().forPath(ROOT_PATH);
    String s = list.stream().sorted(String.CASE_INSENSITIVE_ORDER).findFirst().get();
    System.out.println("Master is " + s);
  }


  public static void main(String[] args) throws Exception {

    for (int i = 0; i < 3; i++) {
      MasterRegister2 masterRegister2 = new MasterRegister2("2" + i);
      masterRegister2.run();
      masterRegister2.printMaster();
    }

    System.in.read();
  }

}
