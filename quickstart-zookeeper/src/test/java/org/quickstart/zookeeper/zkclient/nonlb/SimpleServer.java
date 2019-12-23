package org.quickstart.zookeeper.zkclient.nonlb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer implements Runnable {

    public static void main(String[] args) throws IOException {
        int port = 18080;
        SimpleServer server = new SimpleServer(port);
        Thread thread = new Thread(server);
        thread.start();
    }

    private int port;

    public SimpleServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new SimpleServerHandler(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                }
            }
        }
    }
}


class SimpleServerHandler implements Runnable {

    private Socket socket;

    public SimpleServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String body = null;
            while (true) {
                body = in.readLine();
                if (body == null)
                    break;
                System.out.println("Receive : " + body);
                out.println("Hello, " + body);
            }

        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                this.socket = null;
            }
        }
    }
}
