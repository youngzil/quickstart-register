/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.quickstart.zookeeper.curator.cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;

import com.google.common.collect.Lists;

/**
 * An example of the PathChildrenCache. The example "harness" is a command processor that allows adding/updating/removed nodes in a path. A PathChildrenCache keeps a cache of these changes and outputs
 * when updates occurs.
 */

/*特点：
（1）永久监听指定节点下的节点 
（2）只能监听指定节点下一级节点的变化，比如说指定节点”/example”, 在下面添加”node1”可以监听到，但是添加”node1/n1”就不能被监听到了 
（3）可以监听到的事件：节点创建、节点数据的变化、节点删除等*/
public class PathChildrenCacheExample {
    private static final String PATH = "/example/cache";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = null;
        PathChildrenCache cache = null;
        try {
            client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
            client.start();

            // in this example we will cache data. Notice that this is optional.
            cache = new PathChildrenCache(client, PATH, true);
            cache.start();

            processCommands(client, cache);
        } finally {
            CloseableUtils.closeQuietly(cache);
            CloseableUtils.closeQuietly(client);
        }
    }

    /**
     * Curator也无法对二级子节点进行事件监听。也就是说，如果使用PathChildrenCache对/zk-book进行监听，那么当/zk-book/c1/c2节点被创建或删除的时候，是无法触发子节点变更事件的。 addListener
     * 
     * @Description:
     * @param cache void
     * @Exception
     * @author：yangzl@asiainfo.com
     * @2018年4月26日 下午12:22:59
     * @since 1.0
     */
    private static void addListener(PathChildrenCache cache) {
        // a PathChildrenCacheListener is optional. Here, it's used just to log changes
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED: {
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }

                    case CHILD_UPDATED: {
                        System.out.println("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }

                    case CHILD_REMOVED: {
                        System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }
                }
            }
        };
        cache.getListenable().addListener(listener);
    }

    private static void processCommands(CuratorFramework client, PathChildrenCache cache) throws Exception {
        // More scaffolding that does a simple command line processor

        printHelp();

        try {
            addListener(cache);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            boolean done = false;
            while (!done) {
                System.out.print("> ");

                String line = in.readLine();
                if (line == null) {
                    break;
                }

                String command = line.trim();
                String[] parts = command.split("\\s");
                if (parts.length == 0) {
                    continue;
                }
                String operation = parts[0];
                String args[] = Arrays.copyOfRange(parts, 1, parts.length);

                if (operation.equalsIgnoreCase("help") || operation.equalsIgnoreCase("?")) {
                    printHelp();
                } else if (operation.equalsIgnoreCase("q") || operation.equalsIgnoreCase("quit")) {
                    done = true;
                } else if (operation.equals("set")) {
                    setValue(client, command, args);
                } else if (operation.equals("remove")) {
                    remove(client, command, args);
                } else if (operation.equals("list")) {
                    list(cache);
                }

                Thread.sleep(1000); // just to allow the console output to catch up
            }
        } finally {
        }
    }

    private static void list(PathChildrenCache cache) {
        if (cache.getCurrentData().size() == 0) {
            System.out.println("* empty *");
        } else {
            for (ChildData data : cache.getCurrentData()) {
                System.out.println(data.getPath() + " = " + new String(data.getData()));
            }
        }
    }

    private static void remove(CuratorFramework client, String command, String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("syntax error (expected remove <path>): " + command);
            return;
        }

        String name = args[0];
        if (name.contains("/")) {
            System.err.println("Invalid node name" + name);
            return;
        }
        String path = ZKPaths.makePath(PATH, name);

        try {
            client.delete().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            // ignore
        }
    }

    private static void setValue(CuratorFramework client, String command, String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("syntax error (expected set <path> <value>): " + command);
            return;
        }

        String name = args[0];
        if (name.contains("/")) {
            System.err.println("Invalid node name" + name);
            return;
        }
        String path = ZKPaths.makePath(PATH, name);

        byte[] bytes = args[1].getBytes();
        try {
            client.setData().forPath(path, bytes);
        } catch (KeeperException.NoNodeException e) {
            client.create().creatingParentContainersIfNeeded().forPath(path, bytes);
        }
    }

    private static void printHelp() {
        System.out.println("An example of using PathChildrenCache. This example is driven by entering commands at the prompt:\n");
        System.out.println("set <name> <value>: Adds or updates a node with the given name");
        System.out.println("remove <name>: Deletes the node with the given name");
        System.out.println("list: List the nodes/values in the cache");
        System.out.println("quit: Quit the example");
        System.out.println();
    }
}