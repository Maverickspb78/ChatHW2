package project;

import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


 @Component
public class Client implements Closeable{

    private static Client instance;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private byte[] buffer;
    private int bytesRead;
    private boolean flag;
    Thread readThread;

    public static Client getInstance() throws IOException {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }
    @Autowired
    public Client() throws IOException {
        this.socket = new Socket("localhost", 8189);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.buffer = new byte[256];
        this.flag = true;
    }



    public void write(String msg) {

        try {
            out.write(msg.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isAuthSuccess() throws IOException {
        bytesRead = in.read(buffer);
        String msg = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
        return msg.equals("/success");
    }

    public void readThreadStop(){
        flag = false;
    }
    public void readThreadStart(){
        flag = true;
        readThread.start();
    }

    public void read(TextArea output) throws IOException {
        System.out.println(flag);
        readThread = new Thread(() -> {
            while (flag) {
                try {
                    bytesRead = in.read(buffer);
                    String msg = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    output.appendText(msg);
                } catch (IOException e) {
                    System.out.println("User leave!");
                    output.appendText("Server crashed. Please Quit and Reconnect");
                    break;
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        flag = false;
        in.close();
        out.close();
    }
}


