package org.TCPUDP.client.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

class Receiver {
    ClientListen clientListen;
    JFrame jf;
    public void start(String name,int port) {
        jf = new JFrame(name+"收到的视频");
        jf.setSize(400, 400);
        jf.setDefaultCloseOperation(2);//关闭窗口时，结束线程
        jf.setLocationRelativeTo(null);
        jf.setLayout(new BorderLayout());
        jf.setVisible(true);
        JButton Close=new JButton("挂断视频");
        Close.setPreferredSize(new Dimension(0,60));
        Close.addActionListener(clientListen);
        jf.add(Close,BorderLayout.SOUTH);
        Graphics g = jf.getGraphics();
        DatagramSocket receiver = null;
        try {
            receiver = new DatagramSocket(port);
            receiveThread(g,receiver);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
public void setjfclose(){
        jf.setVisible(false);
}
    public static void receiveThread(Graphics g,DatagramSocket receiver) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] imgbytes=new byte[10000];
                    DatagramPacket packet=new DatagramPacket(imgbytes, imgbytes.length);
                    try {
                        receiver.receive(packet);
                        imgbytes=packet.getData();
                        BufferedImage img= ImageIO.read(new ByteArrayInputStream(imgbytes));
                        g.drawImage(img,0,0,null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}