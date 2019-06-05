/**
 * 项目名称：quickstart-consul 
 * 文件名：ServiceTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright youngzil Corporation 2019
 * 版权所有 *
 */
package org.quickstart.consul.orbitz;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.StatusClient;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.ServiceHealth;

/**
 * ServiceTest
 * 
 * @author：youngzil@163.com
 * @2019年4月15日 上午10:45:13
 * @since 1.0
 */
public class ServiceTest {
    
    // static Consul consul = Consul.builder().withHostAndPort(HostAndPort.fromString("192.168.1.246:8500")).build();

    static String url = "http:/127.0.0.1:8500";
    static Consul consul = Consul.builder().withUrl(url).build();

    public static void main(String[] args) throws NotRegisteredException, IOException {
        
        System.out.println(URLEncoder.encode("http://www.baidu.com", "UTF-8"));

        // 服务注册和服务发现
        // serviceRegister();
        // serviceGet();

        // Example 1: Connect to Consul.
        Consul client = Consul.builder().build(); // connect on localhost

        // default host is localhost and defalt port is 8500
        // Consul client = Consul.newClient();

        // String url = "http:/127.0.0.1:8500";
        // Consul client = Consul.builder().withUrl(url).build();

        // Example 2: Register and check your service in with Consul.
        // 获取 agent
        AgentClient agentClient = client.agentClient();

        String serviceId = "1";
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name("myService")
                .port(8080)// 服务端口
                .address("http://localhost:8080/actuator/health")
                .check(Registration.RegCheck.ttl(3L)) // registers with a TTL of 3 seconds
                .tags(Collections.singletonList("tag2"))
                .meta(Collections.singletonMap("version", "1.1"))
                .build();

        /*int port = 8080;
        int interval = 10;
        String serviceName = "helloWorldService";
        List<String> tag = new ArrayList<>();
        Map<String, String> meta = new HashMap<>();
        client.agentClient().register(port,
                URI.create("http://localhost:8080/actuator/health").toURL(),
                interval,
                serviceName,
                serviceName,
                tag,
                meta);*/

        agentClient.register(service);
        // Check in with Consul (serviceId required only).
        // Client will prepend "service:" for service level checks.
        // Note that you need to continually check in before the TTL expires, otherwise your service's state will be marked as "critical".
        agentClient.pass(serviceId);

        // Example 3: Find available (healthy) services.
        String serviceName = "myService";
        HealthClient healthClient = client.healthClient();
        // Discover only "passing" nodes

        // List<Server> upServerList = new ArrayList<>();//可以定义一个Server实体，然后把获取到的服务放在这个实体中
        List<ServiceHealth> availableServers = healthClient.getHealthyServiceInstances(serviceName).getResponse();
        availableServers.forEach(x -> {

            System.out.println(x.getNode().getAddress());
            System.out.println(x.getService().getPort());

            // Server server = new Server();
            // server.setHost(x.getNode().getAddress());
            // server.setPort(x.getService().getPort());
            // upServerList.add(server);
        });

        // 获取配置中的健康检查URL
        // Consul agent 会来ping这个URL以确定service是否健康
        URL healthURL = URI.create("http://localhost:8080/actuator/health").toURL();
        // 服务注册
        // public void register(int port, URL http, long interval, String name, String id, String... tags)
        // port 注册服务的端口 http 健康检查 interval 健康检查时间间隔 name 服务名字 id 注册ID tags 注册的tag 比如 Dev 用于consul中取不同的配置。
        // agentClient.register(8080, healthURL, 3L, serviceName, "id-1", Collections.singletonList("tag2"), Collections.singletonMap("version", "1.1"));

        // Example 6: Subscribe to healthy services
        // You can also use the ConsulCache implementations to easily subscribe to healthy service changes.
        ServiceHealthCache svHealth = ServiceHealthCache.newCache(healthClient, serviceName);
        svHealth.addListener((Map<ServiceHealthKey, ServiceHealth> newValues) -> {
            // do something with updated server map
            System.out.println("ssss");
            System.out.println(newValues);
        });
        svHealth.start();

        System.in.read();
        // ...
        svHealth.stop();

    }

    /**
     * 服务注册
     */
    public static void serviceRegister() {
        AgentClient agent = consul.agentClient();

        // 健康检测
        ImmutableRegCheck check = ImmutableRegCheck.builder().http("http://192.168.1.104:9020/health").interval("5s").build();

        ImmutableRegistration.Builder builder = ImmutableRegistration.builder();
        builder.id("tomcat1").name("tomcat").addTags("v1").address("192.168.1.104").port(8080).addChecks(check);

        agent.register(builder.build());
    }

    /**
     * 服务获取
     */
    public static void serviceGet() {
        HealthClient client = consul.healthClient();
        String name = "tomcat";
        // 获取所有服务
        System.out.println(client.getAllServiceInstances(name).getResponse().size());

        // 获取所有正常的服务（健康检测通过的）
        client.getHealthyServiceInstances(name).getResponse().forEach((resp) -> {
            System.out.println(resp);
        });
    }

    public void registerService(String serviceName, String serviceId) {

        AgentClient agentClient = consul.agentClient();

        try {
            // "本应用就在"http://127.0.0.1:" + + "/health"，并且启用actuator，可以作为consul的监控服务检查
            agentClient.register(8080, URI.create("http://127.0.0.1:8080/health").toURL(),
                    10L, serviceName, serviceId, null, null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            agentClient.pass(serviceId);
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void deRegisterService(String serviceId) {
        AgentClient agentClient = consul.agentClient();
        agentClient.deregister(serviceId);
    }

    public List<ServiceHealth> findServiceHealthy(String servicename) {
        HealthClient healthClient = consul.healthClient();
        return healthClient.getHealthyServiceInstances(servicename).getResponse();
    }

    public void storeKV(String key, String value) {
        KeyValueClient kvClient = consul.keyValueClient();
        kvClient.putValue(key, value);
    }

    public String getKV(String key) {
        KeyValueClient kvClient = consul.keyValueClient();
        return kvClient.getValueAsString(key).get();
    }

    public List<String> findRaftPeers() {
        StatusClient statusClient = consul.statusClient();
        return statusClient.getPeers();
    }

    public String findRaftLeader() {
        StatusClient statusClient = consul.statusClient();
        return statusClient.getLeader();
    }

}
