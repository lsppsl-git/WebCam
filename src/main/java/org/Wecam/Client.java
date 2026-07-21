package org.Wecam;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException{
        JFrame jf = new JFrame("客户端");
        jf.setSize(400, 300);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        Graphics g = jf.getGraphics();
        Socket socket = null;
        try {
            socket = new Socket("localhost", 10086);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //收视频
                        int len = dis.readInt();
                        byte[] data = new byte[len];
                        dis.readFully(data);
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
                        g.drawImage(img, 0, 0, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
