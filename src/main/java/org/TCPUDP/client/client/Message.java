package org.TCPUDP.client.client;

import java.io.IOException;
import java.io.OutputStream;

public class Message {
    // 制定好一条消息需要包含哪些内容
    /**
     * 类型ID
     * 81：登录
     * 82：注册
     * 91：群聊
     * 92：私聊
     */
    int typeID;
    // 消息总长度
    int length;

    // 接收方信息
    int friendLength;
    String friendName;
    int pwdLength;
    String pwd;

    // 发送方信息
    int senderLength;
    String senderName;

    // 消息内容
    int msgLength;
    String msg;

    // 构造方法

    // 登录、注册消息
    public Message(int typeID,
                   String senderName, String pwd) {
        this.typeID = typeID;
        this.senderLength = senderName.getBytes().length;
        this.senderName = senderName;
        this.pwdLength = pwd.getBytes().length;
        this.pwd = pwd;
    }
    // 群聊消息
    public Message(int typeID, int length,
                   String senderName,
                   String msg) {
        this.typeID = typeID;
        this.length = length;
        this.senderLength = senderName.getBytes().length;
        this.senderName = senderName;
        this.msgLength = msg.getBytes().length;
        this.msg = msg;

    }
    // 私聊消息
    public Message(int typeID, int length,  String friendName,  String senderName,  String msg) {
        this.typeID = typeID;
        this.length = length;
        this.friendLength = friendName.getBytes().length;
        this.friendName = friendName;
        this.senderLength = senderName.getBytes().length;
        this.senderName = senderName;
        this.msgLength = msg.getBytes().length;
        this.msg = msg;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void send(OutputStream out) {
        /**
         * 类型ID
         * 81：登录
         * 82：注册
         * 91：群聊
         * 92：私聊
         */
        try {
            out.write(typeID);
            out.write(length);
            out.write(senderLength);
            out.write(senderName.getBytes());
            if (typeID == 81 || typeID == 82) {
                out.write(pwdLength);
                out.write(pwd.getBytes());
            } else {
                if (typeID == 92) {
                    out.write(friendLength);
                    out.write(friendName.getBytes());
                }
                out.write(msgLength);
                out.write(msg.getBytes());
                out.flush();
            }
            System.out.println("发送成功: " + this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
