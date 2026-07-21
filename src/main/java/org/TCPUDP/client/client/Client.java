package org.TCPUDP.client.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Client {

    String serverIP = "127.0.0.1";
    int serverPort = 10010;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public Socket start() {
        try {
            socket = new Socket(serverIP, serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("连接服务器成功");
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "连接服务器失败: " + e.getMessage());
            return null;
        }
    }

    // 发送注册消息
    public int sendRegisterMessage(String username, String password) throws IOException {
        out.writeUTF("REGISTER");
        out.writeUTF(username);
        out.writeUTF(password);
        out.flush();
        return in.readInt();
    }

    // 发送登录消息
    public int sendLoginMessage(String username, String password) throws IOException {
        out.writeUTF("LOGIN");
        out.writeUTF(username);
        out.writeUTF(password);
        out.flush();
        return in.readInt();
    }

    // 发送群聊消息
    public void sendGroupMessage(String message, String username) throws IOException {
        out.writeUTF("GROUP_CHAT");
        out.writeUTF(message);
        out.writeUTF(username);
        out.flush();
    }

    // 发送私聊消息
    public void sendPrivateMessage(String message, String sender, String receiver) throws IOException {
        out.writeUTF("PRIVATE_CHAT");
        out.writeUTF(message);
        out.writeUTF(sender);
        out.writeUTF(receiver);
        out.flush();
    }

    // 监听服务器消息
    public void listenServerMessages(JTextArea chatArea, ClientListen clientListen) {
        Thread listenerThread = new Thread(() -> {
            while (true) {
                try {
                    String type = in.readUTF();
                    switch (type) {
                        case "GROUP_CHAT":
                            String groupMessage = in.readUTF();
                            updateChatArea(chatArea, groupMessage + "\n");
                            break;
                        case "PRIVATE_CHAT":
                            String sender = in.readUTF();
                            String privateMessage = in.readUTF();
                            clientListen.dispatchMessage(sender, privateMessage);
                            break;
                        case "USER_LIST":
                            int userCount = in.readInt();
                            // 处理在线用户列表更新
                            break;
                        case "HISTORY_RESULT":
                            String target = in.readUTF(); // 从服务器读取target信息
                            int historyCount = in.readInt();
                            StringBuilder historyBuilder = new StringBuilder();
                            for (int i = 0; i < historyCount; i++) {
                                historyBuilder.append(in.readUTF()).append("\n");
                            }
                            final String historyMessages = historyBuilder.toString();
                            final String finalTarget = target; // 需要final变量在lambda表达式中使用
                            SwingUtilities.invokeLater(() -> {
                                // 根据不同的窗口类型显示历史记录
                                if (finalTarget.equals("chatRoom")) {
                                    // 群聊历史记录显示在主聊天区域
                                    chatArea.append("群聊历史记录：\n" + historyMessages);
                                } else {
                                    // 查找对应的私聊窗口并显示历史记录
                                    // 注意：这里需要让Client类能够访问activeChatWindows
                                    // 可以通过添加一个getActiveChatWindows方法到ClientListen类
                                    Map<String, ChatWindow> activeWindows = clientListen.getActiveChatWindows();
                                    ChatWindow chatWindow = activeWindows.get(finalTarget);
                                    if (chatWindow != null) {
                                        chatWindow.chatArea.append("历史记录：\n" + historyMessages);
                                    }
                                }
                            });
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("服务器连接断开");
                    break;
                }
            }
        });
        listenerThread.start();
    }

    // 更新聊天区域
    private void updateChatArea(JTextArea chatArea, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message);
        });
    }
    public void queryHistory(String username, String target, int limit) throws IOException {
        out.writeUTF("HISTORY_QUERY");
        out.writeUTF(username);
        out.writeUTF(target);
        out.writeInt(limit);
        out.flush();
    }
}