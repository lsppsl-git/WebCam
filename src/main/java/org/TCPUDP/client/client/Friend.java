package org.TCPUDP.client.client;

import javax.swing.*;

public class Friend {
    private String name;
    private String status;
    private boolean online;
    private ImageIcon avatar;

    public Friend(String name, String status, boolean online, ImageIcon avatar) {
        this.name = name;
        this.status = status;
        this.online = online;
        this.avatar = avatar;
    }
    // Getters
    public String getName() { return name; }
    public String getStatus() { return status; }
    public boolean isOnline() { return online; }
    public ImageIcon getAvatar() { return avatar; }
}