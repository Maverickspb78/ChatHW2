package server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Component
@Scope("prototype")
public class ClientHandler implements Runnable, Closeable {

    private DbController dbController;
    private CommandController commandController;
    private EchoServer server;
    private Socket socket;
    private static int cnt = 0;
    private String userName;
    private String login;
    private DataInputStream is;
    private DataOutputStream os;
    private final byte[] buffer;
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class.getName());

    public ClientHandler(){
        cnt++;
        userName = "not_authorized#" + cnt;
        buffer = new byte[256];
    }

    @Autowired
    public void setDbController(DbController dbController) {
        this.dbController = dbController;
    }

    @Autowired
    public void setCommandController(CommandController commandController) {
        this.commandController = commandController;
    }

    @Autowired
    public void setServer(EchoServer server) {
        this.server = server;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void run() {
        socket = server.getSocket();
        try {
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                int bytesRead = is.read(buffer);
                if (bytesRead == -1) {
                    server.kickMe(this);
                    server.broadCast("Client " + userName + " leave!" + "\n\r");
                    LOGGER.info("SERVER: CLIENT LEAVE");
                    break;
                }
                String messageFromClient = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                if (messageFromClient.replaceAll("[\n\r]", "").isEmpty()) {
                    continue;
                }
                if (messageFromClient.startsWith("/")) {
                    LOGGER.info("CLIENT SEND COMMAND: " + messageFromClient);
                    if (!messageFromClient.startsWith("/private")) {
                        server.broadCast(commandController.giveAnswer(messageFromClient, this, server));
                    } else {
                        commandController.giveAnswer(messageFromClient, this, server);
                    }
                    continue;
                }
                LOGGER.info("Received from " + userName + ": " + messageFromClient);
                server.broadCast(userName + ": " + messageFromClient + "\n\r");
            } catch (IOException | SQLException e) {
                LOGGER.info("Exception while read");
                break;
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public void changeName(String userName) {
        this.userName = userName;
    }

    public void sendMessage(String message) throws IOException {
        os.write(message.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    @Override
    public void close() throws IOException {
        os.close();
        is.close();
    }
}