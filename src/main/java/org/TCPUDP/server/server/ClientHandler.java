package org.TCPUDP.server.server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ClientHandler {
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    User user;
    Sender sender;
    MessageHandler messageHandler;

    public ClientHandler(Socket socket, Sender sender) {
        this.sender = sender;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            messageHandler = new MessageHandler(in, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
            System.out.println("发送成功: " + msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readMessage() {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // 检查是否有可用数据
                        if (in.available() > 0) {
                            // 尝试读取数据
                            try {
                                // 标记当前位置
                                in.mark(1024);
                                // 尝试读取消息类型
                                String type = in.readUTF();
                                System.out.println("收到消息类型: " + type);

                                switch (type) {
                                    case "REGISTER":
                                        String regName = in.readUTF();
                                        String regPwd = in.readUTF();
                                        messageHandler.handleRegister(regName, regPwd);
                                        break;
                                    case "LOGIN":
                                        String loginName = in.readUTF();
                                        String loginPwd = in.readUTF();
                                        messageHandler.handleLogin(loginName, loginPwd, socket);
                                        break;
                                    case "GROUP_CHAT":
                                        String groupMsg = in.readUTF();
                                        String groupSender = in.readUTF();
                                        messageHandler.handleGroupMessage(groupMsg, groupSender);
                                        break;
                                    case "PRIVATE_CHAT":
                                        String privateMsg = in.readUTF();
                                        String privateSender = in.readUTF();
                                        String privateReceiver = in.readUTF();
                                        messageHandler.handlePrivateMessage(privateMsg, privateSender, privateReceiver);
                                        break;
                                    case "HISTORY_QUERY":
                                        String queryUser = in.readUTF();
                                        String queryTarget = in.readUTF();
                                        int limit = in.readInt();
                                        // 处理历史记录查询并返回结果
                                        List<String> history = messageHandler.handleHistoryQuery(queryUser, queryTarget, limit);
                                        out.writeUTF("HISTORY_RESULT");
                                        out.writeUTF(queryTarget); // 发送target信息回客户端
                                        out.writeInt(history.size());
                                        for (String msg : history) {
                                            out.writeUTF(msg);
                                        }
                                        out.flush();
                                        break;
                                }
                            } catch (UTFDataFormatException e) {
                                // 不是有效的UTF字符串，可能是视频请求
                                try {
                                    // 重置到标记位置
                                    in.reset();
                                    // 读取视频端口偏移量
                                    int portOffset = in.readUnsignedByte();
                                    int clientPort = 10000 + portOffset; // 恢复完整端口号
                                    System.out.println("收到视频请求，客户端端口: " + clientPort);
                                    // 启动视频发送线程
                                    if (sender != null) {
                                        sender.sendThread(clientPort);
                                    } else {
                                        System.out.println("视频发送器未初始化，无法处理视频请求");
                                    }
                                } catch (IOException ex) {
                                    // 处理其他异常
                                    ex.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        // 处理客户端断开连接的情况
                        System.out.println("客户端断开连接");
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    }
                }
            }
        };
        t1.start();
    }
}