package org.quickstart.zookeeper.zkclient.lb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class SimpleClient {

    private static List<String> servers = new ArrayList<>();

    public static void main(String[] args) {

        initServerList();

        SimpleClient client = new SimpleClient();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String name;
            try {
                name = console.readLine();
                if ("exit".equals(name)) {
                    System.exit(0);
                }
                client.send(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initServerList() {
        // 启动时从ZooKeeper读取可用服务器
        String path = "/test";
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 60000, 1000);
        List<String> childs = zkClient.getChildren(path);
        servers.clear();
        for (String p : childs) {
            servers.add(zkClient.readData(path + "/" + p));
        }
        // 订阅节点变化事件
        zkClient.subscribeChildChanges("/test", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s", parentPath, currentChilds.toString()));
                servers.clear();
                for (String p : currentChilds) {
                    servers.add(zkClient.readData(path + "/" + p));
                }
                System.out.println("Servers: " + servers.toString());
            }
        });

    }

    public static String getServer() {
        return servers.get(new Random().nextInt(servers.size()));
    }

    public SimpleClient() {}

    public void send(String name) {

        String server = SimpleClient.getServer();
        String[] cfg = server.split(":");

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(name);
            while (true) {
                String resp = in.readLine();
                if (resp == null)
                    break;
                else if (resp.length() > 0) {
                    System.out.println("Receive : " + resp);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
