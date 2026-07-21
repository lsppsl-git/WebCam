package org.TCPUDP.server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class Server {

    ServerSocket serverSocket;
    int port = 10010;

    static UserServer userServer = new UserServer();
    static ArrayList<ClientHandler> clientList = new ArrayList<>();
    static Map<String, Socket> clients = new ConcurrentHashMap<>(); // 存储在线用户的Socket连接

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器启动成功，端口号：" + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenerClient(Sender sender) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(socket, sender);
                        clientList.add(clientHandler);
                        clientHandler.readMessage();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        t1.start();
    }

    public static void main(String[] args) {
        try {
            // 创建服务器实例
            Server server = new Server();
            server.start();

            // 启动视频发送器
            Sender sender = new Sender();
            try {
                sender.start();
                System.out.println("视频发送器启动成功");
            } catch (TimeoutException e) {
                System.out.println("启动视频发送器失败：" + e.getMessage());
                // 继续运行服务器，只是视频功能不可用
                sender = null;
            }

            // 开始监听客户端连接
            server.listenerClient(sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}