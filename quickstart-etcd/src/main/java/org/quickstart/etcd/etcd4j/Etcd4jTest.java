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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mousio.client.retry.RetryNTimes;
import mousio.client.retry.RetryWithTimeout;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdErrorCode;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdVersionResponse;

/**
 * Etcd4jTest
 * 
 * @author：youngzil@163.com
 * @2019年4月15日 下午9:48:01
 * @since 1.0
 */
public class Etcd4jTest {

    public static void main(String[] args) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {

        // try (EtcdClient etcd = new EtcdClient()) {
        // // Logs etcd version
        // System.out.println(etcd.version());
        // }

        try (EtcdClient client = new EtcdClient(
                URI.create("http://localhost:2379"),
                URI.create("http://localhost:2379"))) {
            // Logs etcd version
            System.out.println(client.getVersion());

            EtcdVersionResponse versionResponse = client.version();
            System.out.println(versionResponse.cluster);
            System.out.println(versionResponse.server);

            testPut(client);

        }

        /*SslContext sslContext = SslContext.newClientContext();
        
        try (EtcdClient etcd = new EtcdClient(sslContext,
                URI.create("http://localhost:2379"),
                URI.create("http://localhost:2379"))) {
            // Logs etcd version
            System.out.println(etcd.getVersion());
        }*/

    }

    public static void testPut(EtcdClient client) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {
        EtcdKeysResponse response = client.put("foo2", "bar").send().get();

        // Prints out: bar
        System.out.println(response.node.value);

        // EtcdKeysResponse response2 = client.put("foo4", "bar").ttl(50).prevExist().send().get();
        // System.out.println(response2.prevNode.value);
        // System.out.println(response2.node.value);

        // Put examples
        // Simple put
        client.put("foo", "bar").send();

        // Put a new dir
        client.putDir("foo_dir").send();

        // Put with ttl and prevexists check
        client.put("foo", "bar2").ttl(20).prevExist().send();

        // Put with prevValue check
        client.put("foo", "bar3").prevValue("bar2").send();

        // Put with prevIndex check
        client.put("foo", "bar4").prevIndex(2).send();

        System.out.println("---------------------------");

        // Get examples
        // You need to read out the returned promises to see the response.

        // Simple key fetch
        client.get("foo").send();

        // Get all nodes and all nodes below it recursively
        client.getAll().recursive().send();
        // Gets directory foo_dir and all nodes below it recursively
        client.getDir("foo_dir").recursive().send();

        // Wait for next change on foo
        EtcdResponsePromise promise4 = client.get("foo").waitForChange().send();
        // Java 8 lambda construction
        promise4.addListener(promise -> {
            // do something with change
        });

        // Wait for change of foo with index 7
        client.get("foo").waitForChange(7).send();

        // Get all items recursively below queue as a sorted list
        client.get("queue").sorted().recursive().send();

        System.out.println("---------------------------");

        // Delete examples
        // You need to read out the returned promises to see the response

        // Simple delete
        client.delete("foo").send();

        // Directory and all subcontents delete
        client.deleteDir("foo_dir").recursive().send();

        client.delete("foo").prevIndex(3).send();

        client.delete("foo").prevValue("bar4").send();

        System.out.println("---------------------------");

        // Post examples
        // You need to read out the returned promises to see the response

        // Simple post
        client.post("queue", "Job1").send();

        // Post with ttl check
        client.put("queue", "Job2").ttl(20).send();

        System.out.println("---------------------------");

        // Set timeout on requests
        // It is possible to set a timeout on all requests. By default there is no timeout.

        // Timeout of 1 second on a put value
        EtcdResponsePromise<EtcdKeysResponse> putPromise = client.put("foo", "bar").timeout(1, TimeUnit.SECONDS).send();

        try {
            EtcdKeysResponse r = putPromise.get();
        } catch (TimeoutException e) {
            // Handle timeout
        } catch (Exception e) {
            // handle other types of exceptions
        }

        EtcdKeyGetRequest getRequest = client.get("foo").waitForChange().timeout(2, TimeUnit.MINUTES);

        try {
            EtcdKeysResponse r = getRequest.send().get();
        } catch (TimeoutException | IOException e) {
            try {
                // Retry again once
                EtcdKeysResponse r = getRequest.send().get();
            } catch (TimeoutException | IOException e2) {
                // Fails again... Maybe wait a bit longer or give up
            } catch (Exception e2) {
                // Handle other types of exceptions
            }
        } catch (Exception e) {
            // Handle other types of exceptions
        }

        System.out.println("---------------------------");
        
        
//        Set a Retry Policy

        // Set the retry policy for all requests on a etcd client connection
        // Will retry with an interval of 200ms with timeout of a total of 20000ms
        client.setRetryHandler(new RetryWithTimeout(200, 20000));

        // Set the retry policy for only one request
        // Will retry 2 times with an interval of 300ms
        EtcdKeysResponse response5 = client.get("foo").setRetryPolicy(new RetryNTimes(300, 2)).send().get();
        System.out.println(response5);

        System.out.println("---------------------------");

        EtcdResponsePromise<EtcdKeysResponse> promise1 = client.put("foo3", "bar22").send();
        EtcdResponsePromise<EtcdKeysResponse> promise2 = client.put("foo4", "bar22").send();

        // Call the promise in a blocking way
        try {
            EtcdKeysResponse response3 = promise1.get();
            // Do something with response
            System.out.println("response3=" + response3.node.value);
        } catch (EtcdException e) {
            if (e.isErrorCode(EtcdErrorCode.NodeExist)) {
                // Do something with error code
            }
            // Do something with the exception returned by etcd
        } catch (IOException | TimeoutException e) {
            // Handle other types of exceptions
            System.out.println("sssss");
        }

        // or listen to it async (Java 8 lambda construction)
        promise2.addListener(promise -> {
            Throwable t = promise.getException();
            if (t instanceof EtcdException) {
                if (((EtcdException) t).isErrorCode(EtcdErrorCode.NodeExist)) {
                    // Do something with error code
                    System.out.println("EtcdException" + EtcdException.DECODER);
                }
            }

            // getNow() returns null on exception
            EtcdKeysResponse response4 = promise.getNow();
            if (response4 != null) {
                System.out.println(response4.node.value);
            }
        });

    }

}
