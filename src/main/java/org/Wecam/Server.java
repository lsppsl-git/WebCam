package org.Wecam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
    static ServerSocket serverSocket = null;

    public static void main(String[] args) throws TimeoutException {
        JFrame jf = new JFrame("服务端");
        jf.setSize(400, 300);
        jf.setDefaultCloseOperation(3);
        jf.setVisible(true);
        Graphics g = jf.getGraphics();
        try {
            serverSocket = new ServerSocket(10086);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        WebcamDiscoveryService dicS = Webcam.getDiscoveryService();
        dicS.scan();
        List<Webcam> webcams = dicS.getWebcams(10l, TimeUnit.SECONDS);
        for (Webcam webcam : webcams) {
            System.out.println(webcam.getName());
        }
        Webcam webcam = webcams.get(0);
        webcam.setViewSize(new Dimension(320, 240));
        webcam.open();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    BufferedImage img = webcam.getImage();
                    g.drawImage(img, 0, 0, null);
                }
            }
        }).start();
        //接收客户端
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket sck = serverSocket.accept();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DataOutputStream dos = null;
                                try {
                                    dos = new DataOutputStream(sck.getOutputStream());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                while (true) {
                                    BufferedImage img = webcam.getImage();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    try {
                                        ImageIO.write(img, "jpg", baos);
                                        byte[] data = baos.toByteArray();
                                        dos.writeInt(data.length);
                                        dos.write(data);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }).start();
                    } catch (IOException E) {
                    }
                }
            }
        }).start();
    }
}
