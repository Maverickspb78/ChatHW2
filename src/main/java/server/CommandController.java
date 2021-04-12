package server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Scope("prototype")
public class CommandController {
    DbController db;
    ConcurrentLinkedDeque<ClientHandler> clTmp;
    private static final Logger LOGGER = LogManager.getLogger(CommandController.class.getName());


    public CommandController(DbController db) {
        this.db = db;
    }

    public String giveAnswer(String clientMessage, ClientHandler clientHandler, EchoServer server) throws IOException, SQLException {
        String[] tmp = clientMessage.split(" ");
        String oldName = clientHandler.getUserName();
        String command = tmp[0];
        clientMessage = clientMessage.substring(command.length()).trim();
        switch (command) {

            case "/auth": {
                if (db.isAuthSuccess(tmp[1].trim(), tmp[2].trim())) {
                    clientHandler.setLogin(tmp[1].trim());
                    clientHandler.changeName(db.getNickNameFromDb(tmp[1]));
                    server.addUser(clientHandler);
                    clientHandler.sendMessage("/success");
                    clientMessage = "--- " + clientHandler.getUserName() + " --- Joined to Chat! \n";
                } else {
                    clientHandler.sendMessage("/NOsuccess");
                    clientMessage = "";
                }
                break;
            }
            case "/reg": {
                tmp = clientMessage.split("\\*");
                clientHandler.setLogin(tmp[0].trim());
                db.updateUser(tmp[0], tmp[1], tmp[2]);
                clientHandler.changeName(tmp[2]);
                clientHandler.sendMessage("/success");
                clientMessage = "--- " + clientHandler.getUserName() + " --- Joined to Chat! \n";
                break;
            }
            case "/changename": {

                clientHandler.changeName(clientMessage);
                clientMessage = "*** " + oldName + " - change name to " + clientMessage + "*** \n";
                break;
            }
            case "/private": {
                String nameTo = tmp[1].trim();
                clientMessage = clientMessage.substring(nameTo.length());
                clTmp = server.getClients();
                for (ClientHandler client : clTmp) {
                    if (client.getUserName().equals(nameTo) || client.getUserName().equals(clientHandler.getUserName())) {
                        client.sendMessage("PRIVAT MESSAGE!!! FROM " + clientHandler.getUserName() + ": " + clientMessage + "\n");
                    }
                }
                break;
            }
            case "/quit": {
                LOGGER.info("SERVER: CLIENT LEAVE");
                clientHandler.sendMessage("quit - accept");
                server.kickMe(clientHandler);
                server.broadCast("Client " + clientHandler.getUserName() + " leave!" + "\n\r");
                break;
            }
            case "/online": {
                clTmp = server.getClients();
                StringBuilder online = new StringBuilder();
                online.append("NOW ONLINE: \n");
                for (ClientHandler client : clTmp) {
                    online.append(client.getUserName()).append("\n");
                }
                for (ClientHandler client : clTmp) {
                    if (client.getUserName().equals(clientHandler.getUserName())) {
                        client.sendMessage(online + "\n");
                    }

                }
                break;
            }
            default: {
                clTmp = server.getClients();
                for (ClientHandler client : clTmp) {
                    if (client.getUserName().equals(clientHandler.getUserName())) {
                        client.sendMessage("All commands:\n /changename .. - change your name \n/private .. - privat message" +
                                "\n/online .. - Who is online\n");
                    }

                }
                break;
            }
        }
        return clientMessage;
    }


}
