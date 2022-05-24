/**
 * 项目名称：quickstart-consul 
 * 文件名：ExampleTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright youngzil Corporation 2019
 * 版权所有 *
 */
package org.quickstart.consul.ecwid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.kv.model.GetValue;

/**
 * ExampleTest
 * 
 * @author：youngzil@163.com
 * @2019年4月15日 下午2:43:01
 * @since 1.0
 */
public class ExampleTest {
    
    ConsulClient client = new ConsulClient("127.0.0.1", 8500);

    private static ConsulClient consulClient;
    static {
        ConsulRawClient client2 = new ConsulRawClient("localhost", 8500);
        consulClient = new ConsulClient(client2);
    }

    //获取所有服务
    public static void getAllService(){
        Map<String, Service> map = consulClient.getAgentServices().getValue();
        System.out.println(map);
    }

    public static void main(String[] args) {
        getAllService();
    }

    public void registerService(String serviceName, String serviceId) {
        // register new service
        NewService newService = new NewService();
        newService.setId(serviceId);
        newService.setName(serviceName);
        newService.setTags(Arrays.asList("EU-West", "EU-East"));
        newService.setPort(8080);

        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp("http://127.0.0.1:8080/health");
        serviceCheck.setInterval("10s");
        newService.setCheck(serviceCheck);
        client.agentServiceRegister(newService);
    }

    public List<HealthService> findHealthyService(String serviceName, boolean onlyPassing) {
        Response<List<HealthService>> healthyServices = client.getHealthServices(serviceName, onlyPassing, QueryParams.DEFAULT);
        return healthyServices.getValue();
    }

    public void deRegisterService(String serviceId) {
        client.agentServiceDeregister(serviceId);
    }

    public void storeKV(String key, String value) {
        Response<Boolean> booleanResponse = client.setKVValue(key, value);
    }

    public String getKV(String key) {
        Response<GetValue> getValueResponse = client.getKVValue(key);
        // return getValueResponse.getValue().getValue();
        return getValueResponse.getValue().getDecodedValue();
    }

    public List<String> findRaftPeers() {
        Response<List<String>> listResponse = client.getStatusPeers();
        return listResponse.getValue();
    }

    public String findRaftLeader() {
        Response<String> stringResponse = client.getStatusLeader();
        return stringResponse.getValue();
    }

}
