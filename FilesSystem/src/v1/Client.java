package v1;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            OutputStream os = socket.getOutputStream();
            JFileChooser chooser = new JFileChooser();
            //
            chooser.showOpenDialog(null);
            File file = chooser.getSelectedFile();
            // ... existing code ...
            String filename = file.getName();
// 使用UTF-8编码转换文件名
            byte[] namebytes = filename.getBytes(StandardCharsets.UTF_8);
// 注意：需要修改长度的传输方式，因为UTF-8是变长编码
// 先发送文件名的字节数组长度，而不是字符串长度
            os.write(namebytes.length);
            os.write(namebytes);
// ... existing code ...
            FileInputStream fis = new FileInputStream(file);
            while (true) {
                byte[] buffer = new byte[1024];
                fis.read(buffer);
                os.write(buffer);
                os.flush();
                if(fis.available() == 0){
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
