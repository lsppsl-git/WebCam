package org.TCPUDP.client.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ChatWindow extends JFrame {
    ClientListen clientListen;
    JTextArea chatArea; // 每个聊天窗口有自己的聊天区域
    String friendName;  // 记录当前聊天的好友名

    public ChatWindow(Friend friend, ClientListen clientListen) {
        super("与" + friend.getName() + "的聊天窗口");
        this.clientListen = clientListen;
        this.friendName = friend.getName();
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 创建独立的聊天区域
        chatArea = new JTextArea();
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("发送");

        // 添加发送按钮的事件监听
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = inputField.getText();
                if (!msg.isEmpty()) {
                    clientListen.send(friendName, msg);
                    // 同时在当前聊天窗口显示发送的消息
                    chatArea.append("我: " + msg + "\n");
                    inputField.setText("");
                }
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        //添加头像到窗口标题
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        headerPanel.add(new JLabel(friend.getAvatar()));
        headerPanel.add(new JLabel(friend.getName()));
        headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        //组装窗口
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // 窗口关闭时从活跃窗口列表中移除
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                clientListen.removeChatWindow(friendName);
            }
        });

        setVisible(true);

        // 将当前聊天窗口添加到活跃窗口列表
        clientListen.addChatWindow(friendName, this);
        // 请求加载与该好友的聊天历史记录
        try {
            clientListen.client.queryHistory(clientListen.name, friendName, 50); // 请求最近50条记录
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 添加一个方法，用于显示接收到的消息
    public void displayMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }

    // 获取聊天区域的引用
    public JTextArea getChatArea() {
        return chatArea;
    }
}