package org.TCPUDP.client.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClientUI extends JFrame {
    //消息管理
    MsgHandler msgHandler = new MsgHandler();
    //获取socket
    Client client = new Client();
    //发消息
    ClientListen clientListen = new ClientListen();
    JFrame jf;

    public void initUI() {
        setTitle("客户端");
        setSize(600, 500);
        setLocation(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel chatPanel = new JPanel();
        JPanel ctrlPanel = new JPanel();
        // chatPanel 聊天信息接收框  消息发送框 发送按钮
        chatPanel.setLayout(new BorderLayout());
        //聊天框
        JTextArea chatArea = new JTextArea();
        //换行
        chatArea.setLineWrap(true);
        //不可编辑
        chatArea.setEditable(false);
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        //添加加载历史记录按钮
        JButton loadHistoryBtn = new JButton("历史记录");
        loadHistoryBtn.setSize(new Dimension(50,50));
        loadHistoryBtn.setActionCommand("LOAD_HISTORY");
        //发送框
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));//各组件水平距离和垂直距离
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(500, 50));
        JButton sendBtn = new JButton("发送");
        JButton aiBtn = new JButton("AI");
        // 创建一个面板来容纳两个按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.add(aiBtn);
        buttonPanel.add(loadHistoryBtn);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        inputPanel.add(buttonPanel, BorderLayout.WEST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        //ctrlPanel 控制聊天框 视频按钮 好友列表
        chatPanel.add(ctrlPanel, BorderLayout.EAST);
        JButton webBtn = new JButton("视频");
        ctrlPanel.setLayout(new BorderLayout());
        //好友列表
        ArrayList<Friend> friends = createFriendData();
        //创建列表模型
        DefaultListModel<Friend> ListModel = new DefaultListModel<>();
        friends.forEach(ListModel::addElement);
        // 创建列表并设置自定义渲染器
        JList<Friend> friendJList = new JList<>();
        friendJList.setModel(ListModel);//设置列表模型
        friendJList.setCellRenderer(new FriendListCellRenderer());//设置自定义渲染器
        friendJList.setFixedCellHeight(50);//设置每个项的高度
        //添加好友列表点击事件
        addFriendListMouseListener(friendJList,inputField,chatArea);
        //添加列表入滚动页面
        JScrollPane scrollPane = new JScrollPane(friendJList);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        ctrlPanel.add(scrollPane, BorderLayout.CENTER);
        ctrlPanel.add(webBtn, BorderLayout.SOUTH);
        add(chatPanel);
        // 关闭窗口时 发送关闭消息
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // 可选：在这里添加用户登出逻辑
            }
        });
        setVisible(true);
        sendBtn.addActionListener(clientListen);
        webBtn.addActionListener(clientListen);
        loadHistoryBtn.addActionListener(clientListen);

        // AI按钮点击事件
        aiBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    // 显示用户消息
                    chatArea.append("我: " + message + "\n");
                    // 清空输入框
                    inputField.setText("");
                    // 异步调用AI接口
                    new Thread(() -> {
                        String aiResponse = callAIAPI(message);
                        // 在EDT线程中更新UI
                        SwingUtilities.invokeLater(() -> {
                            chatArea.append("AI: " + aiResponse + "\n");
                        });
                    }).start();
                }
            }
        });
        //输入框回车发送
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientListen.sendMsg();
                //清空输入框
                inputField.setText("");
            }
        });
        clientListen.inputJtf = inputField;
        clientListen.charArea = chatArea;
        // 读取消息
        client.listenServerMessages(chatArea,clientListen);
    }

    public void initLoginUI() {
        jf = new JFrame("登录界面");
        jf.setSize(400, 300);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        jf.setLayout(new FlowLayout());
        JLabel nameJla = new JLabel("账号：");
        JTextField nameJtf = new JTextField(35);
        JLabel pwdJla = new JLabel("密码：");
        JPasswordField pwdJtf = new JPasswordField(35);
        JButton loginBtn = new JButton("登录");
        JButton registerBtn = new JButton("注册");

        jf.add(nameJla);
        jf.add(nameJtf);
        jf.add(pwdJla);
        jf.add(pwdJtf);
        jf.add(loginBtn);
        jf.add(registerBtn);
        jf.setVisible(true);
        loginBtn.addActionListener(clientListen);
        registerBtn.addActionListener(clientListen);
        clientListen.nameJtf = nameJtf;
        clientListen.pwdJtf = pwdJtf;
        clientListen.clientUI = this;
        clientListen.client = this.client; // 添加这行初始化client
        // 启动界面之后 启动连接
        Socket socket = client.start();
        msgHandler.initMsgHandler(socket);
    }

    public void setjfvisible() {
        jf.setVisible(false);
    }

    // 创建模拟好友数据
    public ArrayList<Friend> createFriendData() {
        ArrayList<Friend> friends = new ArrayList<>();

        // 创建默认头像（实际应用中可替换为真实图片）
        ImageIcon Avatar = new ImageIcon("C://Users//lenovo//Pictures//Saved Pictures//1.jpg");
        Image avatarImage = Avatar.getImage();
        Image defaultImage = avatarImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon defaultAvatar = new ImageIcon(defaultImage);
        // 添加好友
        friends.add(new Friend("张三", "正在聊天中...", true, defaultAvatar));
        friends.add(new Friend("李四", "离线", false, defaultAvatar));
        friends.add(new Friend("王五", "忙碌中", true, defaultAvatar));
        friends.add(new Friend("赵六", "在线", true, defaultAvatar));
        friends.add(new Friend("孙七", "离开", true, defaultAvatar));
        friends.add(new Friend("周八", "离线", false, defaultAvatar));
        friends.add(new Friend("吴九", "在线", true, defaultAvatar));
        friends.add(new Friend("郑十", "在线", true, defaultAvatar));
        friends.add(new Friend("钱十一", "离线", false, defaultAvatar));
        friends.add(new Friend("孙十二", "在线", true, defaultAvatar));
        friends.add(new Friend("李十三", "忙碌中", true, defaultAvatar));
        friends.add(new Friend("周十四", "在线", true, defaultAvatar));

        return friends;
    }
    //给好友列表添加点击事件
    // 修改双击事件处理，不再传递chatArea
    public void addFriendListMouseListener(JList<Friend> friendJList, JTextField inputField, JTextArea chatArea) {
        friendJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = friendJList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Friend selectedFriend = friendJList.getModel().getElementAt(index);
                        System.out.println("单击了好友：" + selectedFriend.getName());
                        inputField.setText("@" + selectedFriend.getName() + ":");
                    }
                } else if (e.getClickCount() == 2) {
                    int index = friendJList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Friend selectedFriend = friendJList.getModel().getElementAt(index);
                        System.out.println("双击了好友：" + selectedFriend.getName());
                        // 打开聊天窗口，但不传递chatArea
                        ChatWindow chatWindow = new ChatWindow(selectedFriend, clientListen);
                    }
                }
            }
        });
    }

    // 调用免费AI接口的方法
    private String callAIAPI(String message) {
        // 尝试使用免费的AI接口
        try {
            // 使用OpenAI的API（需要替换为实际的API密钥）
            String apiKey = "YOUR_API_KEY";
            String apiUrl = "https://api.openai.com/v1/chat/completions";

            // 创建URL对象
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置超时时间
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            // 设置请求方法和头部
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            // 构建请求体
            String requestBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // 解析JSON响应
                String jsonResponse = response.toString();
                // 简单解析获取AI回复（实际应用中建议使用JSON库）
                int startIndex = jsonResponse.indexOf("content\":\"") + 10;
                int endIndex = jsonResponse.indexOf("\"", startIndex);
                if (startIndex > 9 && endIndex > startIndex) {
                    return jsonResponse.substring(startIndex, endIndex);
                } else {
                    return "AI回复解析失败";
                }
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            // 网络连接失败，使用本地模拟回复
            System.out.println("网络连接失败，使用本地模拟回复: " + e.getMessage());
            return getLocalAIResponse(message);
        }
    }

    // 本地模拟AI回复的方法
    private String getLocalAIResponse(String message) {
        // 简单的模拟回复
        if (message.contains("你好") || message.contains("hello") || message.contains("hi")) {
            return "你好！我是聊天机器人，有什么可以帮助你的吗？";
        } else if (message.contains("天气")) {
            return "今天天气晴朗，适合外出活动！";
        } else if (message.contains("时间")) {
            return "当前时间是：" + new java.util.Date().toString();
        } else if (message.contains("名字")) {
            return "我是你的AI助手，很高兴为你服务！";
        } else if (message.contains("帮助")) {
            return "我可以回答你的问题，和你聊天，或者提供一些基本信息。";
        } else {
            return "这是一个模拟的AI回复。由于网络连接问题，无法连接到真实的AI服务。\n你可以尝试检查网络连接，或者使用其他免费的AI API服务。";
        }
    }

    public static void main(String[] args) {
        new ClientUI().initLoginUI();
    }
}