package org.quickstart.consul.ecwid;

import java.util.Base64;
import java.util.List;

import com.ecwid.consul.json.GsonFactory;
import com.ecwid.consul.transport.RawResponse;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ecwid.consul.v1.status.StatusConsulClient;
import com.google.common.reflect.TypeToken;

public class ExampleTest2 {

    public static void mianClient() {
        ConsulClient consulClient = new ConsulClient("127.0.0.1", 8500);
        Response<GetValue> response = consulClient.getKVValue("student");
        System.out.println(new String(Base64.getDecoder().decode(response.getValue().getValue())));
    }

    // ROOT API kv health status
    public static void rawClient() {
        ConsulRawClient rawClient = new ConsulRawClient("127.0.0.1", 8500);
        RawResponse rawResponse = rawClient.makeGetRequest("/v1/kv/student");

        List<GetValue> list = GsonFactory.getGson().fromJson(rawResponse.getContent(),
                new TypeToken<List<GetValue>>() {}.getType());
        GetValue getValue = list.get(0);
        System.out.println(getValue.getKey());
        System.out.println(new String(Base64.getDecoder().decode(getValue.getValue())));

        rawResponse = rawClient.makeGetRequest("/v1/health/service/consul");
        List<HealthService> slist = GsonFactory.getGson().fromJson(rawResponse.getContent(),
                new TypeToken<List<HealthService>>() {}.getType());
        HealthService hservice = slist.get(0);
        System.out.println(hservice.getService().toString());

        rawResponse = rawClient.makeGetRequest("/v1/agent/services");
        com.ecwid.consul.v1.agent.model.Service aservice = GsonFactory.getGson().fromJson(rawResponse.getContent(),
                new TypeToken<com.ecwid.consul.v1.agent.model.Service>() {}.getType());
        System.out.println(aservice.toString());

        rawResponse = rawClient.makeGetRequest("/v1/status/leader");
        String leaders = rawResponse.getContent();
        System.out.println(leaders);
    }

    public static void statusClient() {
        StatusConsulClient statusClient = new StatusConsulClient("127.0.0.1", 8500);
        Response leader = statusClient.getStatusLeader();
        Response peer = statusClient.getStatusPeers();
        System.out.println(leader.toString());
        System.out.println(peer.toString());
    }

}
