package server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 @Component
public class EchoServer {

    private boolean running;
    private ConcurrentLinkedDeque<ClientHandler> clients = new ConcurrentLinkedDeque<>();
    private static final Logger LOGGER = LogManager.getLogger(EchoServer.class.getName());

    private Socket socket;
    private ClientHandler handler;

    public ConcurrentLinkedDeque<ClientHandler> getClients() {
        return clients;
    }

     @Autowired
    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }

    public void start() {
        running = true;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try(ServerSocket server = new ServerSocket(8189)) {
            LOGGER.info("Server started!");
            while (running) {
                LOGGER.info("Server is waiting connection");
                socket = server.accept();
                LOGGER.info("Client accepted!");
                executorService.submit(handler);
            }
        } catch (Exception e) {
            LOGGER.info("Server crashed");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void addUser(ClientHandler handler){
        clients.add(handler);
    }

    public void broadCast(String msg) throws IOException {
        if (msg.equals("")) return;
        for (ClientHandler client : clients) {
            client.sendMessage(msg);
        }
    }

    public void kickMe(ClientHandler client) {
        clients.remove(client);
    }

}
