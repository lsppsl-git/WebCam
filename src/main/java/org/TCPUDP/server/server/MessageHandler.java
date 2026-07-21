package org.TCPUDP.server.server;

import org.TCPUDP.util.PersistentMessageHistoryManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class MessageHandler {
    private PersistentMessageHistoryManager historyManager = new PersistentMessageHistoryManager();
    private DataInputStream dis;
    private DataOutputStream dos;

    public MessageHandler(DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
    }

    // 处理注册消息
    public void handleRegister(String name, String pwd) throws IOException {
        int code = Server.userServer.userRegister(name, pwd);
        dos.writeInt(code);
        dos.flush();
    }

    // 处理登录消息
    public boolean handleLogin(String name, String pwd, Socket socket) throws IOException {
        boolean isLogin = Server.userServer.userLogin(name, pwd);
        if (isLogin) {
            dos.writeInt(202); // 登录成功
            Server.clients.put(name, socket);
            // 广播用户上线消息
            broadcastUserList();
        } else {
            dos.writeInt(203); // 登录失败
        }
        dos.flush();
        return isLogin;
    }

    // 处理群聊消息
    public void handleGroupMessage(String message, String username) {
        // 保存消息到数据库
        historyManager.saveMessage(username, "chatRoom", message);

        // 广播消息给所有在线用户
        String formattedMessage = username + ": " + message;
        broadcastMessage(formattedMessage);
    }

    // 处理私聊消息
    public void handlePrivateMessage(String message, String sender, String receiver) throws IOException {
        // 保存消息到数据库
        historyManager.saveMessage(sender, receiver, message);

        // 发送私聊消息
        Socket socket = Server.clients.get(receiver);
        if (socket != null && !socket.isClosed()) {
            DataOutputStream targetDos = new DataOutputStream(socket.getOutputStream());
            targetDos.writeUTF("PRIVATE_CHAT");
            targetDos.writeUTF(sender);
            targetDos.writeUTF(message);
            targetDos.flush();
        }
    }

    // 处理历史记录查询
    public List<String> handleHistoryQuery(String username, String target, int limit) {
        if (target.equals("chatRoom")) {
            return historyManager.getChatRoomHistory(limit);
        } else {
            return historyManager.getPrivateChatHistory(username, target, limit);
        }
    }

    // 广播用户列表
    private void broadcastUserList() {
        List<String> onlineUsers = Server.userServer.getOnlineUsers();
        for (Map.Entry<String, Socket> entry : Server.clients.entrySet()) {
            try {
                DataOutputStream targetDos = new DataOutputStream(entry.getValue().getOutputStream());
                targetDos.writeUTF("USER_LIST");
                targetDos.writeInt(onlineUsers.size());
                for (String user : onlineUsers) {
                    targetDos.writeUTF(user);
                }
                targetDos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 广播消息
    private void broadcastMessage(String message) {
        for (Map.Entry<String, Socket> entry : Server.clients.entrySet()) {
            try {
                DataOutputStream targetDos = new DataOutputStream(entry.getValue().getOutputStream());
                targetDos.writeUTF("GROUP_CHAT");
                targetDos.writeUTF(message);
                targetDos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}