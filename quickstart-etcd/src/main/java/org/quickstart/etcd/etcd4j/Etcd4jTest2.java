/**
 * 项目名称：quickstart-etcd 
 * 文件名：Etcd4jTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright asiainfo Corporation 2019
 * 版权所有 *
 */
package org.quickstart.etcd.etcd4j;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdVersionResponse;

/**
 * Etcd4jTest
 * 
 * @author：yangzl@asiainfo.com
 * @2019年4月15日 下午9:48:01
 * @since 1.0
 */
public class Etcd4jTest2 {

    public static void main(String[] args) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {

        try (EtcdClient client = new EtcdClient(
                URI.create("http://127.0.0.1:2379"),
                URI.create("http://127.0.0.1:2379"))) {

            // Logs etcd version
            EtcdVersionResponse versionResponse = client.version();
            System.out.println(versionResponse.cluster);
            System.out.println(versionResponse.server);

            // Simple put
            EtcdKeysResponse response = client.put("foo", "bar2").send().get();

            EtcdKeysResponse response2 = client.put("etcd4j_test/foo", "bar").send().get();

            System.out.println(response);
            System.out.println(response.getNode().value);

            // Simple key fetch
            response = client.get("foo").send().get();
            System.out.println(response);
            System.out.println(response.getNode().value);

            System.in.read();

        }

    }

}
