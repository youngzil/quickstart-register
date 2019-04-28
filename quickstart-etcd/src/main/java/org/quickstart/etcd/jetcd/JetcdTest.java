/**
 * 项目名称：quickstart-etcd 
 * 文件名：JetcdTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright asiainfo Corporation 2019
 * 版权所有 *
 */
package org.quickstart.etcd.jetcd;

import static com.google.common.base.Charsets.UTF_8;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

/**
 * JetcdTest
 * 
 * @author：youngzil@163.com
 * @2019年4月15日 下午9:25:17
 * @since 1.0
 */
public class JetcdTest {

    static Client client = Client.builder().endpoints("http://localhost:2379").build();

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // read current ip;
        // String machineIp = getMachineIp();
        // create client;
        // client = Client.builder().endpoints("http://" + machineIp + port).build();
        // Client client = Client.builder().endpoints("http://" + "127.0.0.1:2379").build();

        // create client
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient();

        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // put the key-value
        PutResponse putResponse = kvClient.put(key, value).get();
        System.out.println("putResponse=" + putResponse.toString());
        System.out.println("------------------------");

        // get the CompletableFuture
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        // get the value from CompletableFuture
        GetResponse getResponse = getFuture.get();
        System.out.println("getResponse=" + getResponse.toString());
        System.out.println("------------------------");
        // delete the key
        // kvClient.delete(key).get();

    }

    /**
     * get single etcdKey from etcd; 从Etcd获取单个key
     *
     * @param key etcdKey
     * @return etcdKey and value 's instance
     */
    public static KeyValue getEtcdKey(String key) {
        KeyValue keyValue = null;
        try {
            keyValue = client.getKVClient().get(ByteSequence.from(key.getBytes())).get().getKvs().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyValue;
    }

    /**
     * get all etcdKey with this prefix 从Etcd获取满足前缀的所有key
     *
     * @param prefix etcdKey's prefix
     * @return all etcdKey with this prefix
     */
    public static List<KeyValue> getEtcdKeyWithPrefix(String prefix) {
        List<KeyValue> keyValues = new ArrayList<>();
        GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.from(prefix.getBytes())).build();
        try {
            keyValues = client.getKVClient().get(ByteSequence.from(prefix.getBytes()), getOption).get().getKvs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyValues;
    }

    /**
     * put single etcdKey 将单个key放入Etcd中
     *
     * @param key single etcdKey
     * @param value etcdKey's value
     */
    public static void putEtcdKey(String key, String value) {
        client.getKVClient().put(ByteSequence.from(key.getBytes()), ByteSequence.from(value.getBytes()));
    }

    /**
     * put single etcdKey with a expire time (by etcd lease) 将一个有过期时间的key放入Etcd，通过lease机制
     *
     * @param key single etcdKey
     * @param value etcdKey's value
     * @param expireTime expire time (s) 过期时间，单位秒
     * @return lease id 租约id
     */
    public static long putEtcdKeyWithExpireTime(String key, String value, long expireTime) {
        CompletableFuture<LeaseGrantResponse> leaseGrantResponse = client.getLeaseClient().grant(expireTime);
        PutOption putOption;
        try {
            putOption = PutOption.newBuilder().withLeaseId(leaseGrantResponse.get().getID()).build();
            client.getKVClient().put(ByteSequence.from(key.getBytes()), ByteSequence.from(value.getBytes()), putOption);
            return leaseGrantResponse.get().getID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * put single etcdKey with a lease id 将一个key绑定指定的租约放入到Etcd。
     * 
     * @param key single etcdKey
     * @param value etcdKey's value
     * @param leaseId lease id 租约id
     * @return revision id if exception return 0L
     */
    public static long putEtcdKeyWithLeaseId(String key, String value, long leaseId) throws Exception {
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        CompletableFuture<PutResponse> putResponse = client.getKVClient().put(ByteSequence.from(key.getBytes()), ByteSequence.from(value.getBytes()), putOption);
        try {
            return putResponse.get().getHeader().getRevision();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * keep alive for a single lease
     * 
     * @param leaseId lease id 租约Id
     */
    public static void keepAliveEtcdSingleLease(long leaseId) {
        // client.getLeaseClient().keepAlive(leaseId, observer)
        //
        // EtcdUtil.getEtclClient().getLeaseClient().keepAlive(leaseId);
    }

    /**
     * delete single etcdKey 从Etcd中删除单个key
     *
     * @param key etcdKey
     */
    public static void deleteEtcdKey(String key) {
        client.getKVClient().delete(ByteSequence.from(key.getBytes()));
    }

    /**
     * delete all key with prefix 从Etcd中删除所有满足前缀匹配的key
     *
     * @param prefix etcdKey's prefix
     */
    public static void deleteEtcdKeyWithPrefix(String prefix) {
        DeleteOption deleteOption = DeleteOption.newBuilder().withPrefix(ByteSequence.from(prefix.getBytes())).build();
        client.getKVClient().delete(ByteSequence.from(prefix.getBytes()), deleteOption);
    }

    /**
     * get single etcdKey's custom watcher 得到一个单个key的自定义观察者
     *
     * @param key etcdKey
     * @return single etcdKey's custom watcher
     */
    public static Watch.Watcher getCustomWatcherForSingleKey(String key) {
        // client.getWatchClient().watch(key, listener)
        // return EtcdUtil.getEtclClient().getWatchClient().watch(ByteSequence.fromString(key));

        Watch.Listener listener = Watch.listener(response -> {
            System.out.println("Watching for key=" + key);

            for (WatchEvent event : response.getEvents()) {

                String keyString = Optional.ofNullable(event.getKeyValue().getKey())
                        .map(bs -> bs.toString(UTF_8))
                        .orElse("");
                String valueString = Optional.ofNullable(event.getKeyValue().getValue())
                        .map(bs -> bs.toString(UTF_8))
                        .orElse("");
                System.out.println("type=" + event.getEventType() + ", key=" + keyString + ", value=" + valueString);
            }

        });

        Watch watch = client.getWatchClient();
        Watch.Watcher watcher = watch.watch(ByteSequence.from(key.getBytes()), listener);
        return watcher;
    }

    /**
     * get a watcher who watch all etcdKeys with prefix 得到一个满足所有前缀匹配的key集合的自定义观察者
     *
     * @param prefix etcdKey's prefix
     * @return a watcher who watch all etcdKeys with prefix
     */
    // public static Watch.Watcher getCustomWatcherForPrefix(String prefix) {
    // WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.from(prefix.getBytes())).build();
    // return EtcdUtil.getEtclClient().getWatchClient().watch(ByteSequence.from(prefix.getBytes()), watchOption);
    // }

}
