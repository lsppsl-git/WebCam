package org.Wecam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebcamTest {
    public static void main(String[] args) throws TimeoutException {
        JFrame jf = new JFrame("webcam测试");
        jf.setSize(700, 700);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        Graphics g = jf.getGraphics();
        WebcamDiscoveryService dicS = Webcam.getDiscoveryService();
        dicS.scan();
        List<Webcam> webcams = dicS.getWebcams(10l, TimeUnit.SECONDS);
        for(Webcam webcam:webcams){
            System.out.println(webcam.getName());
        }
        Webcam webcam=webcams.get(0);
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    BufferedImage img=webcam.getImage();
                    g.drawImage(img,0,0,null);
                }
            }
        }).start();
    }
}
