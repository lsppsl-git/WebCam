package org.TCPUDP.client.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MsgHandler {

    Socket socket;
    OutputStream out;
    InputStream in;

    public void initMsgHandler(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(Message msg) {
        msg.send(out);
    }

    public int readLoginMessage() {
        System.out.println("开始读取登录结果");
        int lr = 0;
        try {
            lr = in.read();
            System.out.println("登录结果" + lr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lr;
    }

    public int readRegisterMessage() {
        System.out.println("开始读取注册结果");
        int lr = 0;
        try {
            lr = in.read();
            System.out.println("注册结果" + lr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lr;
    }

    // 收消息的线程

}
