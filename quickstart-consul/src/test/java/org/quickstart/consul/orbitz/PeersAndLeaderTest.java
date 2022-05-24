/**
 * 项目名称：quickstart-consul 
 * 文件名：PeersAndLeaderTest.java
 * 版本信息：
 * 日期：2019年4月15日
 * Copyright youngzil Corporation 2019
 * 版权所有 *
 */
package org.quickstart.consul.orbitz;

import java.io.IOException;

import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.StatusClient;

/**
 * PeersAndLeaderTest 
 *  
 * @author：youngzil@163.com
 * @2019年4月15日 上午10:54:07 
 * @since 1.0
 */
public class PeersAndLeaderTest {
    public static void main(String[] args) throws NotRegisteredException, IOException {
        // Example 1: Connect to Consul.
        Consul client = Consul.builder().build(); // connect on localhost
        
        
        
//        Example 7: Find Raft peers.
        StatusClient statusClient = client.statusClient();
        statusClient.getPeers().forEach(System.out::println);
        
        
//        Example 8: Find Raft leader.
        System.out.println(statusClient.getLeader()); // 127.0.0.1:8300
    }

}
