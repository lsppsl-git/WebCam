package v1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();
            InputStream in = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(in);
            int nameLen = bis.read();
            byte[] namebytes = new byte[nameLen];
            bis.read(namebytes);
            String filename = new String(namebytes);
            File file = new File("files" + "/" + filename);
            // 确保父目录存在
//            File parentDir = file.getParentFile();
//            if (parentDir != null && !parentDir.exists()) {
//                parentDir.mkdirs();
//            }
            if (!file.exists()) {
                file.createNewFile();
            }

            OutputStream out = new FileOutputStream(file);
            while (true) {
                byte[] buf = new byte[1024];
                bis.read(buf);
                out.write(buf);
                out.flush();
                if (bis.available() == 0) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}