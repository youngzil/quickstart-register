/**
 * 项目名称：quickstart-etcd 
 * 文件名：Etcd4jTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright youngzil Corporation 2019
 * 版权所有 *
 */
package org.quickstart.etcd.jetcd;

import com.google.api.Property;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;

import static com.google.common.base.Charsets.UTF_8;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
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
public class JetcdTest2 {

    // etcl客户端链接
    private static Client client = null;

    // 链接初始化
    public static synchronized Client getEtclClient() {
        if (null == client) {
            client = Client.builder().endpoints("http://127.0.0.1:2379").build();
        }
        return client;
    }

    /**
     * 根据指定的配置名称获取对应的value
     * 
     * @param key 配置项
     * @return
     * @throws Exception
     */
    public static String getEtcdValueByKey(String key) throws Exception {
        List<KeyValue> kvs = client.getKVClient().get(ByteSequence.from(key.getBytes())).get().getKvs();
        if (kvs.size() > 0) {
            String value = kvs.get(0).getValue().toString();
            return value;
        } else {
            return null;
        }
    }

    /**
     * 新增或者修改指定的配置
     * 
     * @param key
     * @param value
     * @return
     */
    public static void putEtcdValueByKey(String key, String value) throws Exception {
        client.getKVClient().put(ByteSequence.from(key.getBytes()), ByteSequence.from(value.getBytes("utf-8")));

    }

    /**
     * 删除指定的配置
     * 
     * @param key
     * @return
     */
    public static void deleteEtcdValueByKey(String key) {
        client.getKVClient().delete(ByteSequence.from(key.getBytes()));

    }

}
