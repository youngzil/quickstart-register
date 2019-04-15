/**
 * 项目名称：quickstart-consul 
 * 文件名：KeyValueTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright asiainfo Corporation 2019
 * 版权所有 *
 */
package org.quickstart.consul.orbitz;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.async.ConsulResponseCallback;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.QueryOptions;

/**
 * KeyValueTest
 * 
 * @author：yangzl@asiainfo.com
 * @2019年4月15日 上午10:45:42
 * @since 1.0
 */
public class KeyValueTest {

    public static void main(String[] args) throws NotRegisteredException, IOException {
        // Example 1: Connect to Consul.
        Consul client = Consul.builder().build(); // connect on localhost

        // Example 4: Store key/values.
        KeyValueClient kvClient = client.keyValueClient();
        kvClient.putValue("foo", "bar");
        String putvalue = kvClient.getValueAsString("foo").get(); // bar
        System.out.println("putvalue=" + putvalue);
        
        
        kvClient.getValue("student", QueryOptions.blockSeconds(5, new BigInteger("0")).build(), new ConsulResponseCallback<Optional<Value>>() {

            AtomicReference<BigInteger> index = new AtomicReference<BigInteger>(null);

            public void onComplete(ConsulResponse<Optional<Value>> consulResponse) {

                if (consulResponse.getResponse().isPresent()) {
                    Value v = consulResponse.getResponse().get();
                    System.out.println(String.format("Value is: %s", new String(Base64.getDecoder().decode(v.getValue().get()))));
                }
                index.set(consulResponse.getIndex());
            }


            public void onFailure(Throwable throwable) {
                // System.out.println("Error encountered");
                // watch();
            }
        });
        
        
        

        // Example 5: Subscribe to value change.
        // You can use the ConsulCache implementations to easily subscribe to Key-Value changes.
        KVCache cache = KVCache.newCache(kvClient, "foo");
        cache.addListener(newValues -> {
            // Cache notifies all paths with "foo" the root path
            // If you want to watch only "foo" value, you must filter other paths
            Optional<Value> newValue = newValues.values().stream()
                    .filter(value -> value.getKey().equals("foo"))
                    .findAny();

            newValue.ifPresent(value -> {
                // Values are encoded in key/value store, decode it if needed
                Optional<String> decodedValue = newValue.get().getValueAsString();
                decodedValue.ifPresent(v -> System.out.println(String.format("Value is: %s", v))); // prints "bar"
            });
        });
        cache.start();

        System.in.read();

        // ...
        cache.stop();
    }

}
