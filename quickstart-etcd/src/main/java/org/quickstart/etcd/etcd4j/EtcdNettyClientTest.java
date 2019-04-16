/**
 * 项目名称：quickstart-etcd 
 * 文件名：EtcdNettyClientTest.java
 * 版本信息：
 * 日期：2019年4月16日
 * Copyright asiainfo Corporation 2019
 * 版权所有 *
 */
package org.quickstart.etcd.etcd4j;

import java.net.URI;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.transport.EtcdNettyClient;
import mousio.etcd4j.transport.EtcdNettyConfig;

/**
 * EtcdNettyClientTest 
 *  
 * @author：yangzl@asiainfo.com
 * @2019年4月16日 下午3:42:56 
 * @since 1.0
 */
public class EtcdNettyClientTest {
    public static void main(String[] args) {
        /* EtcdNettyConfig config = new EtcdNettyConfig()
                .setConnectTimeout(100)
                .setHostName("www.example.net")
                .setEventLoopGroup(customEventLoop);
                // .setEventLoopGroup(customEventLoop, false); // don't close event loop group when etcd client close
        
            nettySslContext
            try(EtcdClient etcd = new EtcdClient(new EtcdNettyClient(config, sslContext, URI.create(uri)))){
              // Use etcd client here
            }*/
    }

}
