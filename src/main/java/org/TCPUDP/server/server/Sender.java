package org.TCPUDP.server.server;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Sender {
    static Webcam webcam;
    static DatagramSocket sender;

    public void start() throws TimeoutException {
        try {
            sender = new DatagramSocket(0);
            WebcamDiscoveryService dicS = Webcam.getDiscoveryService();
            dicS.scan();
            List<Webcam> webcams = dicS.getWebcams(10l, TimeUnit.SECONDS);
            if (webcams != null && !webcams.isEmpty()) {
                // 你原来的逻辑
                webcam = webcams.get(0);
                webcam.setViewSize(new Dimension(320, 240));
                webcam.open();
            } else {
                System.out.println("未找到摄像头设备");
                // 可以选择抛出异常或者返回，避免后续代码执行
                throw new TimeoutException("未找到摄像头设备");
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public  void sendThread( int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (webcam != null && webcam.isOpen()) {
                        BufferedImage img = webcam.getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(img, "jpg", baos);
                            byte[] imgbytes = baos.toByteArray();
                            //目标地址，端口号封装为网络地址
                            InetSocketAddress receiveraddress = new InetSocketAddress(InetAddress.getByName("localhost"), port);
                            //数据，地址打包
                            DatagramPacket hi = new DatagramPacket(imgbytes, imgbytes.length, receiveraddress);
                            sender.send(hi);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();
    }
}