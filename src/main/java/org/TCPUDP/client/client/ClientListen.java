package org.TCPUDP.client.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientListen implements ActionListener {
    //客户端
    JTextArea charArea;
    JTextField inputJtf;

    // 登录界面的输入框
    JTextField nameJtf;
    JTextField pwdJtf;

    ClientUI clientUI;
    Client client;
    String name;
    Receiver cam;

    // 维护当前活跃的聊天窗口
    public Map<String, ChatWindow> activeChatWindows = new HashMap<>();

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("登录")) {
            String name = nameJtf.getText();
            String pwd = pwdJtf.getText();
            try {
                int code = client.sendLoginMessage(name, pwd);
                System.out.println("登录结果" + code);
                if (code == 202) {
                    this.name = name;
                    clientUI.initUI();
                    clientUI.setjfvisible();
                } else {
                    JOptionPane.showMessageDialog(null, "登录失败！");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "登录异常: " + ex.getMessage());
            }
        } else if (cmd.equals("注册")) {
            String name = nameJtf.getText();
            String pwd = pwdJtf.getText();
            try {
                int code = client.sendRegisterMessage(name, pwd);
                if (code == 0) {
                    JOptionPane.showMessageDialog(null, "注册成功！");
                } else if (code == 1) {
                    JOptionPane.showMessageDialog(null, "用户名已存在！");
                } else {
                    JOptionPane.showMessageDialog(null, "注册失败！");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "注册异常: " + ex.getMessage());
            }
        } else if (cmd.equals("发送")) {
            sendMsg();
            //清空输入框
            inputJtf.setText("");
        } else if (cmd.equals("视频")) {
            cam = new Receiver();
            cam.clientListen = clientUI.clientListen;
            Random random = new Random();
            int i = random.nextInt(10000, 10100);
            System.out.println(i);
            try {
                client.out.write(i - 10000);
                client.out.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            cam.start(name, i);
        } else if (cmd.equals("挂断视频")) {
            cam.setjfclose();
        } else if (cmd.equals("LOAD_HISTORY")) {
            loadChatRoomHistory();
        }

    }

    // 添加聊天窗口到活跃列表
    public void addChatWindow(String friendName, ChatWindow chatWindow) {
        activeChatWindows.put(friendName, chatWindow);
    }

    // 从活跃列表中移除聊天窗口
    public void removeChatWindow(String friendName) {
        activeChatWindows.remove(friendName);
    }

    // 向特定聊天窗口分发消息
    public void dispatchMessage(String sender, String message) {
        // 检查是否有对应于发送者的活跃聊天窗口
        ChatWindow chatWindow = activeChatWindows.get(sender);
        if (chatWindow != null) {
            chatWindow.displayMessage(sender, message);
        }
    }

    // 发送消息到指定好友
    public void send(String friendName, String message) {
        try {
            client.sendPrivateMessage(message, name, friendName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 发送群聊消息
    public void sendMsg() {
        String msg = inputJtf.getText();
        if (!msg.isEmpty()) {
            try {
                client.sendGroupMessage(msg, name);
                charArea.append("我: " + msg + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 获取活跃聊天窗口的方法
    public Map<String, ChatWindow> getActiveChatWindows() {
        return activeChatWindows;
    }
    // 加载群聊历史记录的方法
    public void loadChatRoomHistory() {
        try {
            // 请求最近50条群聊历史记录
            client.queryHistory(name, "chatRoom", 50);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载群聊历史记录失败: " + e.getMessage());
        }
    }
}