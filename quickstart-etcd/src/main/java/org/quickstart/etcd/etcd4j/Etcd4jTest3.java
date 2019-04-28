/**
 * 项目名称：quickstart-etcd 
 * 文件名：Etcd4jTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright asiainfo Corporation 2019
 * 版权所有 *
 */
package org.quickstart.etcd.etcd4j;

import com.google.api.Property;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;

/**
 * Etcd4jTest
 * 
 * @author：youngzil@163.com
 * @2019年4月15日 下午9:48:01
 * @since 1.0
 */
public class Etcd4jTest3 {

    public static void main(String[] args) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {

        final String serverName = "roomServer"; // 自定义的服务名字，我定义成roomServer
        final String dirString = "/roomServerList";
        final String zoneId = "1"; // 自定义的一个标识，我定义成1
        final String etcdKey = dirString + "/" + zoneId + "/" + serverName; // 这里就是发布的节点
        int nodeCount = 3;

        URI[] uris = new URI[nodeCount]; // 对于集群，把所有集群节点地址加进来，etcd的代码里会轮询这些地址来发布节点，直到成功
        for (int iter = 0; iter < nodeCount; iter++) {
            String urlString = "etcdHost" + new Integer(iter).toString();
            System.out.println(urlString);
            uris[iter] = URI.create(urlString);
        }
        EtcdClient client = new EtcdClient(uris);
        client.setRetryHandler(new RetryOnce(20)); // retry策略

        // String dir = etcdKey + "_" + Property.getProperty("serverIp") + "_" + Property.getProperty("serverPort");
        String dir = etcdKey + "_" + "127.0.0.1" + "_" + "8081";

        // 注册节点，放在程序启动的入口
        // 用put方法发布一个节点
        EtcdResponsePromise<EtcdKeysResponse> p1 = client
                .putDir(dir)
                .ttl(60).send();
        p1.get(); // 加上这个get()用来保证设置完成，走下一步，get会阻塞，由上面client的retry策略决定阻塞的方式

        // 删除
        EtcdResponsePromise<EtcdKeysResponse> p2 = client
                .deleteDir(dir)
                .recursive().send();
        p2.get();

        client.refresh(dir, 60).send();// 启动一个守护线程来定时刷新节点

        client.close();

    }

}
