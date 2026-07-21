package org.UDPtest;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Sender {

    public static void main(String[] args) throws TimeoutException {
        DatagramSocket sender = null;
        try {
            sender = new DatagramSocket(0);
            WebcamDiscoveryService dicS = Webcam.getDiscoveryService();
            dicS.scan();
            List<Webcam> webcams = dicS.getWebcams(10l, TimeUnit.SECONDS);
            Webcam webcam = webcams.get(0);
            webcam.setViewSize(new Dimension(320, 240));
            webcam.open();
            sendThread(webcam, sender);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendThread(Webcam webcam, DatagramSocket sender) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    BufferedImage img = webcam.getImage();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(img, "jpg", baos);
                        byte[] imgbytes = baos.toByteArray();
                        //目标地址，端口号封装为网络地址
                        InetSocketAddress receiveraddress = new InetSocketAddress(InetAddress.getByName("localhost"), 10086);
                        //数据，地址打包
                        DatagramPacket hi = new DatagramPacket(imgbytes, imgbytes.length, receiveraddress);
                        sender.send(hi);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}

class Receiver {
    public static void main(String[] args) {
        JFrame jf = new JFrame("收到的视频");
        jf.setSize(400, 400);
        jf.setDefaultCloseOperation(3);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        Graphics g = jf.getGraphics();
        DatagramSocket receiver = null;
        try {
            receiver = new DatagramSocket(10086);
            receiveThread(g,receiver);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void receiveThread(Graphics g,DatagramSocket receiver) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] imgbytes=new byte[12000];
                    DatagramPacket packet=new DatagramPacket(imgbytes, imgbytes.length);
                    try {
                        receiver.receive(packet);
                        imgbytes=packet.getData();
                        BufferedImage img=ImageIO.read(new ByteArrayInputStream(imgbytes));
                        g.drawImage(img,0,0,null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}